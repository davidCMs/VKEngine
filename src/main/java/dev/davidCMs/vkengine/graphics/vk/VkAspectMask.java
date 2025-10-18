package dev.davidCMs.vkengine.graphics.vk;

import static org.lwjgl.vulkan.VK14.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum VkAspectMask {

	COLOR(VK_IMAGE_ASPECT_COLOR_BIT),
	DEPTH(VK_IMAGE_ASPECT_DEPTH_BIT),
	STENCIL(VK_IMAGE_ASPECT_STENCIL_BIT),
	PLANE_0(VK_IMAGE_ASPECT_PLANE_0_BIT),
	PLANE_1(VK_IMAGE_ASPECT_PLANE_1_BIT),
	PLANE_2(VK_IMAGE_ASPECT_PLANE_2_BIT),
	METADATA(VK_IMAGE_ASPECT_METADATA_BIT)

	;

	final int bit;

	VkAspectMask(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkAspectMask... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkAspectMask bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkAspectMask> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkAspectMask bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkAspectMask bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkAspectMask> maskAsSet(long mask) {
	    Set<VkAspectMask> set = new HashSet<>();
	    for (VkAspectMask bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkAspectMask valueOf(long bitVal) {
	    for (VkAspectMask bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}

}
