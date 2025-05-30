package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public record VkImageContext(VkDeviceContext device, long image, long imageView) {

	public void destroyImageView() {
		VK14.vkDestroyImageView(device.device(), imageView, null);
	}

	public void destroyImage() {
		VK14.vkDestroyImage(device.device(), image, null);
	}

	public void destroy() {
		destroyImageView();
		destroyImage();
	}
}
