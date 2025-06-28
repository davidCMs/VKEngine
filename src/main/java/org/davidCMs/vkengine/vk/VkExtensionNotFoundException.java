package org.davidCMs.vkengine.vk;

public class VkExtensionNotFoundException extends RuntimeException {
	public VkExtensionNotFoundException(String extension) {
		super("Could not find extension \"" + extension + "\"");
	}
}
