package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;

public class VkCommandPool {

	private final VkQueueFamily queueFamily;
	private final long commandPool;
	private final VkDeviceContext device;

	public VkCommandPool(VkQueueFamily queueFamily, VkDeviceContext device, long commandPool) {
		this.queueFamily = queueFamily;
		this.device = device;
		this.commandPool = commandPool;
	}

	public VkCommandBuffer[] createCommandBuffer(boolean isSecondary, int count) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCommandBufferAllocateInfo info = VkCommandBufferAllocateInfo.calloc(stack);
			info.sType$Default();
			info.commandPool(commandPool);

			if (isSecondary)
				info.level(VK14.VK_COMMAND_BUFFER_LEVEL_SECONDARY);
			else
				info.level(VK14.VK_COMMAND_BUFFER_LEVEL_PRIMARY);

			info.commandBufferCount(count);

			PointerBuffer pb = stack.mallocPointer(count);
			int err;
			err = VK14.vkAllocateCommandBuffers(device.device(), info, pb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to allocate command buffer(s): " + err);

			VkCommandBuffer[] buffers = new VkCommandBuffer[count];

			for (int i = 0; i < count; i++) {
				buffers[i] = new VkCommandBuffer(this, pb.get(i));
			}

			return buffers;
		}
	}

	public VkCommandBuffer createCommandBuffer(boolean isSecondary) {
		return createCommandBuffer(isSecondary, 1)[0];
	}

	public VkCommandBuffer[] createCommandBuffer(int count) {
		return createCommandBuffer(false, count);
	}

	public VkCommandBuffer createCommandBuffer() {
		return createCommandBuffer(false);
	}

	public VkQueueFamily getQueueFamily() {
		return queueFamily;
	}

	public VkDeviceContext getDevice() {
		return device;
	}

	public void destroy() {
		VK14.vkDestroyCommandPool(device.device(), commandPool, null);
	}
}
