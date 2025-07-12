package org.davidCMs.vkengine.vk;

import java.util.Collection;

import static org.lwjgl.vulkan.VK14.*;

public enum VkDescriptorSetLayoutCreateFlags {

	UPDATE_AFTER_BIND_POOL(VK_DESCRIPTOR_SET_LAYOUT_CREATE_UPDATE_AFTER_BIND_POOL_BIT),
	PUSH_DESCRIPTOR(VK_DESCRIPTOR_SET_LAYOUT_CREATE_PUSH_DESCRIPTOR_BIT)

	;

	final int bit;

	VkDescriptorSetLayoutCreateFlags(int bit) {
		this.bit = bit;
	}

	public static int getMaskOf(VkDescriptorSetLayoutCreateFlags... bits) {
		if (bits == null) return 0;

		int sum = 0;
		for (VkDescriptorSetLayoutCreateFlags bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkDescriptorSetLayoutCreateFlags> bits) {
		return getMaskOf(bits.toArray(new VkDescriptorSetLayoutCreateFlags[0]));
	}
}
