package dev.davidCMs.vkengine;

import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.graphics.vk.VkDebugMessageSeverity;
import dev.davidCMs.vkengine.graphics.vk.VkDebugMessageType;
import dev.davidCMs.vkengine.graphics.vk.VkDebugMessengerCallback;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

public class DefaultDebugMessengerCallback implements VkDebugMessengerCallback {
	private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan Validation");

	@Override
	public void invoke(VkDebugMessageSeverity severity, VkDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data) {
		String s = "[" + type + "] " + data.pMessageString();

		switch (severity) {
			case VERBOSE -> log.debug(s);
			case INFO -> log.info(s);
			case WARNING -> log.warn(s);
			case ERROR -> log.error(s);
		}
	}
}
