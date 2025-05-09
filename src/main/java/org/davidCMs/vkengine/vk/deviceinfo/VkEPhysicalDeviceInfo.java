package org.davidCMs.vkengine.vk.deviceinfo;

import org.davidCMs.vkengine.vk.VkEQueueFamily;
import org.lwjgl.vulkan.*;

import java.util.Set;

public record VkEPhysicalDeviceInfo(
		VkEPhysicalDeviceFeatures features,
		VkEPhysicalDeviceProperties properties,
		Set<VkEQueueFamily> queueFamilies
		) {

	public static VkEPhysicalDeviceInfo getFrom(VkPhysicalDevice device) {
		return new VkEPhysicalDeviceInfo(
				VkEPhysicalDeviceFeatures.getFrom(device),
				VkEPhysicalDeviceProperties.getFrom(device),
				VkEQueueFamily.getDeviceQueueFamilies(device)
		);
	}

}
