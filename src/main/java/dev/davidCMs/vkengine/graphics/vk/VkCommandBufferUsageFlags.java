package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkCommandBufferUsageFlags {

	ONE_TIME_SUBMIT(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT),
	SIMULTANEOUS_USE(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT),

	;

	final int bit;

	VkCommandBufferUsageFlags(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkCommandBufferUsageFlags... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkCommandBufferUsageFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkCommandBufferUsageFlags> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkCommandBufferUsageFlags bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkCommandBufferUsageFlags bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkCommandBufferUsageFlags> maskAsSet(long mask) {
	    Set<VkCommandBufferUsageFlags> set = new HashSet<>();
	    for (VkCommandBufferUsageFlags bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkCommandBufferUsageFlags valueOf(long bitVal) {
	    for (VkCommandBufferUsageFlags bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
