package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
						((properties.queueFlags() & VK14.VK_QUEUE_GRAPHICS_BIT) != 0),
						((properties.queueFlags() & VK14.VK_QUEUE_COMPUTE_BIT) != 0),
						((properties.queueFlags() & VK14.VK_QUEUE_TRANSFER_BIT) != 0),
						((properties.queueFlags() & VK14.VK_QUEUE_SPARSE_BINDING_BIT) != 0),
						properties.queueCount()
				);
				familySet.add(family);
			}
			familyDeviceMap.put(device, familySet);
			return familySet;
		}
	}

	private final int index;

	private final boolean graphics;
	private final boolean compute;
	private final boolean transfer;
	private final boolean sparseBinding;

	private final int maxQueues;

	private int queuesCreated = 0;

	VkEQueueFamily(int index, boolean graphics, boolean compute, boolean transfer, boolean sparseBinding, int maxQueues) {
		this.index = index;
		this.graphics = graphics;
		this.compute = compute;
		this.transfer = transfer;
		this.sparseBinding = sparseBinding;
		this.maxQueues = maxQueues;
	}

	public VkEDeviceQueueCreateInfo makeCrateInfo(float pri) {
		return new VkEDeviceQueueCreateInfo(index, pri);
	}

	public boolean isGraphics() {
		return graphics;
	}

	public boolean isCompute() {
		return compute;
	}

	public boolean isTransfer() {
		return transfer;
	}

	public boolean isSparseBinding() {
		return sparseBinding;
	}

	public int getMaxQueues() {
		return maxQueues;
	}

	int getIndex() {
		return index;
	}

	public int availableQueues() {
		return maxQueues - queuesCreated;
	}

	public int getQueuesCreated() {
		return queuesCreated;
	}
}
