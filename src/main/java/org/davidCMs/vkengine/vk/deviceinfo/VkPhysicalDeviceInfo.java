package org.davidCMs.vkengine.vk.deviceinfo;

import org.davidCMs.vkengine.vk.VkQueueFamily;
import org.lwjgl.vulkan.*;

import java.util.Set;

public record VkPhysicalDeviceInfo(
		VkPhysicalDeviceFeatures features,
		VkPhysicalDeviceProperties properties,
		Set<VkQueueFamily> queueFamilies
		) {

	public static VkPhysicalDeviceInfo getFrom(VkPhysicalDevice device) {
		return new VkPhysicalDeviceInfo(
				VkPhysicalDeviceFeatures.getFrom(device),
				VkPhysicalDeviceProperties.getFrom(device),
				VkQueueFamily.getDeviceQueueFamilies(device)
		);
	}

}
