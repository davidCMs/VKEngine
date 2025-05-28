package org.davidCMs.vkengine.vk;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public record VkPhysicalDeviceSwapChainInfo(
		SurfaceCapabilities surfaceCapabilities,
		Set<SurfaceFormat> surfaceFormats,
		Set<VkPresentMode> presentModes
) {

	public static VkPhysicalDeviceSwapChainInfo getFrom(VkPhysicalDevice physicalDevice, long surface) {
		return new VkPhysicalDeviceSwapChainInfo(
				SurfaceCapabilities.getFrom(physicalDevice, surface),
				SurfaceFormat.getFrom(physicalDevice, surface),
				VkPresentMode.getFrom(physicalDevice, surface)
		);
	}

	public boolean supportsFormat(int format) {
		for (SurfaceFormat f : surfaceFormats) {
			if (f.format == format) return true;
		}
		return false;
	}

	public boolean supportsColorSpace(int imageColorSpace) {
		for (SurfaceFormat f : surfaceFormats) {
			if (f.colorSpace == imageColorSpace) return true;
		}
		return false;
	}

	public record SurfaceCapabilities(
			int minImageCount,
			int maxImageCount,
			Vector2i minImageExtent,
			Vector2i maxImageExtent,
			Vector2i currentExtent,
			int maxImageArrayLayers,
			Set<VkSurfaceTransform> supportedTransforms,
			Set<VkCompositeAlpha> supportedCompositeAlpha,
			Set<VkImageUsage> supportedUsage
	) {

		public static SurfaceCapabilities getFrom(VkPhysicalDevice physicalDevice, long surface) {
			try (MemoryStack stack = MemoryStack.stackPush()) {

				VkSurfaceCapabilitiesKHR surfaceCapabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
				int result;
				result = vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice, surface, surfaceCapabilities);
				if (result != VK14.VK_SUCCESS)
					throw new RuntimeException("Failed to get surface capabilities err: " + result);

				return new SurfaceCapabilities(
						surfaceCapabilities.minImageCount(),
						surfaceCapabilities.maxImageCount(),
						new Vector2i(
								surfaceCapabilities.minImageExtent().width(),
								surfaceCapabilities.minImageExtent().height()
						),
						new Vector2i(
								surfaceCapabilities.maxImageExtent().width(),
								surfaceCapabilities.maxImageExtent().height()
						),
						new Vector2i(
								surfaceCapabilities.currentExtent().width(),
								surfaceCapabilities.currentExtent().height()
						),
						surfaceCapabilities.maxImageArrayLayers(),
						VkSurfaceTransform.getFromMask(surfaceCapabilities.supportedTransforms()),
						VkCompositeAlpha.getFromMask(surfaceCapabilities.supportedCompositeAlpha()),
						VkImageUsage.getFromMask(surfaceCapabilities.supportedUsageFlags())
				);
			}
		}

	}

	public record SurfaceFormat(int colorSpace, int format) {

		public static Set<SurfaceFormat> getFrom(VkPhysicalDevice physicalDevice, long surface) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer count = stack.callocInt(1);
				int result = vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, count, null);
				if (result != VK14.VK_SUCCESS)
					throw new RuntimeException("Could not query surface formats");
				VkSurfaceFormatKHR.Buffer buf = VkSurfaceFormatKHR.calloc(count.get(0), stack);
				vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice, surface, count, buf);

				Set<SurfaceFormat> formats = new HashSet<>();
				for (int i = 0; i < count.get(0); i++) {
					formats.add(new SurfaceFormat(
							buf.get(i).colorSpace(),
							buf.get().format()
					));
				}
				return formats;
			}
		}
	}


}
