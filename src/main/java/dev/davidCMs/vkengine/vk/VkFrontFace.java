package dev.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkFrontFace {

	CLOCKWISE(VK_FRONT_FACE_CLOCKWISE),
	COUNTER_CLOCKWISE(VK_FRONT_FACE_COUNTER_CLOCKWISE)

	;

	final int bit;

	VkFrontFace(int bit) {
		this.bit = bit;
	}
}
