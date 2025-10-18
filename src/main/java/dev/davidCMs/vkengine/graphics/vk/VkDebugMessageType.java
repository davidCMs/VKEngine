package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum VkDebugMessageType {

	PERFORMANCE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT),
	VALIDATION(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
	GENERAL(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT)

	;

	final int bit;

	VkDebugMessageType(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkDebugMessageType... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkDebugMessageType bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkDebugMessageType> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkDebugMessageType bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkDebugMessageType bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkDebugMessageType> maskAsSet(long mask) {
	    Set<VkDebugMessageType> set = new HashSet<>();
	    for (VkDebugMessageType bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkDebugMessageType valueOf(long bitVal) {
	    for (VkDebugMessageType bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
