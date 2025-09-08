package dev.davidCMs.vkengine.vk;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum VkCompositeAlpha {

	INHERIT(VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR),
	OPAQUE(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR),
	PRE_MULTIPLIED(VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR),
	POST_MULTIPLIED(VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR),

	;
	final int bit;

	VkCompositeAlpha(int bit) {
		this.bit = bit;
	}

	public static Set<VkCompositeAlpha> getFromMask(int mask) {
		Set<VkCompositeAlpha> set = new HashSet<>();
		for (int i = 0; i < values().length; i++) {
			VkCompositeAlpha alpha = values()[i];
			if ((alpha.bit & mask) != 0) set.add(alpha);
		}
		return set;
	}
}
