package dev.davidCMs.vkengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.davidCMs.vkengine.vk.VkDebugMessageSeverity;
import dev.davidCMs.vkengine.vk.VkDebugMessageType;
import dev.davidCMs.vkengine.vk.VkDebugMessengerCallback;
import dev.davidCMs.vkengine.vk.VulkanMessageFactory;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

public class DefaultDebugMessengerCallback implements VkDebugMessengerCallback {
	private static final Logger log = LogManager.getLogger(DefaultDebugMessengerCallback.class, VulkanMessageFactory.INSTANCE);

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
