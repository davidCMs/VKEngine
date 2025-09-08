package dev.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;

public record VkImageView(
		long imageView,
		VkImage image,
		VkImageType viewType,
		VkImageFormat format,
		VkImageViewBuilder.ComponentOverrides components,
		VkImageSubresourceRangeBuilder subresourceRange
) {

	public void destroy() {
		VK14.vkDestroyImageView(image.device().device(), imageView, null);
	}

}
