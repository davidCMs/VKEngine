package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

@FunctionalInterface
public interface VkDebugMessengerCallback {

	void invoke(VkDebugMessageSeverity severity, VkDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data);

}
