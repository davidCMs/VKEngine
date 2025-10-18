package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.EXTDebugUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum VkDebugMessageSeverity {

	VERBOSE(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
	INFO(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
	WARNING(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
	ERROR(EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT)

	;

	final int bit;

	VkDebugMessageSeverity(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkDebugMessageSeverity... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkDebugMessageSeverity bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkDebugMessageSeverity> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkDebugMessageSeverity bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkDebugMessageSeverity bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkDebugMessageSeverity> maskAsSet(long mask) {
	    Set<VkDebugMessageSeverity> set = new HashSet<>();
	    for (VkDebugMessageSeverity bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkDebugMessageSeverity valueOf(long bitVal) {
	    for (VkDebugMessageSeverity bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
