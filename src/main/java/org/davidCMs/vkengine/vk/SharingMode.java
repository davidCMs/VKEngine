package org.davidCMs.vkengine.vk;

import static org.lwjgl.vulkan.VK14.*;

public enum SharingMode {

	CONCURRENT(VK_SHARING_MODE_CONCURRENT),
	EXCLUSIVE(VK_SHARING_MODE_EXCLUSIVE)

	;

	final int value;

	SharingMode(int value) {
		this.value = value;
	}
}
