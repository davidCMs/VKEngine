package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferUsageFlags2CreateInfo;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkBufferBuilder {

    private Set<VkBufferCreateFlags> flags;
    private long size;
    private Set<VkBufferUsageFlags> usage;
    private Set<VkQueueFamily> queueFamilies;

    public VkBuffer build(VkDeviceContext device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack);
            info.sType$Default();
            info.pNext(VkBufferUsageFlags2CreateInfo.calloc(stack)
                    .sType$Default()
                    .usage(VkBufferUsageFlags.getMaskOf(usage)));
            info.flags(VkBufferCreateFlags.getMaskOf(flags));
            info.size(size);
            if (queueFamilies != null && !queueFamilies.isEmpty()) {
                info.sharingMode(VkSharingMode.CONCURRENT.bit);
                info.queueFamilyIndexCount(queueFamilies.size());
                IntBuffer ib = stack.callocInt(queueFamilies.size());
                int i = 0;
                for (VkQueueFamily queueFamily : queueFamilies) {
                    ib.put(i, queueFamily.getIndex());
                    i++;
                }
                info.pQueueFamilyIndices(ib);
            } else info.sharingMode(VkSharingMode.EXCLUSIVE.bit);

            LongBuffer lb = stack.mallocLong(0);

            int err;
            err = VK14.vkCreateBuffer(device.device(), info, null, lb);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to create a vulkan buffer: " + VkUtils.translateErrorCode(err));

            return new VkBuffer(lb.get(0), device, new HashSet<>(usage));
        }
    }

    public Set<VkBufferCreateFlags> getFlags() {
        return flags;
    }

    public VkBufferBuilder setFlags(Set<VkBufferCreateFlags> flags) {
        this.flags = flags;
        return this;
    }

    public long getSize() {
        return size;
    }

    public VkBufferBuilder setSize(long size) {
        this.size = size;
        return this;
    }

    public Set<VkBufferUsageFlags> getUsage() {
        return usage;
    }

    public VkBufferBuilder setUsage(Set<VkBufferUsageFlags> usage) {
        this.usage = usage;
        return this;
    }

    public Set<VkQueueFamily> getQueueFamilies() {
        return queueFamilies;
    }

    public VkBufferBuilder setQueueFamilies(Set<VkQueueFamily> queueFamilies) {
        this.queueFamilies = queueFamilies;
        return this;
    }
}
