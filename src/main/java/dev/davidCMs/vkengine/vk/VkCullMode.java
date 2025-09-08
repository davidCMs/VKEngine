package dev.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkCullMode {

	NONE(VK_CULL_MODE_NONE),
	BACK(VK_CULL_MODE_BACK_BIT),
	FRONT(VK_CULL_MODE_FRONT_BIT),
	FRONT_AND_BACK(VK_CULL_MODE_FRONT_AND_BACK);

	;

	final int bit;

	VkCullMode(int bit) {
		this.bit = bit;
	}
}
