package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import org.lwjgl.vulkan.VK14;

import java.util.Set;

public record VkImage(
		long image,
		VkDeviceContext device,
		VkImageType type,
		VkFormat format,
		int width,
		int height,
		int depth,
		int mipLevels,
		int arrayLayers,
		VkSampleCount samples,
		VkImageTiling tiling,
		Set<VkImageUsage> imageUsages,
		VkSharingMode sharingMode
) implements Destroyable {

    @Override
	public void destroy() {
		VK14.vkDestroyImage(device.device(), image, null);
	}

}
