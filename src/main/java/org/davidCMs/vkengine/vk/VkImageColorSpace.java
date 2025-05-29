package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.KHRSurface;

public enum VkImageColorSpace {

	SRGB_NONLINEAR(KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)

	;

	final int bit;

	VkImageColorSpace(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public static VkImageColorSpace valueOf(int bit) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].bit == bit) return values()[i];
		}
		throw new IllegalArgumentException("No Value for bit: " + bit);
	}
}
