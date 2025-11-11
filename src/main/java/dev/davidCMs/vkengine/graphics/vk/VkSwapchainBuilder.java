package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.BuilderSet;
import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.Copyable;
import dev.davidCMs.vkengine.util.VkUtils;
import dev.davidCMs.vkengine.window.GLFWWindow;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

public class VkSwapchainBuilder implements Copyable {

    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");

	private final VkDeviceContext device;

    private VkSurface surface;
    private int minImageCount = -1;
    private VkFormat imageFormat = VkFormat.R8G8B8A8_SRGB;
    private VkImageColorSpace imageColorSpace = VkImageColorSpace.SRGB_NONLINEAR;
    private Vector2i imageExtent;
	private int imageArrayLayers = 1;;
	private BuilderSet<VkSwapchainBuilder, VkImageUsage> imageUsage = new BuilderSet<>(this);
	private BuilderSet<VkSwapchainBuilder, VkQueueFamily> queueFamilies = new BuilderSet<>(this);
	private VkSurfaceTransform surfaceTransform = VkSurfaceTransform.IDENTITY;
	private VkCompositeAlpha compositeAlpha = VkCompositeAlpha.OPAQUE;
	private VkPresentMode presentMode = VkPresentMode.FIFO;
	private boolean clipped;

	public VkSwapchainBuilder(VkDeviceContext device) {
		this.device = device;
        imageUsage.add(VkImageUsage.COLOR_ATTACHMENT);
	}

    public VkSwapchain create(VkSwapchain oldSwapchain) {
        return new VkSwapchain(this, oldSwapchain);
    }

	public long build(long oldSwapchain) {

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
					.clipped(clipped)
					.compositeAlpha(compositeAlpha.bit)
					.flags(0)
					.imageArrayLayers(imageArrayLayers)
					.imageColorSpace(imageColorSpace.bit)
					.imageExtent(VkUtils.vector2iToExtent2D(imageExtent, stack))
					.imageFormat(imageFormat.bit)
					.imageSharingMode(queueFamilies.size() > 1 ? VkSharingMode.CONCURRENT.bit : VkSharingMode.EXCLUSIVE.bit)
					.imageUsage((int) VkImageUsage.getMaskOf(imageUsage))
					.minImageCount(minImageCount)
					.oldSwapchain(oldSwapchain)
					.pQueueFamilyIndices(queueFamiliesAsArray(stack))
					.presentMode(presentMode.value)
					.preTransform(surfaceTransform.bit)
					.queueFamilyIndexCount(queueFamilies.size())
					.surface(surface.getSurface())
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

	public VkSwapchainContext newContext(GLFWWindow window) {
		return new VkSwapchainContext(copy(), window);
	}

	public VkSurface getSurface() {
		return surface;
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

	public VkFormat getImageFormat() {
		return imageFormat;
	}

	public VkSwapchainBuilder setImageFormat(VkFormat imageFormat) {
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

    public BuilderSet<VkSwapchainBuilder, VkImageUsage> imageUsage() {
        return imageUsage;
    }

    public BuilderSet<VkSwapchainBuilder, VkQueueFamily> queueFamilies() {
        return queueFamilies;
    }

    public VkSwapchainBuilder setSurface(VkSurface surface) {
        this.surface = surface;
        return this;
    }

    @Override
	public VkSwapchainBuilder copy() {
		return new VkSwapchainBuilder(device)
				.setMinImageCount(minImageCount)
				.setImageFormat(imageFormat)
				.setImageColorSpace(imageColorSpace)
				.setImageExtent(imageExtent)
				.setImageArrayLayers(imageArrayLayers)
				.imageUsage().add(imageUsage.getSet()).ret()
				.queueFamilies().add(queueFamilies.getSet()).ret()
				.setSurfaceTransform(surfaceTransform)
				.setCompositeAlpha(compositeAlpha)
				.setPresentMode(presentMode)
                .setSurface(surface)
				.setClipped(clipped);
	}
}
