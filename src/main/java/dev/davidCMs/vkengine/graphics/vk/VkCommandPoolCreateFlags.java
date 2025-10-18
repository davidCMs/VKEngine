package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkCommandPoolCreateFlags {

	PROTECTED(VK_COMMAND_POOL_CREATE_PROTECTED_BIT),
	RESET_COMMAND_BUFFER(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT),
	TRANSIENT(VK_COMMAND_POOL_CREATE_TRANSIENT_BIT)

	;

	final int bit;

	VkCommandPoolCreateFlags(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkCommandPoolCreateFlags... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkCommandPoolCreateFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkCommandPoolCreateFlags> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkCommandPoolCreateFlags bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkCommandPoolCreateFlags bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkCommandPoolCreateFlags> maskAsSet(long mask) {
	    Set<VkCommandPoolCreateFlags> set = new HashSet<>();
	    for (VkCommandPoolCreateFlags bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkCommandPoolCreateFlags valueOf(long bitVal) {
	    for (VkCommandPoolCreateFlags bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}

}
