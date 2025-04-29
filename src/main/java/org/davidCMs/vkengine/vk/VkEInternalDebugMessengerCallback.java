package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.EXTDebugUtils;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackEXT;

public class VkEInternalDebugMessengerCallback extends VkDebugUtilsMessengerCallbackEXT {

	private VkEDebugMessengerCallback callback;

	public VkEInternalDebugMessengerCallback(VkEDebugMessengerCallback callback) {
		this.callback = callback;
	}

	@Override
	public int invoke(int messageSeverity, int messageTypes, long pCallbackData, long pUserData) {
		VkDebugUtilsMessengerCallbackDataEXT data = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
		VkEDebugMessageSeverity severity = switch (messageSeverity) {
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> VkEDebugMessageSeverity.VERBOSE;
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> VkEDebugMessageSeverity.INFO;
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> VkEDebugMessageSeverity.WARNING;
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> VkEDebugMessageSeverity.ERROR;

			default -> throw new IllegalArgumentException("Unknown message severity: " + messageSeverity);
		};
		VkEDebugMessageType type = switch (messageTypes) {
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT -> VkEDebugMessageType.PERFORMANCE;
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT -> VkEDebugMessageType.VALIDATION;
			case EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT -> VkEDebugMessageType.GENERAL;

			default -> throw new IllegalArgumentException("Unknown message type: " + messageSeverity);
		};
		if (callback != null)
			callback.invoke(severity, type, data);
		else System.err.println("Failed to handle message from messenger because the messenger callback is not set.");

		return VK14.VK_FALSE;
	}

	public void setCallback(VkEDebugMessengerCallback callback) {
		this.callback = callback;
	}
}
