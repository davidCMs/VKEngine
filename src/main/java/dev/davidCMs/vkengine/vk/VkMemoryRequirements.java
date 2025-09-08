package dev.davidCMs.vkengine.vk;

public record VkMemoryRequirements(
        long size,
        long alignment,
        int memoryTypeBits
) {
}
