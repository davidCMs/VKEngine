package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.VkUtil;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkSwapchainBuilder {

	private static final Logger log = LogManager.getLogger(VkSwapchainBuilder.class, VulkanMessageFactory.INSTANCE);
	private final long surface;
	private final VkDevice device;
	private final VkPhysicalDeviceSwapChainInfo swapChainInfo;

	private int minImageCount = -1;
	private int imageFormat = -1;
	private int imageColorSpace = -1;
	private Vector2i imageExtent;
	private int imageArrayLayers = 1;
	private VkImageUsage imageUsage;
	private Set<VkQueueFamily> queueFamilies = new HashSet<>();
	private VkSurfaceTransform surfaceTransform;
	private VkCompositeAlpha compositeAlpha;
	private VkPresentMode presentMode;
	private boolean clipped;

	public VkSwapchainBuilder(long surface, VkDeviceContext device) {
		this.surface = surface;
		this.device = device.device();
		this.swapChainInfo = VkPhysicalDeviceSwapChainInfo.getFrom(device.device().getPhysicalDevice(), surface);
	}

	public long build(long oldSwapchain) {

		if (swapChainInfo.surfaceCapabilities().maxImageCount() != 0) {
			if (minImageCount > swapChainInfo.surfaceCapabilities().maxImageCount()) {
				log.warn("Clamping minImageCount({}) to the maximum as it is higher then the maximum({})",
						minImageCount, swapChainInfo.surfaceCapabilities().maxImageCount());
				minImageCount = swapChainInfo.surfaceCapabilities().maxImageCount();
			}
		}

		if (minImageCount < swapChainInfo.surfaceCapabilities().minImageCount()) {
			log.warn("Clamping minImageCount({}) to minimum+1 as it is lower than the minimum({})",
					minImageCount, swapChainInfo.surfaceCapabilities().minImageCount());
			minImageCount = swapChainInfo.surfaceCapabilities().minImageCount() + 1;
		}

		if (imageFormat == -1 || !swapChainInfo.supportsFormat(imageFormat)) {
			if (swapChainInfo.supportsFormat(VK14.VK_FORMAT_R8G8B8A8_SRGB)) {
				log.warn("Falling back to VK_FORMAT_R8G8B8A8_SRGB as the provided format({}) is not supported",
						imageFormat);
				imageFormat = VK14.VK_FORMAT_R8G8B8A8_SRGB;
			}
			else throw new RuntimeException("Could not fallback to a supported format");
		}

		if (imageColorSpace == -1 || !swapChainInfo.supportsColorSpace(imageColorSpace)) {
			if (swapChainInfo.supportsColorSpace(KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)) {
				log.warn("Falling back to VK_COLOR_SPACE_SRGB_NONLINEAR_KHR as the provided color space({}) is not supported",
						imageColorSpace);
				imageColorSpace = KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
			}
			else throw new RuntimeException("Could not fallback to a supported color space");
		}

		if (imageExtent == null) {
			Vector2i currentExtent = swapChainInfo.surfaceCapabilities().currentExtent();
			if (currentExtent.x == 0xFFFFFFFF || currentExtent.y == 0xFFFFFFFF) {
				throw new IllegalStateException("imageExtent is not set and cannot fallback to current extent as it is undefined");
			}
			imageExtent = currentExtent;
			log.warn("Falling back to current extent as imageExtent is not set");
		}

		if (imageExtent.x > swapChainInfo.surfaceCapabilities().maxImageExtent().x) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().x != 0xFFFFFFFF) {
				log.warn("Falling back to current extent width({}) as the provided extent width({}) is over the maximum",
						imageExtent.x, swapChainInfo.surfaceCapabilities().maxImageExtent().x);
				imageExtent.x = swapChainInfo.surfaceCapabilities().currentExtent().x;
			} else {
				throw new RuntimeException("Could not fallback to an acceptable extent");
			}
		}

		if (imageExtent.y > swapChainInfo.surfaceCapabilities().maxImageExtent().y) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().y != 0xFFFFFFFF) {
				log.warn("Falling back to current extent height({}) as the provided extent height({}) is over the maximum",
						imageExtent.y, swapChainInfo.surfaceCapabilities().maxImageExtent().y);
				imageExtent.y = swapChainInfo.surfaceCapabilities().currentExtent().y;
			} else {
				throw new RuntimeException("Could not fallback to an acceptable extent");
			}
		}

		if (imageExtent.x < swapChainInfo.surfaceCapabilities().minImageExtent().x) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().x != 0xFFFFFFFF) {
				log.warn("Falling back to current extent width({}) as the provided extent width({}) is below the minimum",
						imageExtent.x, swapChainInfo.surfaceCapabilities().minImageExtent().x);
				imageExtent.x = swapChainInfo.surfaceCapabilities().currentExtent().x;
			} else {
				throw new RuntimeException("Could not fallback to an acceptable extent");
			}
		}

		if (imageExtent.y < swapChainInfo.surfaceCapabilities().minImageExtent().y) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().y != 0xFFFFFFFF) {
				log.warn("Falling back to current extent height({}) as the provided extent height({}) is below the minimum",
						imageExtent.y, swapChainInfo.surfaceCapabilities().minImageExtent().y);
				imageExtent.y = swapChainInfo.surfaceCapabilities().currentExtent().y;
			} else {
				throw new RuntimeException("Could not fallback to an acceptable extent");
			}
		}

		if (imageArrayLayers > swapChainInfo.surfaceCapabilities().maxImageArrayLayers()) {
			log.warn("Clamping imageArrayLayers({}) to the maximum as it is higher then the maximum({})",
					imageArrayLayers, swapChainInfo.surfaceCapabilities().maxImageArrayLayers());
			imageArrayLayers = swapChainInfo.surfaceCapabilities().maxImageArrayLayers();
		}

		Set<VkImageUsage> usages = swapChainInfo.surfaceCapabilities().supportedUsage();
		if (imageUsage == null || !usages.contains(imageUsage)) {
			log.warn("No valid ImageUsage set defaulting to COLOR_ATTACHMENT");
			imageUsage = VkImageUsage.COLOR_ATTACHMENT;
		}

		Set<VkSurfaceTransform> transforms = swapChainInfo.surfaceCapabilities().supportedTransforms();
		if (surfaceTransform == null || !transforms.contains(surfaceTransform)) {
			if (transforms.contains(VkSurfaceTransform.INHERIT)) {
				surfaceTransform = VkSurfaceTransform.INHERIT;
				log.warn("No valid SurfaceTransform set defaulting to INHERIT");
			}
			else {
				surfaceTransform = VkSurfaceTransform.IDENTITY;
				log.warn("No valid SurfaceTransform set defaulting to IDENTITY as INHERIT is unsupported");
			}
		}

		Set<VkCompositeAlpha> alphas = swapChainInfo.surfaceCapabilities().supportedCompositeAlpha();
		if (compositeAlpha == null || !alphas.contains(compositeAlpha)) {
			if (alphas.contains(VkCompositeAlpha.INHERIT)) {
				compositeAlpha = VkCompositeAlpha.INHERIT;
				log.warn("No valid CompositeAlpha set defaulting to INHERIT");
			} else {
				compositeAlpha = VkCompositeAlpha.OPAQUE;
				log.warn("No valid CompositeAlpha set defaulting to OPAQUE as INHERIT is unsupported");
			}
		}

		Set<VkPresentMode> presentModes = swapChainInfo.presentModes();
		if (presentMode == null || !presentModes.contains(presentMode)) {
			if (presentModes.contains(VkPresentMode.MAILBOX)) {
				presentMode = VkPresentMode.MAILBOX;
				log.warn("No valid PresentMode set defaulting to MAILBOX");
			} else {
				presentMode = VkPresentMode.FIFO;
				log.warn("No valid PresentMode set defaulting to FIFO as MAILBOX is unsupported");
			}
			presentMode = VkPresentMode.FIFO;
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
					.clipped(clipped)
					.compositeAlpha(compositeAlpha.bit)
					.flags(0)
					.imageArrayLayers(imageArrayLayers)
					.imageColorSpace(imageColorSpace)
					.imageExtent(VkUtil.Vector2iToExtent2D(imageExtent, stack))
					.imageFormat(imageFormat)
					.imageSharingMode(queueFamilies.size() > 1 ? VkSharingMode.CONCURRENT.value : VkSharingMode.EXCLUSIVE.value)
					.imageUsage(imageUsage.bit)
					.minImageCount(minImageCount)
					.oldSwapchain(oldSwapchain)
					.pQueueFamilyIndices(queueFamiliesAsArray(stack))
					.presentMode(presentMode.value)
					.preTransform(surfaceTransform.bit)
					.queueFamilyIndexCount(queueFamilies.size())
					.surface(surface)
					.sType$Default();

			LongBuffer lb = stack.callocLong(1);

			int err;
			err = KHRSwapchain.vkCreateSwapchainKHR(device, createInfo, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create swapchain error code: " + err);

			return lb.get(0);
		}

	}



	public VkPresentMode getPresentMode() {
		return presentMode;
	}

	public void setPresentMode(VkPresentMode presentMode) {
		this.presentMode = presentMode;
	}

	public boolean isClipped() {
		return clipped;
	}

	public void setClipped(boolean clipped) {
		this.clipped = clipped;
	}

	public VkCompositeAlpha getCompositeAlpha() {
		return compositeAlpha;
	}

	public VkSwapchainBuilder setCompositeAlpha(VkCompositeAlpha compositeAlpha) {
		this.compositeAlpha = compositeAlpha;
		return this;
	}

	public VkSurfaceTransform getSurfaceTransform() {
		return surfaceTransform;
	}

	public VkSwapchainBuilder setSurfaceTransform(VkSurfaceTransform surfaceTransform) {
		this.surfaceTransform = surfaceTransform;
		return this;
	}

	public Set<VkQueueFamily> getQueueFamilies() {
		return queueFamilies;
	}

	public VkSwapchainBuilder setQueueFamilies(Set<VkQueueFamily> queueFamilies) {
		this.queueFamilies = queueFamilies;
		return this;
	}

	public VkImageUsage getImageUsage() {
		return imageUsage;
	}

	public VkSwapchainBuilder setImageUsage(VkImageUsage imageUsage) {
		if (imageUsage == null)
			throw new NullPointerException("imageUsage is null.");
		this.imageUsage = imageUsage;
		return this;
	}

	public int getImageArrayLayers() {
		return imageArrayLayers;
	}

	public VkSwapchainBuilder setImageArrayLayers(int imageArrayLayers) {
		if (imageArrayLayers > swapChainInfo.surfaceCapabilities().maxImageArrayLayers()) {
			throw new IllegalArgumentException("Provided imageArrayLayers(" + imageArrayLayers + ") exceeds the maximum imageArrayLayers(" + swapChainInfo.surfaceCapabilities().maxImageArrayLayers() + ")");
		}
		this.imageArrayLayers = imageArrayLayers;
		return this;
	}

	public Vector2i getImageExtent() {
		return imageExtent;
	}

	public VkSwapchainBuilder setImageExtent(Vector2i imageExtent) {

		if (imageExtent == null) {
			throw new NullPointerException("imageExtent is null.");
		}

		if (imageExtent.x > swapChainInfo.surfaceCapabilities().maxImageExtent().x) {
			throw new RuntimeException("Provided extent's width (" + imageExtent.x + ") component exceeds the maximum (" + swapChainInfo.surfaceCapabilities().maxImageExtent().x + ")");
		}

		if (imageExtent.y > swapChainInfo.surfaceCapabilities().maxImageExtent().y) {
			throw new RuntimeException("Provided extent's height (" + imageExtent.y + ") component exceeds the maximum (" + swapChainInfo.surfaceCapabilities().maxImageExtent().y + ")");
		}

		if (imageExtent.x < swapChainInfo.surfaceCapabilities().minImageExtent().x) {
			throw new RuntimeException("Provided extent's width (" + imageExtent.x + ") component subceed the minimum (" + swapChainInfo.surfaceCapabilities().minImageExtent().x + ")");
		}

		if (imageExtent.y < swapChainInfo.surfaceCapabilities().minImageExtent().y) {
			throw new RuntimeException("Provided extent's height (" + imageExtent.y + ") component subceed the minimum (" + swapChainInfo.surfaceCapabilities().minImageExtent().y + ")");
		}

		this.imageExtent = imageExtent;
		return this;
	}

	public int getImageColorSpace() {
		return imageColorSpace;
	}

	public VkSwapchainBuilder setImageColorSpace(int imageColorSpace) {
		if (!swapChainInfo.supportsColorSpace(imageColorSpace))
			throw new IllegalArgumentException("Color space \"" + imageColorSpace + "\" is not supported");
		this.imageColorSpace = imageColorSpace;
		return this;
	}

	public int getImageFormat() {
		return imageFormat;
	}

	public VkSwapchainBuilder setImageFormat(int imageFormat) {
		if (!swapChainInfo.supportsFormat(imageFormat))
			throw new IllegalArgumentException("Formant \"" + imageFormat + "\" is not supported");
		this.imageFormat = imageFormat;
		return this;
	}

	public int getMinImageCount() {
		return minImageCount;
	}

	public VkSwapchainBuilder setMinImageCount(int minImageCount) {
		if (swapChainInfo.surfaceCapabilities().maxImageCount() != 0) {
			if (minImageCount > swapChainInfo.surfaceCapabilities().maxImageCount())
				throw new IllegalArgumentException("Provided minImageCount (" + minImageCount + ") exceeds the max image count (" + swapChainInfo.surfaceCapabilities().maxImageCount() + ")");
		}

		int surfaceMinImageCount = swapChainInfo.surfaceCapabilities().minImageCount();
		if (minImageCount < surfaceMinImageCount)
			throw new IllegalArgumentException("provided minImageCount (" + minImageCount + ") was smaller than the minimum that the surface is capable of (" + surfaceMinImageCount + ")");
		this.minImageCount = minImageCount;
		return this;
	}

	private IntBuffer queueFamiliesAsArray(MemoryStack stack) {
		return stack.ints(queueFamilies.stream()
				.mapToInt(VkQueueFamily::getIndex)
				.toArray());
	}

	public VkPhysicalDeviceSwapChainInfo getSwapChainInfo() {
		return swapChainInfo;
	}

	public long getSurface() {
		return surface;
	}

	public VkDevice getDevice() {
		return device;
	}
}
