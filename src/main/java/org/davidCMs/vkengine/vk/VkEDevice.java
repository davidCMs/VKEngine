package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class VkEDevice extends AutoCloseableResource {

	private final VkDevice device;

	private final HashMap<VkEQueueFamily, VkQueue[]> queueMap;

	public VkEDevice(VkPhysicalDevice physicalDevice, VkEDeviceCreateInfo info) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer ptr = stack.callocPointer(1);
			int err;
			err = VK14.vkCreateDevice(physicalDevice, info.getInfo(), null, ptr);
			if (err != VK14.VK_SUCCESS)
				throw new VkEDeviceCreationFailureException("Failed to create device error code: " + err);

			device = new VkDevice(ptr.get(), physicalDevice, info.getInfo());
			queueMap = collectQueues(device, info);

			info.close();
		}
	}

	private HashMap<VkEQueueFamily, VkQueue[]> collectQueues(VkDevice device, VkEDeviceCreateInfo info) {
		Set<VkEDeviceQueueCreateInfo> queueCreateInfos = info.getQueueCreateInfos();
		HashMap<VkEQueueFamily, VkQueue[]> map = new HashMap<>();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer ptr = stack.callocPointer(1);
			ptr.put(0, 0);

			for (VkEDeviceQueueCreateInfo queueInfo : queueCreateInfos) {
				VkQueue[] queues = new VkQueue[queueInfo.getPriorities().length];

				for (int i = 0; i < queueInfo.getPriorities().length; i++) {
					VK14.vkGetDeviceQueue(device, queueInfo.getFamily().getIndex(), i, ptr);
					if (ptr.get(0) == -1)
						throw new CannotGetQueueException("Cannot get the " + i
								+ " queue from queue family " + queueInfo.getFamily());

					queues[i] = new VkQueue(ptr.get(0), device);
					ptr.put(0, -1);
				}
				System.out.println("Got " + queues.length + " queues from queue family " + queueInfo.getFamily().getIndex());
				map.put(queueInfo.getFamily(), queues);
			}
		}

		return map;
	}

	public VkQueue getQueue(VkEQueueFamily family, int index) {
		check();
		if (!queueMap.containsKey(family)) throw new
				IllegalArgumentException("Provided queue family(index: " + family.getIndex() + ") was not created in this device!");
		VkQueue[] queues = queueMap.get(family);
		if (!(index < queues.length))
			throw new ArrayIndexOutOfBoundsException("Provided index is out of bounds. max: " + (queues.length-1) + ", got: " + index);
		return queues[index];
	}


	@Override
	public void close() {
		super.close();

		VK14.vkDestroyDevice(device, null);
	}
}
