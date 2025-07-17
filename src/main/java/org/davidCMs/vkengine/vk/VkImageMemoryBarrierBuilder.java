package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkImageMemoryBarrier2;

import java.util.HashSet;
import java.util.Set;

public class VkImageMemoryBarrierBuilder {

	private VkImage image;

	private Set<VkPipelineStage> srcStageMask = new HashSet<>();
	private Set<VkAccess> srcAccessMask = new HashSet<>();
	private Set<VkPipelineStage> dstStageMask = new HashSet<>();
	private Set<VkAccess> dstAccessMask = new HashSet<>();
	private VkImageLayout oldLayout;
	private VkImageLayout newLayout;
	private VkQueueFamily srcQueueFamily;
	private VkQueueFamily dstQueueFamily;
	private VkImageSubresourceRangeBuilder subresourceRange;

	public VkImageMemoryBarrier2 build(MemoryStack stack) {
		VkImageMemoryBarrier2 barrier = VkImageMemoryBarrier2.calloc(stack);
		barrier.sType$Default();
		barrier.image(image.image());

		if (srcStageMask != null)
			barrier.srcStageMask(VkPipelineStage.getMaskOf(srcStageMask));
		if (srcAccessMask != null)
			barrier.srcAccessMask(VkAccess.getMaskOf(srcAccessMask));
		if (dstStageMask != null)
			barrier.dstStageMask(VkPipelineStage.getMaskOf(dstStageMask));
		if (dstAccessMask != null)
			barrier.dstAccessMask(VkAccess.getMaskOf(dstAccessMask));

		barrier.oldLayout(oldLayout.bit);
		barrier.newLayout(newLayout.bit);

		barrier.srcQueueFamilyIndex(
				srcQueueFamily == null ?
						VK14.VK_QUEUE_FAMILY_IGNORED :
						srcQueueFamily.getIndex()
		);
		barrier.dstQueueFamilyIndex(
				dstQueueFamily == null ?
						VK14.VK_QUEUE_FAMILY_IGNORED :
						dstQueueFamily.getIndex()
		);

		barrier.subresourceRange(subresourceRange.build(stack));

		return barrier;

	}

	public VkImage getImage() {
		return image;
	}

	public Set<VkPipelineStage> getSrcStageMask() {
		return srcStageMask;
	}

	public VkImageMemoryBarrierBuilder setSrcStageMask(Set<VkPipelineStage> srcStageMask) {
		this.srcStageMask = srcStageMask;
		return this;
	}

	public Set<VkAccess> getSrcAccessMask() {
		return srcAccessMask;
	}

	public VkImageMemoryBarrierBuilder setSrcAccessMask(Set<VkAccess> srcAccessMask) {
		this.srcAccessMask = srcAccessMask;
		return this;
	}

	public Set<VkPipelineStage> getDstStageMask() {
		return dstStageMask;
	}

	public VkImageMemoryBarrierBuilder setDstStageMask(Set<VkPipelineStage> dstStageMask) {
		this.dstStageMask = dstStageMask;
		return this;
	}

	public Set<VkAccess> getDstAccessMask() {
		return dstAccessMask;
	}

	public VkImageMemoryBarrierBuilder setDstAccessMask(Set<VkAccess> dstAccessMask) {
		this.dstAccessMask = dstAccessMask;
		return this;
	}

	public VkImageLayout getOldLayout() {
		return oldLayout;
	}

	public VkImageMemoryBarrierBuilder setOldLayout(VkImageLayout oldLayout) {
		this.oldLayout = oldLayout;
		return this;
	}

	public VkImageLayout getNewLayout() {
		return newLayout;
	}

	public VkImageMemoryBarrierBuilder setNewLayout(VkImageLayout newLayout) {
		this.newLayout = newLayout;
		return this;
	}

	public VkQueueFamily getSrcQueueFamily() {
		return srcQueueFamily;
	}

	public VkImageMemoryBarrierBuilder setSrcQueueFamily(VkQueueFamily srcQueueFamily) {
		this.srcQueueFamily = srcQueueFamily;
		return this;
	}

	public VkQueueFamily getDstQueueFamily() {
		return dstQueueFamily;
	}

	public VkImageMemoryBarrierBuilder setDstQueueFamily(VkQueueFamily dstQueueFamily) {
		this.dstQueueFamily = dstQueueFamily;
		return this;
	}

	public VkImageSubresourceRangeBuilder getSubresourceRange() {
		return subresourceRange;
	}

	public VkImageMemoryBarrierBuilder setSubresourceRange(VkImageSubresourceRangeBuilder subresourceRange) {
		this.subresourceRange = subresourceRange;
		return this;
	}

	public VkImageMemoryBarrierBuilder setImage(VkImage image) {
		this.image = image;
		return this;
	}
}
