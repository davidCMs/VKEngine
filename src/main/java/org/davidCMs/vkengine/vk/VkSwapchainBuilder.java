package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.Copyable;
import org.davidCMs.vkengine.util.VkUtils;
import org.davidCMs.vkengine.window.GLFWWindow;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkSwapchainBuilder implements Copyable {

	private static final Logger log = LogManager.getLogger(VkSwapchainBuilder.class, VulkanMessageFactory.INSTANCE);

	private final GLFWWindow window;
	private final VkDeviceContext device;

	private int minImageCount = -1;
	private VkImageFormat imageFormat = VkImageFormat.R8G8B8A8_SRGB;
	private VkImageColorSpace imageColorSpace = VkImageColorSpace.SRGB_NONLINEAR;
	private Vector2i imageExtent;
	private int imageArrayLayers = 1;
	private Set<VkImageUsage> imageUsage = Set.of(VkImageUsage.COLOR_ATTACHMENT);
	private Set<VkQueueFamily> queueFamilies = new HashSet<>();
	private VkSurfaceTransform surfaceTransform = VkSurfaceTransform.IDENTITY;
	private VkCompositeAlpha compositeAlpha = VkCompositeAlpha.OPAQUE;
	private VkPresentMode presentMode = VkPresentMode.FIFO;
	private boolean clipped;

	public VkSwapchainBuilder(GLFWWindow window, VkDeviceContext device) {
		this.window = window;
		this.device = device;
	}

	public long build(long oldSwapchain) {

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
					.clipped(clipped)
					.compositeAlpha(compositeAlpha.bit)
					.flags(0)
					.imageArrayLayers(imageArrayLayers)
					.imageColorSpace(imageColorSpace.bit)
					.imageExtent(VkUtils.Vector2iToExtent2D(imageExtent, stack))
					.imageFormat(imageFormat.bit)
					.imageSharingMode(queueFamilies.size() > 1 ? VkSharingMode.CONCURRENT.bit : VkSharingMode.EXCLUSIVE.bit)
					.imageUsage(VkImageUsage.getMaskOf(imageUsage))
					.minImageCount(minImageCount)
					.oldSwapchain(oldSwapchain)
					.pQueueFamilyIndices(queueFamiliesAsArray(stack))
					.presentMode(presentMode.value)
					.preTransform(surfaceTransform.bit)
					.queueFamilyIndexCount(queueFamilies.size())
					.surface(window.getVkSurface(device.getInstance()))
					.sType$Default();

			LongBuffer lb = stack.callocLong(1);

			int err;
			err = KHRSwapchain.vkCreateSwapchainKHR(device.device(), createInfo, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create swapchain error code: " + VkUtils.translateErrorCode(err));

			return lb.get(0);
		}

	}

	private IntBuffer queueFamiliesAsArray(MemoryStack stack) {
		return stack.ints(queueFamilies.stream()
				.mapToInt(VkQueueFamily::getIndex)
				.toArray());
	}

	public VkSwapchainContext newContext() {
		return new VkSwapchainContext(copy());
	}

	public GLFWWindow getWindow() {
		return window;
	}

	public VkDeviceContext getDevice() {
		return device;
	}

	public int getMinImageCount() {
		return minImageCount;
	}

	public VkSwapchainBuilder setMinImageCount(int minImageCount) {
		this.minImageCount = minImageCount;
		return this;
	}

	public VkImageFormat getImageFormat() {
		return imageFormat;
	}

	public VkSwapchainBuilder setImageFormat(VkImageFormat imageFormat) {
		this.imageFormat = imageFormat;
		return this;
	}

	public VkImageColorSpace getImageColorSpace() {
		return imageColorSpace;
	}

	public VkSwapchainBuilder setImageColorSpace(VkImageColorSpace imageColorSpace) {
		this.imageColorSpace = imageColorSpace;
		return this;
	}

	public Vector2i getImageExtent() {
		return imageExtent;
	}

	public VkSwapchainBuilder setImageExtent(Vector2i imageExtent) {
		this.imageExtent = imageExtent;
		return this;
	}

	public int getImageArrayLayers() {
		return imageArrayLayers;
	}

	public VkSwapchainBuilder setImageArrayLayers(int imageArrayLayers) {
		this.imageArrayLayers = imageArrayLayers;
		return this;
	}

	public Set<VkImageUsage> getImageUsage() {
		return imageUsage;
	}

	public VkSwapchainBuilder setImageUsage(Set<VkImageUsage> imageUsage) {
		this.imageUsage = imageUsage;
		return this;
	}

	public Set<VkQueueFamily> getQueueFamilies() {
		return queueFamilies;
	}

	public VkSwapchainBuilder setQueueFamilies(Set<VkQueueFamily> queueFamilies) {
		this.queueFamilies = queueFamilies;
		return this;
	}

	public VkSurfaceTransform getSurfaceTransform() {
		return surfaceTransform;
	}

	public VkSwapchainBuilder setSurfaceTransform(VkSurfaceTransform surfaceTransform) {
		this.surfaceTransform = surfaceTransform;
		return this;
	}

	public VkCompositeAlpha getCompositeAlpha() {
		return compositeAlpha;
	}

	public VkSwapchainBuilder setCompositeAlpha(VkCompositeAlpha compositeAlpha) {
		this.compositeAlpha = compositeAlpha;
		return this;
	}

	public VkPresentMode getPresentMode() {
		return presentMode;
	}

	public VkSwapchainBuilder setPresentMode(VkPresentMode presentMode) {
		this.presentMode = presentMode;
		return this;
	}

	public boolean isClipped() {
		return clipped;
	}

	public VkSwapchainBuilder setClipped(boolean clipped) {
		this.clipped = clipped;
		return this;
	}

	@Override
	public VkSwapchainBuilder copy() {
		return new VkSwapchainBuilder(window, device)
				.setMinImageCount(minImageCount)
				.setImageFormat(imageFormat)
				.setImageColorSpace(imageColorSpace)
				.setImageExtent(imageExtent)
				.setImageArrayLayers(imageArrayLayers)
				.setImageUsage(imageUsage == null ? null : new HashSet<>(imageUsage))
				.setQueueFamilies(queueFamilies)
				.setSurfaceTransform(surfaceTransform)
				.setCompositeAlpha(compositeAlpha)
				.setPresentMode(presentMode)
				.setClipped(clipped);
	}
}
