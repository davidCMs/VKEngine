package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import org.lwjgl.vulkan.VK14;

public record VkImageView(
		long imageView,
		VkImage image,
		VkImageType viewType,
		VkFormat format,
		VkImageViewBuilder.ComponentOverrides components,
		VkImageSubresourceRangeBuilder subresourceRange
) implements Destroyable {

    @Override
	public void destroy() {
		VK14.vkDestroyImageView(image.device().device(), imageView, null);
	}

}
