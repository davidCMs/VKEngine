package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public enum VkImageFormat {

	R8G8B8A8_SRGB(VK14.VK_FORMAT_R8G8B8A8_SRGB)
	;

	final int bit;

	VkImageFormat(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public static VkImageFormat valueOf(int bit) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].bit == bit) return values()[i];
		}
		throw new IllegalArgumentException("No Value for bit: " + bit);
	}
}
