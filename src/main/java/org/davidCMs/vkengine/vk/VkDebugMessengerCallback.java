package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;

@FunctionalInterface
public interface VkDebugMessengerCallback {

	VkDebugMessengerCallback defaultCallBack = ((severity, type, data) -> {
		String s = "[Vulkan] [" + type + "] [" + severity + "] " + data.pMessageString();

		switch (severity) {
			case INFO:
			case WARNING:
			case VERBOSE:
				System.out.println(s);
				break;
			case ERROR:
				System.err.println(s);
		}
	});

	void invoke(VkDebugMessageSeverity severity, VkDebugMessageType type, VkDebugUtilsMessengerCallbackDataEXT data);

}
