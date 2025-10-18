package dev.davidCMs.vkengine.graphics.vk;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum VkCompositeAlpha {

	INHERIT(VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR),
	OPAQUE(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR),
	PRE_MULTIPLIED(VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR),
	POST_MULTIPLIED(VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR),

	;
	final int bit;

	VkCompositeAlpha(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkCompositeAlpha... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkCompositeAlpha bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkCompositeAlpha> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkCompositeAlpha bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkCompositeAlpha bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkCompositeAlpha> maskAsSet(long mask) {
	    Set<VkCompositeAlpha> set = new HashSet<>();
	    for (VkCompositeAlpha bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkCompositeAlpha valueOf(long bitVal) {
	    for (VkCompositeAlpha bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
