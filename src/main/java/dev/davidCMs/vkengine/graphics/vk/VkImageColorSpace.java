package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.EXTSwapchainColorspace;
import org.lwjgl.vulkan.KHRSurface;

public enum VkImageColorSpace {

	SRGB_NONLINEAR(KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR),

	DISPLAY_P3_NONLINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_DISPLAY_P3_NONLINEAR_EXT),
	EXTENDED_SRGB_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_EXTENDED_SRGB_LINEAR_EXT),
	DISPLAY_P3_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_DISPLAY_P3_LINEAR_EXT),
	DCI_P3_NONLINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_DCI_P3_NONLINEAR_EXT),
	BT709_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_BT709_LINEAR_EXT),
	BT709_NONLINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_BT709_NONLINEAR_EXT),
	BT2020_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_BT2020_LINEAR_EXT),
	HDR10_ST2084_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_HDR10_ST2084_EXT),
	DOLBYVISION_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_DOLBYVISION_EXT),
	HDR10_HLG_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_HDR10_HLG_EXT),
	ADOBERGB_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_ADOBERGB_LINEAR_EXT),
	ADOBERGB_NONLINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_ADOBERGB_NONLINEAR_EXT),
	PASS_THROUGH_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_PASS_THROUGH_EXT),
	EXTENDED_SRGB_NONLINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_EXTENDED_SRGB_NONLINEAR_EXT),
	DCI_P3_LINEAR_EXT(EXTSwapchainColorspace.VK_COLOR_SPACE_DCI_P3_LINEAR_EXT),

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
