package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.LogUtils;
import org.joml.Vector2dKt;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkBufferViewCreateInfo;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

public record VkDescriptorSet(long set) {

    private final static TaggedLogger log = Logger.tag("Vulkan");

    public static void uploadBuffer(VkDescriptorSet[] set, int[] binding, int[] arrayElement, VkDescriptorType[] descriptorType, VkBuffer[] buffer, long[] offset, long[] range) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkWriteDescriptorSet.Buffer write = VkWriteDescriptorSet.malloc(set.length, stack);
            for (int i = 0; i < set.length; i++) {
                write.get(i).sType$Default()
                        .dstSet(set[i].set)
                        .dstBinding(binding[i])
                        .dstArrayElement(arrayElement[i])
                        .descriptorType(descriptorType[i].bit)
                        .descriptorCount(set.length)
                        .pBufferInfo(
                                VkDescriptorBufferInfo.malloc(1, stack)
                                        .buffer(buffer[i].getBuffer())
                                        .offset(offset[i])
                                        .range(range[i])
                        );
            }
            VK14.vkUpdateDescriptorSets(buffer[0].getDevice().device(), write, null);
        }
    }

    public static void uploadBuffer(VkDescriptorSet set, int binding, int arrayElement, VkDescriptorType descriptorType, VkBuffer buffer, long offset, long range) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkDescriptorBufferInfo.Buffer bufferInfoBuf = VkDescriptorBufferInfo.calloc(1, stack);
            VkDescriptorBufferInfo bufferInfo = bufferInfoBuf.get(0);
            bufferInfo
                    .buffer(buffer.getBuffer())
                    .offset(offset)
                    .range(range);

            VkWriteDescriptorSet.Buffer writeBuf = VkWriteDescriptorSet.calloc(1, stack);
            VkWriteDescriptorSet write = writeBuf.get(0);
            write
                    .sType$Default()
                    .dstSet(set.set)
                    .dstBinding(binding)
                    .dstArrayElement(arrayElement)
                    .descriptorType(descriptorType.bit)
                    .descriptorCount(1)
                    .pBufferInfo(bufferInfoBuf);

            log.info("""
                    device: {}
                    set: {}
                    binding: {}
                    arrayElement: {}
                    descriptorType: {}
                    buffer: {}
                    offset: {}
                    range {}
                    """,
                    LogUtils.asHex(buffer.getDevice().device().address()),
                    LogUtils.asHex(set.set),
                    binding,
                    arrayElement,
                    descriptorType,
                    LogUtils.asHex(buffer.getBuffer()),
                    offset,
                    range);

            VK14.vkUpdateDescriptorSets(buffer.getDevice().device(), writeBuf, null);
        }
    }

    public void uploadBuffer(int binding, int arrayElement, VkDescriptorType descriptorType, VkBuffer buffer, long offset, long range) {
        VkDescriptorSet.uploadBuffer(this, binding, arrayElement, descriptorType, buffer, offset, range);
    }

    public void uploadBuffer(int binding, int arrayElement, VkDescriptorType descriptorType, VkBuffer buffer) {
        VkDescriptorSet.uploadBuffer(this, binding, arrayElement, descriptorType, buffer, 0, VK14.VK_WHOLE_SIZE);
    }

}
