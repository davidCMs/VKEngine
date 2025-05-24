package org.davidCMs.vkengine.vk;

import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkQueue;

import java.util.HashMap;

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

}
