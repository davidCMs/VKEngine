package dev.davidCMs.vkengine.graphics.vk;

import org.tinylog.TaggedLogger;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

@FunctionalInterface
public interface VkDebugMessengerCallback {

	void invoke(VkDebugMessageSeverity severity, VkDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data);

}
