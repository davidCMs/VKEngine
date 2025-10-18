package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.vulkan.VK14;

public record VkVersion(int variant, int major, int minor, int patch) {

	public VkVersion(int verInt) {
		this(
				VK14.VK_API_VERSION_VARIANT(verInt),
				VK14.VK_API_VERSION_MAJOR(verInt),
				VK14.VK_API_VERSION_MINOR(verInt),
				VK14.VK_API_VERSION_PATCH(verInt)
		);
	}

	public int makeVersion() {
		return VK14.VK_MAKE_API_VERSION(variant, major, minor, patch);
	}

}
