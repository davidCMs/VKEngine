package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkAttachmentDescriptionFlags {

	MAY_ALIAS(VK_ATTACHMENT_DESCRIPTION_MAY_ALIAS_BIT)

	;

	final int bit;

	VkAttachmentDescriptionFlags(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkAttachmentDescriptionFlags... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkAttachmentDescriptionFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkAttachmentDescriptionFlags> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkAttachmentDescriptionFlags bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkAttachmentDescriptionFlags bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkAttachmentDescriptionFlags> maskAsSet(long mask) {
	    Set<VkAttachmentDescriptionFlags> set = new HashSet<>();
	    for (VkAttachmentDescriptionFlags bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkAttachmentDescriptionFlags valueOf(long bitVal) {
	    for (VkAttachmentDescriptionFlags bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
