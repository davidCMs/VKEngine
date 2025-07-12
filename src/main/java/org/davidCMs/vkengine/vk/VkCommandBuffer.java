package org.davidCMs.vkengine.vk;

public class VkCommandBuffer {

	private final VkCommandPool pool;
	private final org.lwjgl.vulkan.VkCommandBuffer commandBuffer;

	public VkCommandBuffer(VkCommandPool pool, long commandBuffer) {
		this.pool = pool;
		this.commandBuffer = new org.lwjgl.vulkan.VkCommandBuffer(commandBuffer, pool.getDevice().device());
	}
}
