package dev.davidCMs.vkengine.graphics.vk;

public class VkDeviceExtensionNotAvailableException extends RuntimeException {
    public VkDeviceExtensionNotAvailableException(VkDeviceExtension extension) {
        super("Extension: \"" + extension.name + "\" is missing but it is required");
    }
}
