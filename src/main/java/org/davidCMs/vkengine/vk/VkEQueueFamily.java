package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class VkEQueueFamily {

	private static final HashMap<VkPhysicalDevice, Set<VkEQueueFamily>> familyDeviceMap = new HashMap<>();

	public static Set<VkEQueueFamily> getDeviceQueueFamilies(VkPhysicalDevice device) {
		if (familyDeviceMap.containsKey(device)) return familyDeviceMap.get(device);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] fCount = new int[1];

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device, fCount, null);

			VkQueueFamilyProperties.Buffer buffer = VkQueueFamilyProperties.calloc(fCount[0], stack);

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device, fCount, buffer);

			Set<VkEQueueFamily> familySet = new HashSet<>();

			for (int i = 0; i < buffer.remaining(); i++) {
				VkQueueFamilyProperties properties = buffer.get(i);

				VkEQueueFamily family = new VkEQueueFamily(
						i,
						properties.queueFlags(),
						properties.queueCount()
				);
				familySet.add(family);
			}
			familyDeviceMap.put(device, familySet);
			return familySet;
		}
	}

	private final int index;
	private final int mask;
	private final int maxQueues;

	private AtomicInteger queuesCreated;

	VkEQueueFamily(int index, int mask, int maxQueues) {
		this.index = index;
		this.mask = mask;
		this.maxQueues = maxQueues;
		queuesCreated = new AtomicInteger(0);
		System.out.println("Created new family with " + maxQueues + " max queues");
	}

	public VkEDeviceQueueCreateInfo makeCreateInfo(float priority) {
		if (maxQueues - queuesCreated.incrementAndGet() < 0) {
			System.out.println(maxQueues);
			System.out.println(queuesCreated.get());
			queuesCreated.decrementAndGet();
			throw new VkECannotCreateQueueException("Failed to create queue as the queue family has reached its limit");
		}
		return new VkEDeviceQueueCreateInfo(index, queuesCreated.get()-1, priority);
	}

	public boolean capableOfGraphics() {
		return (mask & VK14.VK_QUEUE_GRAPHICS_BIT) != 0;
	}

	public boolean capableOfCompute() {
		return (mask & VK14.VK_QUEUE_COMPUTE_BIT) != 0;
	}

	public boolean capableOfTransfer() {
		return (mask & VK14.VK_QUEUE_TRANSFER_BIT) != 0;
	}

	public boolean capableOfSparseBinding() {
		return (mask & VK14.VK_QUEUE_SPARSE_BINDING_BIT) != 0;
	}

	public int getMaxQueues() {
		return maxQueues;
	}

	int getIndex() {
		return index;
	}

	public int getQueuesCreated() {
		return queuesCreated.get();
	}
}
