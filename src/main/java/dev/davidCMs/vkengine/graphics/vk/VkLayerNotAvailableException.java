package dev.davidCMs.vkengine.graphics.vk;

public class VkLayerNotAvailableException extends RuntimeException {
	public VkLayerNotAvailableException(VkLayer layer) {
		super("Extension: \"" + layer.name + "\" is missing but it is required");
	}
}
