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
		Set<PresentMode> presentModes
) {

	public static VkPhysicalDeviceSwapChainInfo getFrom(VkPhysicalDevice physicalDevice, long surface) {
		return new VkPhysicalDeviceSwapChainInfo(
				SurfaceCapabilities.getFrom(physicalDevice, surface),
				SurfaceFormat.getFrom(physicalDevice, surface),
				PresentMode.getFrom(physicalDevice, surface)
		);
	}

	public record SurfaceCapabilities(
			int minImageCount,
			int maxImageCount,
			Vector2i minImageExtent,
			Vector2i maxImageExtent,
			int maxImageArrayLayers,
			int supportedTransforms,
			int supportedCompositeAlpha,
			int supportedUsageFlags
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
						surfaceCapabilities.maxImageArrayLayers(),
						surfaceCapabilities.supportedTransforms(),
						surfaceCapabilities.supportedCompositeAlpha(),
						surfaceCapabilities.supportedUsageFlags()
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

	public enum PresentMode {
		IMMEDIATE(VK_PRESENT_MODE_IMMEDIATE_KHR),
		MAILBOX(VK_PRESENT_MODE_MAILBOX_KHR),
		FIFO(VK_PRESENT_MODE_FIFO_KHR),
		FIFO_RELAXED(VK_PRESENT_MODE_FIFO_RELAXED_KHR)

		;

		private int ord;

		PresentMode(int ord) {
			this.ord = ord;
		}

		public static Set<PresentMode> getFrom(VkPhysicalDevice physicalDevice, long surface) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				IntBuffer count = stack.callocInt(1);
				int result = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, count, null);
				if (result != VK14.VK_SUCCESS)
					throw new RuntimeException("Could not query presentation modes");
				IntBuffer ints = stack.callocInt(count.get(0));
				vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, count, ints);

				Set<PresentMode> set = new HashSet<>();
				for (int i = 0; i < count.get(0); i++) {
					for (int j = 0; j < values().length; j++) {
						if (ints.get(i) == values()[j].ord) {
							set.add(values()[j]);
							break;
						}
					}
				}
				return set;
			}
		}
	}


}
