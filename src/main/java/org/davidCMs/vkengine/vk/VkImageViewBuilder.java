package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

public class VkImageViewBuilder {

	private final VkDeviceContext device;

	private VkImageType imageViewType;
	private VkImageFormat imageFormat;
	private ComponentOverrides componentOverrides = ComponentOverrides.IDENTITY;
	private VkImageSubresourceRangeBuilder imageSubresourceRange;

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

	public VkImageView build(VkImage image) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkImageViewCreateInfo info = VkImageViewCreateInfo.calloc(stack)
					.set(
							0,
							VK14.VK_NULL_HANDLE,
							0,
							image.image(),
							imageViewType.bit,
							imageFormat.bit,
							componentOverrides.createMapping(stack),
							imageSubresourceRange.build(stack)
					)
					.sType$Default();
			LongBuffer lb = stack.callocLong(1);

			int err;
			err = VK14.vkCreateImageView(device.device(), info, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create image view err: " + VkUtils.translateErrorCode(err));
			return new VkImageView(
					lb.get(0),
					image,
					imageViewType,
					imageFormat,
					componentOverrides,
					imageSubresourceRange.copy()
			);
		}
	}

	public VkImageSubresourceRangeBuilder getVkImageSubresourceRange() {
		return imageSubresourceRange;
	}

	public VkImageViewBuilder setImageSubresourceRange(VkImageSubresourceRangeBuilder vkImageSubresourceRange) {
		this.imageSubresourceRange = vkImageSubresourceRange;
		return this;
	}

	public VkImageSubresourceRangeBuilder getImageSubresourceRange() {
		return imageSubresourceRange;
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

	public VkImageType getImageViewType() {
		return imageViewType;
	}

	public VkImageViewBuilder setImageViewType(VkImageType imageViewType) {
		this.imageViewType = imageViewType;
		return this;
	}

	public VkDeviceContext getDevice() {
		return device;
	}
}
