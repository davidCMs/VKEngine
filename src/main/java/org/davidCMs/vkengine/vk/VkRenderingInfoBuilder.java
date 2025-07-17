package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;

import java.util.List;

public class VkRenderingInfoBuilder {

	private VkRect2D renderArea;
	private int layerCount;
	private List<VkRenderingAttachmentInfoBuilder> colorAttachments;
	private VkRenderingAttachmentInfoBuilder depthAttachment;

	public VkRenderingInfo build(MemoryStack stack) {
		VkRenderingInfo info = VkRenderingInfo.calloc(stack);
		info.sType$Default();
		info.renderArea(renderArea.toNative(stack));
		info.layerCount(layerCount);
		info.pColorAttachments(getColorAttachmentsBuffer(stack));
		if (depthAttachment != null)
			info.pDepthAttachment(depthAttachment.build(stack));
		return info;
	}

	private VkRenderingAttachmentInfo.Buffer getColorAttachmentsBuffer(MemoryStack stack) {
		VkRenderingAttachmentInfo.Buffer buf = VkRenderingAttachmentInfo.calloc(colorAttachments.size(), stack);

		for (int i = 0; i < colorAttachments.size(); i++) {
			buf.put(i, colorAttachments.get(i).build(stack));
		}

		return buf;
	}

	public VkRect2D getRenderArea() {
		return renderArea;
	}

	public VkRenderingInfoBuilder setRenderArea(VkRect2D renderArea) {
		this.renderArea = renderArea;
		return this;
	}

	public int getLayerCount() {
		return layerCount;
	}

	public VkRenderingInfoBuilder setLayerCount(int layerCount) {
		this.layerCount = layerCount;
		return this;
	}

	public List<VkRenderingAttachmentInfoBuilder> getColorAttachments() {
		return colorAttachments;
	}

	public VkRenderingInfoBuilder setColorAttachments(List<VkRenderingAttachmentInfoBuilder> colorAttachments) {
		this.colorAttachments = colorAttachments;
		return this;
	}

	public VkRenderingAttachmentInfoBuilder getDepthAttachment() {
		return depthAttachment;
	}

	public VkRenderingInfoBuilder setDepthAttachment(VkRenderingAttachmentInfoBuilder depthAttachment) {
		this.depthAttachment = depthAttachment;
		return this;
	}
}
