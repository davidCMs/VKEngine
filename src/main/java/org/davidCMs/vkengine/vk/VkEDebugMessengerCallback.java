package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

@FunctionalInterface
public interface VkEDebugMessengerCallback {

	void invoke(VkEDebugMessageSeverity severity, VkEDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data);

}
