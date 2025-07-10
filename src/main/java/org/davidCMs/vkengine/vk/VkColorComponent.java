package org.davidCMs.vkengine.vk;

import java.util.Collection;

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

	static int getMaskOf(VkColorComponent... bits) {
		if (bits == null)
			return 0;

		int sum = 0;
		for (VkColorComponent bit : bits) {
			if (bit == null) continue;
			sum |= bit.bit;
		}
		return sum;
	}

	public static int getMaskOf(Collection<VkColorComponent> bits) {
		return getMaskOf(bits.toArray(new VkColorComponent[0]));
	}

}
