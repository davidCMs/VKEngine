package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkImageUsage {

	COLOR_ATTACHMENT(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT),
	SAMPLED(VK_IMAGE_USAGE_SAMPLED_BIT),
	DEPTH_STENCIL(VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT),
	STORAGE(VK_IMAGE_USAGE_STORAGE_BIT),
	HOST_TRANSFER(VK_IMAGE_USAGE_HOST_TRANSFER_BIT),
	INPUT_ATTACHMENT(VK_IMAGE_USAGE_INPUT_ATTACHMENT_BIT),
	TRANSFER_DST(VK_IMAGE_USAGE_TRANSFER_DST_BIT),
	TRANSFER_SRC(VK_IMAGE_USAGE_TRANSFER_SRC_BIT),
	TRANSIENT_ATTACHMENT(VK_IMAGE_USAGE_TRANSIENT_ATTACHMENT_BIT)

	;

	final int bit;

	VkImageUsage(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkImageUsage... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkImageUsage bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkImageUsage> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkImageUsage bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkImageUsage bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkImageUsage> maskAsSet(long mask) {
	    Set<VkImageUsage> set = new HashSet<>();
	    for (VkImageUsage bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkImageUsage valueOf(long bitVal) {
	    for (VkImageUsage bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}

}
