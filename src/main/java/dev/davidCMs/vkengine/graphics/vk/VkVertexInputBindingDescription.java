package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.system.MemoryStack;

public record VkVertexInputBindingDescription(
        int binding,
        int stride,
        VkVertexInputRate inputRate
) {

    public org.lwjgl.vulkan.VkVertexInputBindingDescription toNative(MemoryStack stack) {
        return org.lwjgl.vulkan.VkVertexInputBindingDescription.calloc(stack)
                .binding(binding)
                .stride(stride)
                .inputRate(inputRate.bit);
    }

}
