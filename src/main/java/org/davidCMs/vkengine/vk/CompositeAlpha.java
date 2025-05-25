package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum CompositeAlpha {

	INHERIT(VK_COMPOSITE_ALPHA_INHERIT_BIT_KHR),
	OPAQUE(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR),
	PRE_MULTIPLIED(VK_COMPOSITE_ALPHA_PRE_MULTIPLIED_BIT_KHR),
	POST_MULTIPLIED(VK_COMPOSITE_ALPHA_POST_MULTIPLIED_BIT_KHR),

	;
	final int bit;

	CompositeAlpha(int bit) {
		this.bit = bit;
	}
}
