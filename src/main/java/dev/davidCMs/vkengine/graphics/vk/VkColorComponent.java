package dev.davidCMs.vkengine.graphics.vk;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK14.*;

public enum VkColorComponent {

	R(VK_COLOR_COMPONENT_R_BIT),
	G(VK_COLOR_COMPONENT_G_BIT),
	B(VK_COLOR_COMPONENT_B_BIT),
	A(VK_COLOR_COMPONENT_A_BIT)

	;

	final int bit;

	VkColorComponent(int bit) {
		this.bit = bit;
	}

	public static long getMaskOf(VkColorComponent... bits) {
		if (bits == null) return 0;

		long sum = 0;
		for (VkColorComponent bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static long getMaskOf(Iterable<VkColorComponent> bits) {
	    if (bits == null) return 0;

	    long sum = 0;
	    for (VkColorComponent bit : bits) {
	        if (bit == null) continue;
	        sum |= bit.bit;
	    }
	    return sum;
	}

	public static boolean doesMaskHave(long mask, VkColorComponent bit) {
	    if (bit == null) return false;
	    return (mask & bit.bit) != 0;
	}

	public static Set<VkColorComponent> maskAsSet(long mask) {
	    Set<VkColorComponent> set = new HashSet<>();
	    for (VkColorComponent bit : values()) {
	        if ((bit.bit & mask) != 0) set.add(bit);
	    }
	    return set;
	}

	public static VkColorComponent valueOf(long bitVal) {
	    for (VkColorComponent bit : values()) {
	        if (bit.bit == bitVal) return bit;
	    }
	    throw new IllegalArgumentException("No Value for bit: " + bitVal);
	}

}
