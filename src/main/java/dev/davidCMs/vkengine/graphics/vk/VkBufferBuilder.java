package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderSet;
import dev.davidCMs.vkengine.graphics.vma.VmaAllocationBuilder;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationInfo;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferUsageFlags2CreateInfo;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class VkBufferBuilder {

    private VmaAllocationBuilder allocationBuilder;
    private long size;
    private final BuilderSet<VkBufferBuilder,VkBufferCreateFlags> flags = new BuilderSet<>(this);
    private final BuilderSet<VkBufferBuilder,VkBufferUsageFlags> usage = new BuilderSet<>(this);
    private final BuilderSet<VkBufferBuilder, VkQueueFamily> queueFamilies = new BuilderSet<>(this);

    public VkBuffer build(VkDeviceContext device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack);
            info.sType$Default();
            info.pNext(VkBufferUsageFlags2CreateInfo.calloc(stack)
                    .sType$Default()
                    .usage(VkBufferUsageFlags.getMaskOf(usage)));
            info.flags((int) VkBufferCreateFlags.getMaskOf(flags));
            info.size(size);
            if (queueFamilies.size() > 1) {
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

            LongBuffer lb = stack.mallocLong(1);
            PointerBuffer pb = stack.mallocPointer(1);
            VmaAllocationInfo allocInfo = VmaAllocationInfo.malloc(stack);

            int err;
            err = Vma.vmaCreateBuffer(device.allocator(), info, allocationBuilder.build(stack), lb, pb, allocInfo);//line 53
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to create a vulkan buffer: " + VkUtils.translateErrorCode(err));

            return new VkBuffer(lb.get(0),
                    device,
                    usage.copyAsImmutableSet(),
                    pb.get(0),
                    size,
                    allocInfo
            );
        }
    }

    public long getSize() {
        return size;
    }

    public VkBufferBuilder setSize(long size) {
        this.size = size;
        return this;
    }

    public BuilderSet<VkBufferBuilder, VkBufferCreateFlags> flags() {
        return flags;
    }

    public BuilderSet<VkBufferBuilder, VkBufferUsageFlags> usage() {
        return usage;
    }

    public BuilderSet<VkBufferBuilder, VkQueueFamily> queueFamilies() {
        return queueFamilies;
    }

    public VmaAllocationBuilder getAllocationBuilder() {
        return allocationBuilder;
    }

    public VkBufferBuilder setAllocationBuilder(VmaAllocationBuilder allocationBuilder) {
        this.allocationBuilder = allocationBuilder;
        return this;
    }
}
