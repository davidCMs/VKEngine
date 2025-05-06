package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public record VkEVersion(int variant, int major, int minor, int patch) {

	public int makeVersion() {
		return VK14.VK_MAKE_API_VERSION(variant, major, minor, patch);
	}

}
