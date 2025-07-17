package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkImageSubresourceRange;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VkImageSubresourceRangeBuilder implements Copyable {

	private Set<VkAspectMask> aspectMask = new HashSet<>();

	private int baseMipLevel = 0;
	private int levelCount = -1;
	private int baseArrayLayer = 0;
	private int layerCount = -1;

	public VkImageSubresourceRange build(MemoryStack stack) {

		if (aspectMask == null)
			throw new NullPointerException("aspectMask was not set");
		if (aspectMask.isEmpty())
			throw new IllegalStateException("aspectMask must be set to an set containing at least 1 element, it is currently set to and empty set");

		VkImageSubresourceRange range = VkImageSubresourceRange.calloc(stack)
				.aspectMask(VkAspectMask.getMaskOf(aspectMask))
				.baseMipLevel(baseMipLevel)
				.levelCount(levelCount)
				.baseArrayLayer(baseArrayLayer)
				.layerCount(layerCount);

		return range;
	}

	public Set<VkAspectMask> getAspectMask() {
		return aspectMask;
	}

	public VkImageSubresourceRangeBuilder setAspectMask(Set<VkAspectMask> aspectMask) {
		this.aspectMask.clear();
		this.aspectMask.addAll(aspectMask);
		return this;
	}

	public VkImageSubresourceRangeBuilder setAspectMask(VkAspectMask... aspectMask) {
		this.aspectMask.clear();
		this.aspectMask.addAll(Arrays.stream(aspectMask).toList());
		return this;
	}

	public int getBaseMipLevel() {
		return baseMipLevel;
	}

	public VkImageSubresourceRangeBuilder setBaseMipLevel(int baseMipLevel) {
		this.baseMipLevel = baseMipLevel;
		return this;
	}

	public int getLevelCount() {
		return levelCount;
	}

	public VkImageSubresourceRangeBuilder setLevelCount(int levelCount) {
		this.levelCount = levelCount;
		return this;
	}

	public int getBaseLayer() {
		return baseArrayLayer;
	}

	public VkImageSubresourceRangeBuilder setBaseLayer(int baseLayer) {
		this.baseArrayLayer = baseLayer;
		return this;
	}

	public int getLayerCount() {
		return layerCount;
	}

	public VkImageSubresourceRangeBuilder setLayerCount(int layerCount) {
		this.layerCount = layerCount;
		return this;
	}

	@Override
	public VkImageSubresourceRangeBuilder copy() {
		return new VkImageSubresourceRangeBuilder()
				.setAspectMask(aspectMask != null ? new HashSet<>(aspectMask) : null)
				.setBaseMipLevel(baseMipLevel)
				.setLevelCount(levelCount)
				.setBaseLayer(baseArrayLayer)
				.setLayerCount(layerCount);
	}
}
