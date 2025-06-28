package org.davidCMs.vkengine.vk;

public class VkLayerNotFoundException extends RuntimeException {
	public VkLayerNotFoundException(String layerName) {
		super("Could not find layer \"" + layerName + "\"");
	}
}
