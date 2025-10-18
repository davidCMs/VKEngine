package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.system.MemoryStack;

public record VkVertexInputAttributeDescription(
        int location,
        int binding,
        VkFormat format,
        int offset
) {

    public org.lwjgl.vulkan.VkVertexInputAttributeDescription toNative(MemoryStack stack) {
        return org.lwjgl.vulkan.VkVertexInputAttributeDescription.calloc(stack)
                .location(location)
                .binding(binding)
                .format(format.bit)
                .offset(offset);
    }

}
