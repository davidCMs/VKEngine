package org.davidCMs.vkengine.vk;

import java.util.Collection;

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

	public static int getMaskOf(VkCommandPoolCreateFlags... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkCommandPoolCreateFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkCommandPoolCreateFlags> bits) {
		return getMaskOf(bits.toArray(new VkCommandPoolCreateFlags[0]));
	}

}
