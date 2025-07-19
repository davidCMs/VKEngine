package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;

public class VkRenderingAttachmentInfoBuilder {

	private VkImageView imageView;
	private VkImageLayout imageLayout;
	private VkAttachmentLoadOp loadOp;
	private VkAttachmentStoreOp storeOp;
	private VkClearValue clearValue;

	public VkRenderingAttachmentInfo build(MemoryStack stack) {
		VkRenderingAttachmentInfo info = VkRenderingAttachmentInfo.calloc(stack);
		info.sType$Default();
		info.imageView(imageView.imageView());
		info.imageLayout(imageLayout.bit);
		info.loadOp(loadOp.bit);
		info.storeOp(storeOp.bit);
		info.clearValue(clearValue.toNativeVkClearValue(stack));

		return info;
	}

	public VkImageView getImageView() {
		return imageView;
	}

	public VkRenderingAttachmentInfoBuilder setImageView(VkImageView imageView) {
		this.imageView = imageView;
		return this;
	}

	public VkImageLayout getImageLayout() {
		return imageLayout;
	}

	public VkRenderingAttachmentInfoBuilder setImageLayout(VkImageLayout imageLayout) {
		this.imageLayout = imageLayout;
		return this;
	}

	public VkAttachmentLoadOp getLoadOp() {
		return loadOp;
	}

	public VkRenderingAttachmentInfoBuilder setLoadOp(VkAttachmentLoadOp loadOp) {
		this.loadOp = loadOp;
		return this;
	}

	public VkAttachmentStoreOp getStoreOp() {
		return storeOp;
	}

	public VkRenderingAttachmentInfoBuilder setStoreOp(VkAttachmentStoreOp storeOp) {
		this.storeOp = storeOp;
		return this;
	}

	public VkClearValue getClearValue() {
		return clearValue;
	}

	public VkRenderingAttachmentInfoBuilder setClearValue(VkClearValue clearValue) {
		this.clearValue = clearValue;
		return this;
	}
}
