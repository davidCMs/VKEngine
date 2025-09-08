package dev.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkCommandBufferUsageFlags {

	ONE_TIME_SUBMIT(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT),
	SIMULTANEOUS_USE(VK_COMMAND_BUFFER_USAGE_SIMULTANEOUS_USE_BIT),

	;

	final int bit;

	VkCommandBufferUsageFlags(int bit) {
		this.bit = bit;
	}

	public static int getMaskOf(VkCommandBufferUsageFlags... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkCommandBufferUsageFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkCommandBufferUsageFlags> bits) {
		return getMaskOf(bits.toArray(new VkCommandBufferUsageFlags[0]));
	}
}
