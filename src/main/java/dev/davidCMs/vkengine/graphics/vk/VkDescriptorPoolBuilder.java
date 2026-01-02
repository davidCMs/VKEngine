package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderList;
import dev.davidCMs.vkengine.common.BuilderSet;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;

import java.nio.LongBuffer;

public class VkDescriptorPoolBuilder {

    public record VkPoolSize(VkDescriptorType type, int descriptorCount) {
        public VkDescriptorPoolSize toNative(MemoryStack stack) {
            return VkDescriptorPoolSize.malloc(stack)
                    .type(type.bit)
                    .descriptorCount(descriptorCount);
        }
    }

    private final BuilderSet<VkDescriptorPoolBuilder, VkDescriptionPoolCreateFlags> flags = new BuilderSet<>(this);
    private final BuilderList<VkDescriptorPoolBuilder, VkPoolSize> poolSizes = new BuilderList<>(this);
    private int maxSets;

    private VkDescriptorPoolSize.Buffer getBuffer(MemoryStack stack) {
        int i = 0;
        VkDescriptorPoolSize.Buffer buf = VkDescriptorPoolSize.calloc(poolSizes.size(), stack);
        for (VkPoolSize poolSize : poolSizes) {
            buf.put(i, poolSize.toNative(stack));
            i++;
        }
        return buf;
    }

    public VkDescriptorPool build(VkDeviceContext device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorPoolCreateInfo info = VkDescriptorPoolCreateInfo.calloc(stack);
            info.sType$Default();
            info.flags((int) VkDescriptionPoolCreateFlags.getMaskOf(flags.getSet()));
            info.pPoolSizes(getBuffer(stack));
            info.maxSets(maxSets);

            LongBuffer lb = stack.mallocLong(1);
            int err = VK14.vkCreateDescriptorPool(device.device(), info, null, lb);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to create a descriptor pool: " + VkUtils.translateErrorCode(err));

            return new VkDescriptorPool(lb.get(), device);
        }
    }

    public BuilderSet<VkDescriptorPoolBuilder, VkDescriptionPoolCreateFlags> flags() {
        return flags;
    }

    public BuilderList<VkDescriptorPoolBuilder, VkPoolSize> poolSizes() {
        return poolSizes;
    }

    public int maxSets() {
        return maxSets;
    }

    public VkDescriptorPoolBuilder setMaxSets(int maxSets) {
        this.maxSets = maxSets;
        return this;
    }
}
