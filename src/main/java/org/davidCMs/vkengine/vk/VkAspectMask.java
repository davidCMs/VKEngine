package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

import java.util.Collection;

public enum VkAspectMask {

	COLOR(VK14.VK_IMAGE_ASPECT_COLOR_BIT),
	DEPTH(VK14.VK_IMAGE_ASPECT_DEPTH_BIT),
	STENCIL(VK14.VK_IMAGE_ASPECT_STENCIL_BIT),
	PLANE_0(VK14.VK_IMAGE_ASPECT_PLANE_0_BIT),
	PLANE_1(VK14.VK_IMAGE_ASPECT_PLANE_1_BIT),
	PLANE_2(VK14.VK_IMAGE_ASPECT_PLANE_2_BIT),
	METADATA(VK14.VK_IMAGE_ASPECT_METADATA_BIT)

	;

	final int bit;

	VkAspectMask(int bit) {
		this.bit = bit;
	}

	static int getMaskOf(VkAspectMask... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkAspectMask bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkAspectMask> debugMessageTypes) {
		return getMaskOf(debugMessageTypes.toArray(new VkAspectMask[0]));
	}

}
