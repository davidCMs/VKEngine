package dev.davidCMs.vkengine.graphics.vk;

public class VkDeviceExtensionNotAvailableException extends RuntimeException {
    public VkDeviceExtensionNotAvailableException(VkDeviceExtension extension) {
        super("Device extension: \"" + extension.name + "\" is missing but it is required");
    }
}
