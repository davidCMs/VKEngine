package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackEXT;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkViewport;

import java.util.Set;

public record VkInstanceContext(
		VkInstance instance,
		VkDebugUtilsMessengerCallbackEXT callback,
		VkInstanceBuilder builder,
		String applicationName,
		String engineName,
		VkVersion applicationVersion,
		VkVersion engineVersion,
		Set<VkLayer> enabledLayers,
		Set<VkExtension> enabledExtensions,
		VkDebugMessengerCallback messengerCallback,
		Set<VkDebugMessageSeverity> debugMessageSeverities,
		Set<VkDebugMessageType> debugMessageTypes
) {

	public VkInstanceContext(VkInstance instance, VkDebugUtilsMessengerCallbackEXT callback, VkInstanceBuilder builder) {
		this(instance, callback, builder,
				builder.getApplicationName(),
				builder.getEngineName(),
				builder.getApplicationVersion(),
				builder.getEngineVersion(),
				builder.enabledLayers().copyAsImmutableSet(),
				builder.enabledExtensions().copyAsImmutableSet(),
				builder.getMessengerCallback(),
				builder.debugMessageSeverities().copyAsImmutableSet(),
				builder.debugMessageTypes().copyAsImmutableSet());
	}

	public void destroy() {
		VK14.vkDestroyInstance(instance, null);
		callback.free();
	}

}
