package dev.davidCMs.vkengine.graphics.vk;

public record VkMemoryRequirements(
        long size,
        long alignment,
        int memoryTypeBits
) {
}
