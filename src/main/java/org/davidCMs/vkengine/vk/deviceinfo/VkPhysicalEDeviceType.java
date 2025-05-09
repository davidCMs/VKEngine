package org.davidCMs.vkengine.vk.deviceinfo;

import org.lwjgl.vulkan.VK14;

public enum VkPhysicalEDeviceType {

	OTHER(VK14.VK_PHYSICAL_DEVICE_TYPE_OTHER),
	DISCRETE(VK14.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU),
	INTEGRATED(VK14.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU),
	VIRTUAL(VK14.VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU),
	CPU(VK14.VK_PHYSICAL_DEVICE_TYPE_CPU)

	;

	final int vkInt;

	VkPhysicalEDeviceType(int vkInt) {
		this.vkInt = vkInt;
	}

	public static VkPhysicalEDeviceType getType(int vkInt) {
		for (VkPhysicalEDeviceType t : values()) {
			if (t.vkInt == vkInt) return t;
		}

		throw new IllegalArgumentException("Could not find type that correlates to the vulkan int :" + vkInt);
	}
}
