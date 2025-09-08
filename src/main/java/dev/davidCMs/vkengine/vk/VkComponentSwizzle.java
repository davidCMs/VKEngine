package dev.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public enum VkComponentSwizzle {

	R(VK14.VK_COMPONENT_SWIZZLE_R),
	G(VK14.VK_COMPONENT_SWIZZLE_G),
	B(VK14.VK_COMPONENT_SWIZZLE_B),
	A(VK14.VK_COMPONENT_SWIZZLE_A),
	ONE(VK14.VK_COMPONENT_SWIZZLE_ONE),
	ZERO(VK14.VK_COMPONENT_SWIZZLE_ZERO),
	IDENTITY(VK14.VK_COMPONENT_SWIZZLE_IDENTITY)

	;

	final int bit;

	VkComponentSwizzle(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public static VkComponentSwizzle valueOf(int bit) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].bit == bit) return values()[i];
		}
		throw new IllegalArgumentException("No Value for bit: " + bit);
	}
}
