package org.davidCMs.vkengine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.vk.VkDebugMessageSeverity;
import org.davidCMs.vkengine.vk.VkDebugMessageType;
import org.davidCMs.vkengine.vk.VkDebugMessengerCallback;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

public class DefaultDebugMessengerCallback implements VkDebugMessengerCallback {
	private static final Logger log = LogManager.getLogger(DefaultDebugMessengerCallback.class);

	@Override
	public void invoke(VkDebugMessageSeverity severity, VkDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data) {
		String s = "[Vulkan] [" + type + "] " + data.pMessageString();

		switch (severity) {
			case VERBOSE -> log.debug(s);
			case INFO -> log.info(s);
			case WARNING -> log.warn(s);
			case ERROR -> log.error(s);
		}
	}
}
