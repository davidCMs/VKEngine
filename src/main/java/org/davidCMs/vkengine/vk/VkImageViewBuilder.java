package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

public class VkImageViewBuilder {

	private final VkDeviceContext device;

	private long image = -1;
	private VkImageViewType imageViewType;
	private VkImageFormat imageFormat;
	private ComponentOverrides componentOverrides = ComponentOverrides.IDENTITY;
	private VkImageSubresourceRangeBuilder vkImageSubresourceRange;

	public record ComponentOverrides(
			VkComponentSwizzle r,
			VkComponentSwizzle g,
			VkComponentSwizzle b,
			VkComponentSwizzle a
	) {

		public static final ComponentOverrides IDENTITY = new ComponentOverrides(
				VkComponentSwizzle.IDENTITY,
				VkComponentSwizzle.IDENTITY,
				VkComponentSwizzle.IDENTITY,
				VkComponentSwizzle.IDENTITY
		);

		public VkComponentMapping createMapping(MemoryStack stack) {
			VkComponentMapping mapping = VkComponentMapping.calloc(stack);
			mapping.set(r.getBit(), g.getBit(), b.getBit(), a.getBit());
			return mapping;
		}

	}

	public VkImageViewBuilder(VkDeviceContext device) {
		this.device = device;
	}

	public VkImageContext build() {

		if (image == -1)
			throw new NullPointerException("image was not set");

		if (imageViewType == null) {
			throw new NullPointerException("imageViewType was not set.");
		}

		if (imageFormat == null) {
			throw new NullPointerException("imageFormat was not set");
		}

		if (vkImageSubresourceRange == null)
			throw new NullPointerException("vkImageSubresourceRange was not set");

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkImageViewCreateInfo info = VkImageViewCreateInfo.calloc(stack)
					.set(
							0,
							VK14.VK_NULL_HANDLE,
							0,
							image,
							imageViewType.bit,
							imageFormat.bit,
							componentOverrides.createMapping(stack),
							vkImageSubresourceRange.build(stack)
					)
					.sType$Default();
			LongBuffer lb = stack.callocLong(1);

			int err;
			err = VK14.vkCreateImageView(device.device(), info, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create image view err: " + VkUtils.translateErrorCode(err));
			return new VkImageContext(device, image, lb.get(0));
		}
	}

	public VkImageSubresourceRangeBuilder getVkImageSubresourceRange() {
		return vkImageSubresourceRange;
	}

	public VkImageViewBuilder setVkImageSubresourceRange(VkImageSubresourceRangeBuilder vkImageSubresourceRange) {
		this.vkImageSubresourceRange = vkImageSubresourceRange;
		return this;
	}

	public long getImage() {
		return image;
	}

	public VkImageViewBuilder setImage(long image) {
		this.image = image;
		return this;
	}

	public ComponentOverrides getComponentOverrides() {
		return componentOverrides;
	}

	public VkImageViewBuilder setComponentOverrides(ComponentOverrides componentOverrides) {
		this.componentOverrides = componentOverrides;
		return this;
	}

	public VkImageFormat getImageFormat() {
		return imageFormat;
	}

	public VkImageViewBuilder setImageFormat(VkImageFormat imageFormat) {
		this.imageFormat = imageFormat;
		return this;
	}

	public VkImageViewType getImageViewType() {
		return imageViewType;
	}

	public VkImageViewBuilder setImageViewType(VkImageViewType imageViewType) {
		this.imageViewType = imageViewType;
		return this;
	}

	public VkDeviceContext getDevice() {
		return device;
	}
}
