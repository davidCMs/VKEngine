package org.davidCMs.vkengine.vk;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.Set;

public class VkCommandBuffer {

	private final VkCommandPool pool;
	private final org.lwjgl.vulkan.VkCommandBuffer commandBuffer;

	private boolean hasBegun = false;
	private boolean recorded = false;

	public VkCommandBuffer(VkCommandPool pool, long commandBuffer) {
		this.pool = pool;
		this.commandBuffer = new org.lwjgl.vulkan.VkCommandBuffer(commandBuffer, pool.getDevice().device());
	}

	public VkCommandBuffer begin(VkCommandBufferUsageFlags... flags) {
		return begin(VkCommandBufferUsageFlags.getMaskOf(flags));
	}

	public VkCommandBuffer begin(Set<VkCommandBufferUsageFlags> flags) {
		return begin(VkCommandBufferUsageFlags.getMaskOf(flags));
	}

	private VkCommandBuffer begin(int flags) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCommandBufferBeginInfo info = VkCommandBufferBeginInfo.calloc(stack);
			info.sType$Default();
			info.flags(flags);

			VK14.vkBeginCommandBuffer(commandBuffer, info);
			hasBegun = true;
		}
		return this;
	}

	public VkCommandBuffer end() {
		VK14.vkEndCommandBuffer(commandBuffer);
		hasBegun = true;
		recorded = true;
		return this;
	}

	public boolean canRecord() {
		return hasBegun && !recorded;
	}

	public VkCommandBuffer insertImageMemoryBarrier(VkImageMemoryBarrierBuilder barrier) {
		if (!canRecord())
			throw new IllegalStateException("Cannot record to buffer");

		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkImageMemoryBarrier2.Buffer buffer = VkImageMemoryBarrier2.calloc(1, stack);
			buffer.put(0, barrier.build(stack));

			VkDependencyInfo info = VkDependencyInfo.calloc(stack);
			info.pImageMemoryBarriers(buffer);

			VK14.vkCmdPipelineBarrier2(commandBuffer, info);
		}
		return this;
	}

}
