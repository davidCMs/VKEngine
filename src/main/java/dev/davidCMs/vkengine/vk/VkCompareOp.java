package dev.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkCompareOp {

	ALWAYS(VK_COMPARE_OP_ALWAYS),
	EQUAL(VK_COMPARE_OP_EQUAL),
	GREATER(VK_COMPARE_OP_GREATER),
	LESS(VK_COMPARE_OP_LESS),
	GREATER_OR_EQUAL(VK_COMPARE_OP_GREATER_OR_EQUAL),
	LESS_OR_EQUAL(VK_COMPARE_OP_LESS_OR_EQUAL),
	NEVER(VK_COMPARE_OP_NEVER),
	NOT_EQUAL(VK_COMPARE_OP_NOT_EQUAL),

	;

	final int bit;

	VkCompareOp(int bit) {
		this.bit = bit;
	}
}
