package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDevice;

import java.util.HashMap;
import java.util.List;

public record VkDeviceContext(
		VkDevice device,
		HashMap<VkQueueFamily, VkQueue[]> queueMap,

		VkDeviceBuilder builder
) {


	public VkQueue getQueue(VkQueueFamily family, int index) {
		if (!queueMap.containsKey(family)) throw new
				IllegalArgumentException("Provided queue family(index: " + family.getIndex() + ") was not created in this device!");
		VkQueue[] queues = queueMap.get(family);
		if (!(index < queues.length))
			throw new ArrayIndexOutOfBoundsException("Provided index is out of bounds. max: " + (queues.length-1) + ", got: " + index);
		return queues[index];
	}

	public void destroy() {
		VK14.vkDestroyDevice(device, null);
	}

	public void resetFences(List<VkFence> fences) {
		if (fences == null || fences.isEmpty())
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkResetFences(
					device,
					VkFence.fencesToLB(stack, fences)
			);
		}
	}

	public  void resetFences(VkFence... fences) {
		if (fences == null || fences.length < 1)
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkResetFences(
					device,
					VkFence.fencesToLB(stack, fences)
			);
		}
	}

	public void waitForFences(List<VkFence> fences) {
		waitForFences(-1, true, fences);
	}

	public void waitForFences(VkFence... fences) {
		waitForFences(-1, true, fences);
	}

	public void waitForFences(boolean waitAll, List<VkFence> fences) {
		waitForFences(-1, waitAll, fences);
	}

	public void waitForFences(boolean waitAll, VkFence... fences) {
		waitForFences(-1, waitAll, fences);
	}

	public void waitForFences(long timeout, List<VkFence> fences) {
		waitForFences(timeout, true, fences);
	}

	public void waitForFences(long timeout, VkFence... fences) {
		waitForFences(timeout, true, fences);
	}

	public void waitForFences(long timeout, boolean waitAll, List<VkFence> fences) {
		if (fences == null || fences.isEmpty())
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkWaitForFences(
					device,
					VkFence.fencesToLB(stack, fences),
					waitAll,
					timeout
			);
		}
	}

	public void waitForFences(long timeout, boolean waitAll, VkFence... fences) {
		if (fences == null || fences.length < 1)
			return;

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VK14.vkWaitForFences(
					device,
					VkFence.fencesToLB(stack, fences),
					waitAll,
					timeout
			);
		}
	}

	public void waitIdle() {
		VK14.vkDeviceWaitIdle(device);
	}

}
