package dev.davidCMs.vkengine.graphics.vk;

public class VkExtensionNotDefined extends RuntimeException {
	public VkExtensionNotDefined(String extension) {
		super("Extension \"" + extension + "\" was not defined in VkExtension enum");
	}
}
