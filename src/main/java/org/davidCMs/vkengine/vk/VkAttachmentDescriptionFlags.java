package org.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkAttachmentDescriptionFlags {

	MAY_ALIAS(VK_ATTACHMENT_DESCRIPTION_MAY_ALIAS_BIT)

	;

	final int bit;

	VkAttachmentDescriptionFlags(int bit) {
		this.bit = bit;
	}

	public static int getMaskOf(VkAttachmentDescriptionFlags... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkAttachmentDescriptionFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkAttachmentDescriptionFlags> bits) {
		return getMaskOf(bits.toArray(new VkAttachmentDescriptionFlags[0]));
	}
}
