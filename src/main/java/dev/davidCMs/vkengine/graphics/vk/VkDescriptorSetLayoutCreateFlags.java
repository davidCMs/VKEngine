package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkDescriptorSetLayoutCreateFlags {

	UPDATE_AFTER_BIND_POOL(VK_DESCRIPTOR_SET_LAYOUT_CREATE_UPDATE_AFTER_BIND_POOL_BIT),
	PUSH_DESCRIPTOR(VK_DESCRIPTOR_SET_LAYOUT_CREATE_PUSH_DESCRIPTOR_BIT)

	;

	final int bit;

	VkDescriptorSetLayoutCreateFlags(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkDescriptorSetLayoutCreateFlags... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkDescriptorSetLayoutCreateFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkDescriptorSetLayoutCreateFlags> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkDescriptorSetLayoutCreateFlags bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkDescriptorSetLayoutCreateFlags bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkDescriptorSetLayoutCreateFlags> maskAsSet(long mask) {
	    Set<VkDescriptorSetLayoutCreateFlags> set = new HashSet<>();
	    for (VkDescriptorSetLayoutCreateFlags bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkDescriptorSetLayoutCreateFlags valueOf(long bitVal) {
	    for (VkDescriptorSetLayoutCreateFlags bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
