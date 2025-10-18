package dev.davidCMs.vkengine.graphics.vk;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum VkSurfaceTransform {

	HORIZONTAL_MIRROR(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_BIT_KHR),
	IDENTITY(VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR),
	INHERIT(VK_SURFACE_TRANSFORM_INHERIT_BIT_KHR),
	ROTATE_90(VK_SURFACE_TRANSFORM_ROTATE_90_BIT_KHR),
	ROTATE_180(VK_SURFACE_TRANSFORM_ROTATE_180_BIT_KHR),
	ROTATE_270(VK_SURFACE_TRANSFORM_ROTATE_270_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_90(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_90_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_180(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_180_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_270(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_270_BIT_KHR)

	;

	final int bit;

	VkSurfaceTransform(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkSurfaceTransform... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkSurfaceTransform bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkSurfaceTransform> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkSurfaceTransform bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkSurfaceTransform bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkSurfaceTransform> maskAsSet(long mask) {
	    Set<VkSurfaceTransform> set = new HashSet<>();
	    for (VkSurfaceTransform bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkSurfaceTransform valueOf(long bitVal) {
	    for (VkSurfaceTransform bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}

}
