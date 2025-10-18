package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkAccess {

	COLOR_ATTACHMENT_READ(VK_ACCESS_2_COLOR_ATTACHMENT_READ_BIT),
	COLOR_ATTACHMENT_WRITE(VK_ACCESS_2_COLOR_ATTACHMENT_WRITE_BIT),
	HOST_READ(VK_ACCESS_2_HOST_READ_BIT),
	NONE(VK_ACCESS_2_NONE),
	HOST_WRITE(VK_ACCESS_2_HOST_WRITE_BIT),
	DEPTH_STENCIL_ATTACHMENT_READ(VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_READ_BIT),
	DEPTH_STENCIL_ATTACHMENT_WRITE(VK_ACCESS_2_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT),
	INDEX_READ(VK_ACCESS_2_INDEX_READ_BIT),
	INDIRECT_COMMAND_READ(VK_ACCESS_2_INDIRECT_COMMAND_READ_BIT),
	INPUT_ATTACHMENT_READ(VK_ACCESS_2_INPUT_ATTACHMENT_READ_BIT),
	MEMORY_READ(VK_ACCESS_2_MEMORY_READ_BIT),
	MEMORY_WRITE(VK_ACCESS_2_MEMORY_WRITE_BIT),
	SHADER_READ(VK_ACCESS_2_SHADER_READ_BIT),
	SHADER_SAMPLED_READ(VK_ACCESS_2_SHADER_SAMPLED_READ_BIT),
	SHADER_STORAGE_READ(VK_ACCESS_2_SHADER_STORAGE_READ_BIT),
	SHADER_STORAGE_WRITE(VK_ACCESS_2_SHADER_STORAGE_WRITE_BIT),
	SHADER_WRITE(VK_ACCESS_2_SHADER_WRITE_BIT),
	TRANSFER_READ(VK_ACCESS_2_TRANSFER_READ_BIT),
	TRANSFER_WRITE(VK_ACCESS_2_TRANSFER_WRITE_BIT),
	UNIFORM_READ(VK_ACCESS_2_UNIFORM_READ_BIT),
	VERTEX_ATTRIBUTE_READ(VK_ACCESS_2_VERTEX_ATTRIBUTE_READ_BIT),

	;

	final long bit;

	VkAccess(long bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkAccess... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkAccess bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkAccess> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkAccess bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkAccess bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkAccess> maskAsSet(long mask) {
	    Set<VkAccess> set = new HashSet<>();
	    for (VkAccess bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkAccess valueOf(long bitVal) {
	    for (VkAccess bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}
}
