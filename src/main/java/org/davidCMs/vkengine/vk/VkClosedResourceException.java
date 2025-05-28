package org.davidCMs.vkengine.vk;

public class VkClosedResourceException extends RuntimeException {
	public VkClosedResourceException() {
		this("This resource is closed and cannot be accessed");
	}

	public VkClosedResourceException(String message) {
		super(message);
	}
}
