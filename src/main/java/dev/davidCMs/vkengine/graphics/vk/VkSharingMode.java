package dev.davidCMs.vkengine.graphics.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkSharingMode {

	CONCURRENT(VK_SHARING_MODE_CONCURRENT),
	EXCLUSIVE(VK_SHARING_MODE_EXCLUSIVE)

	;

	final int bit;

	VkSharingMode(int bit) {
		this.bit = bit;
	}
}
