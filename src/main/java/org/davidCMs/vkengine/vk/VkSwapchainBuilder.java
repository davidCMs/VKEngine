package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtil;
import org.joml.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class VkSwapchainBuilder {

	private final long surface;
	private final VkDevice device;
	private final VkPhysicalDeviceSwapChainInfo swapChainInfo;

	private int minImageCount = -1;
	private int imageFormat = -1;
	private int imageColorSpace = -1;
	private Vector2i imageExtent;
	private int imageArrayLayers = -1;
	private Set<VkImageUsage> imageUsage = new HashSet<>();
	private SharingMode imageSharingMode;
	private Set<VkQueueFamily> queueFamilies = new HashSet<>();
	private SurfaceTransform surfaceTransform;
	private CompositeAlpha compositeAlpha;
	private PresentMode presentMode;
	private boolean clipped;

	private VkSwapchainBuilder(long surface, VkDevice device) {
		this.surface = surface;
		this.device = device;
		this.swapChainInfo = VkPhysicalDeviceSwapChainInfo.getFrom(device.getPhysicalDevice(), surface);
	}

	//todo When a logging system is added add warnings when a value is changed.
	public KHRSwapchain build(int oldSwapchain) {

		if (minImageCount < swapChainInfo.surfaceCapabilities().minImageCount()) {
			minImageCount = swapChainInfo.surfaceCapabilities().minImageCount() + 1;
		}

		if (imageFormat == -1 || !swapChainInfo.supportsFormat(imageFormat)) {
			if (swapChainInfo.supportsFormat(VK14.VK_FORMAT_R8G8B8A8_SRGB)) imageFormat = VK14.VK_FORMAT_R8G8B8A8_SRGB;
			else throw new RuntimeException("Could not fallback to a supported format");
		}

		if (imageColorSpace == -1 || !swapChainInfo.supportsColorSpace(imageColorSpace)) {
			if (swapChainInfo.supportsColorSpace(KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR))
				imageColorSpace = KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR;
			else throw new RuntimeException("Could not fallback to a supported color space");
		}

		if (imageExtent.x > swapChainInfo.surfaceCapabilities().maxImageExtent().x) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().x != 0xFFFFFFFF)
				imageExtent.x = swapChainInfo.surfaceCapabilities().currentExtent().x;
			else throw new RuntimeException("Could not fallback to a acceptable extent");
		}

		if (imageExtent.y > swapChainInfo.surfaceCapabilities().maxImageExtent().y) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().y != 0xFFFFFFFF)
				imageExtent.y = swapChainInfo.surfaceCapabilities().currentExtent().y;
			else throw new RuntimeException("Could not fallback to a acceptable extent");
		}

		if (imageExtent.x < swapChainInfo.surfaceCapabilities().minImageExtent().x) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().x != 0xFFFFFFFF)
				imageExtent.x = swapChainInfo.surfaceCapabilities().currentExtent().x;
			else throw new RuntimeException("Could not fallback to a acceptable extent");
		}

		if (imageExtent.y < swapChainInfo.surfaceCapabilities().minImageExtent().y) {
			if (swapChainInfo.surfaceCapabilities().currentExtent().y != 0xFFFFFFFF)
				imageExtent.y = swapChainInfo.surfaceCapabilities().currentExtent().y;
			else throw new RuntimeException("Could not fallback to a acceptable extent");
		}

		if (imageArrayLayers > swapChainInfo.surfaceCapabilities().maxImageArrayLayers()) {
			imageArrayLayers = swapChainInfo.surfaceCapabilities().maxImageArrayLayers();
		}

		if (imageUsage.isEmpty()) {
			imageUsage = Set.of(VkImageUsage.COLOR_ATTACHMENT);
		}

		if (imageSharingMode == null)
			imageSharingMode = SharingMode.EXCLUSIVE;

		if (surfaceTransform == null)
			surfaceTransform = SurfaceTransform.INHERIT;

		if (compositeAlpha == null)
			compositeAlpha = CompositeAlpha.INHERIT;

		if (presentMode == null)
			presentMode = PresentMode.FIFO;

		if (!swapChainInfo.presentModes().contains(presentMode))
			presentMode = PresentMode.FIFO;

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
					.clipped(clipped)
					.compositeAlpha(compositeAlpha.bit)
					.flags(0)
					.imageArrayLayers(imageArrayLayers)
					.imageColorSpace(imageColorSpace)
					.imageExtent(VkUtil.Vector2iToExtent2D(imageExtent, stack))
					.imageFormat(imageFormat)
					.imageSharingMode(imageSharingMode.value)
					.imageUsage(VkImageUsage.getValueOf(imageUsage))
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



		}

	}

	public CompositeAlpha getCompositeAlpha() {
		return compositeAlpha;
	}

	public void setCompositeAlpha(CompositeAlpha compositeAlpha) {
		this.compositeAlpha = compositeAlpha;
	}

	public SurfaceTransform getSurfaceTransform() {
		return surfaceTransform;
	}

	public void setSurfaceTransform(SurfaceTransform surfaceTransform) {
		this.surfaceTransform = surfaceTransform;
	}

	public Set<VkQueueFamily> getQueueFamilies() {
		return queueFamilies;
	}

	public void setQueueFamilies(Set<VkQueueFamily> queueFamilies) {
		this.queueFamilies = queueFamilies;
	}

	public SharingMode getImageSharingMode() {
		return imageSharingMode;
	}

	public void setImageSharingMode(SharingMode imageSharingMode) {
		this.imageSharingMode = imageSharingMode;
	}

	public Set<VkImageUsage> getImageUsage() {
		return imageUsage;
	}

	public void setImageUsage(Set<VkImageUsage> imageUsage) {
		if (imageUsage == null)
			throw new NullPointerException("imageUsage is null.");
		if (imageUsage.isEmpty())
			throw new IllegalArgumentException("Provided set is empty.");
		this.imageUsage = imageUsage;
	}

	public int getImageArrayLayers() {
		return imageArrayLayers;
	}

	public void setImageArrayLayers(int imageArrayLayers) {
		if (imageArrayLayers > swapChainInfo.surfaceCapabilities().maxImageArrayLayers()) {
			throw new IllegalArgumentException("Provided imageArrayLayers(" + imageArrayLayers + ") exceeds the maximum imageArrayLayers(" + swapChainInfo.surfaceCapabilities().maxImageArrayLayers() + ")");
		}
		this.imageArrayLayers = imageArrayLayers;
	}

	public Vector2i getImageExtent() {
		return imageExtent;
	}

	public void setImageExtent(Vector2i imageExtent) {

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
	}

	public int getImageColorSpace() {
		return imageColorSpace;
	}

	public void setImageColorSpace(int imageColorSpace) {
		if (!swapChainInfo.supportsColorSpace(imageColorSpace))
			throw new IllegalArgumentException("Color space \"" + imageColorSpace + "\" is not supported");
		this.imageColorSpace = imageColorSpace;
	}

	public int getImageFormat() {
		return imageFormat;
	}

	public void setImageFormat(int imageFormat) {
		if (!swapChainInfo.supportsFormat(imageFormat))
			throw new IllegalArgumentException("Formant \"" + imageFormat + "\" is not supported");
		this.imageFormat = imageFormat;
	}

	public int getMinImageCount() {
		return minImageCount;
	}

	public void setMinImageCount(int minImageCount) {
		int surfaceMinImageCount = swapChainInfo.surfaceCapabilities().minImageCount();
		if (minImageCount < surfaceMinImageCount)
			throw new IllegalArgumentException("provided minImageCount (" + minImageCount + ") was smaller than the minimum that the surface is capable of (" + surfaceMinImageCount + ")");
		this.minImageCount = minImageCount;
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

}
