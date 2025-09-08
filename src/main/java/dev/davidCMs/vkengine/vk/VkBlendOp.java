package dev.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkBlendOp {

	ADD(VK_BLEND_OP_ADD),
	MAX(VK_BLEND_OP_MAX),
	MIN(VK_BLEND_OP_MIN),
	SUB(VK_BLEND_OP_SUBTRACT),
	REVERSE_SUB(VK_BLEND_OP_REVERSE_SUBTRACT)

	;

	final int bit;

	VkBlendOp(int bit) {
		this.bit = bit;
	}
}
