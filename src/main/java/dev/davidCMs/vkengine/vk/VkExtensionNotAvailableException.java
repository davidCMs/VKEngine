package dev.davidCMs.vkengine.vk;

public class VkExtensionNotAvailableException extends RuntimeException {
    public VkExtensionNotAvailableException(VkExtension extension) {
        super("Extension: \"" + extension.name + "\" is missing but it is required");
    }
}
