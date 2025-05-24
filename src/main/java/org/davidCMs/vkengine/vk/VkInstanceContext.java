package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackEXT;
import org.lwjgl.vulkan.VkInstance;

public record VkInstanceContext(
		VkInstance instance,
		VkDebugUtilsMessengerCallbackEXT callback,

		VkInstanceBuilder builder
) {

	public void destroy() {
		VK14.vkDestroyInstance(instance, null);
		callback.free();
	}

}
