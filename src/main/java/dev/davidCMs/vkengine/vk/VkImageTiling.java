package dev.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkImageTiling {
	LINEAR(VK_IMAGE_TILING_LINEAR),
	OPTIMAL(VK_IMAGE_TILING_OPTIMAL)

	;

	final int bit;

	VkImageTiling(int bit) {
		this.bit = bit;
	}
}
