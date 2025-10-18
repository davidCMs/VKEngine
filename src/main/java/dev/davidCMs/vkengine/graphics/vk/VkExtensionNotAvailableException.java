package dev.davidCMs.vkengine.graphics.vk;

public class VkExtensionNotAvailableException extends RuntimeException {
    public VkExtensionNotAvailableException(VkExtension extension) {
        super("Extension: \"" + extension.name + "\" is missing but it is required");
    }
}
