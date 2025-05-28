package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum VkSharingMode {

	CONCURRENT(VK_SHARING_MODE_CONCURRENT),
	EXCLUSIVE(VK_SHARING_MODE_EXCLUSIVE)

	;

	final int value;

	VkSharingMode(int value) {
		this.value = value;
	}
}
