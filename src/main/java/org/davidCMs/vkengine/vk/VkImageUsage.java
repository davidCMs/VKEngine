package org.davidCMs.vkengine.vk;

import java.util.Collection;

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

	public static int getValueOf(VkImageUsage... bits) {
		int sum = 0;
		for (VkImageUsage bit : bits) {
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getValueOf(Collection<VkImageUsage> bits) {
		return getValueOf(bits.toArray(bits.toArray(new VkImageUsage[0])));
	}

	public static boolean maskContains(int mask, VkImageUsage imageUsage) {
		return (mask & imageUsage.bit) == imageUsage.bit;
	}
}
