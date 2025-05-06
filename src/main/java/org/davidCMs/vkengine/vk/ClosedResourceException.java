package org.davidCMs.vkengine.vk;

public class ClosedResourceException extends RuntimeException {
	public ClosedResourceException() {
		this("This resource is closed and cannot be accessed");
	}

	public ClosedResourceException(String message) {
		super(message);
	}
}
