package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.util.LogUtils;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.nio.LongBuffer;

public record VkDescriptorPool(long pool, VkDeviceContext device) implements Destroyable {

    private final static TaggedLogger log = Logger.tag("Vulkan");

    public VkDescriptorSet[] allocate(VkDescriptorSetLayout... layouts) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.malloc(stack);
            allocateInfo.sType$Default();
            allocateInfo.descriptorPool(pool);

            LongBuffer longBuffer = stack.callocLong(layouts.length);
            for (int i = 0; i < layouts.length; i++)
                longBuffer.put(i, layouts[i].layout());

            allocateInfo.pSetLayouts(longBuffer);

            int err = VK14.vkAllocateDescriptorSets(device.device(), allocateInfo, longBuffer);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to allocate DescriptorSet: " + VkUtils.translateErrorCode(err));

            VkDescriptorSet[] sets = new VkDescriptorSet[layouts.length];
            for (int i = 0; i < layouts.length; i++) {
                sets[i] = new VkDescriptorSet(longBuffer.get(i));
            }

            return sets;
        }
    }

    public VkDescriptorSet allocate(VkDescriptorSetLayout layout) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorSetAllocateInfo allocateInfo = VkDescriptorSetAllocateInfo.malloc(stack);
            allocateInfo.sType$Default();
            allocateInfo.descriptorPool(pool);

            LongBuffer layoutBuf = stack.mallocLong(1);
            layoutBuf.put(0, layout.layout());

            allocateInfo.pSetLayouts(layoutBuf);

            LongBuffer descSet = stack.mallocLong(1);
            int err = VK14.vkAllocateDescriptorSets(device.device(), allocateInfo, descSet);
            if (err != VK14.VK_SUCCESS)
                throw new RuntimeException("Failed to allocate DescriptorSet: " + VkUtils.translateErrorCode(err));

            log.info("Allocated one {} layout from the {} pool at address {}",
                    LogUtils.asHex(layout.layout()),
                    LogUtils.asHex(pool),
                    LogUtils.asHex(descSet.get(0))
            );

            return new VkDescriptorSet(descSet.get(0));
        }
    }

    @Override
    public void destroy() {
        VK14.vkDestroyDescriptorPool(device.device(), pool, null);
    }
}
