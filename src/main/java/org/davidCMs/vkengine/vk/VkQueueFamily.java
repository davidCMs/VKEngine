package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class VkQueueFamily {

	private static final HashMap<VkPhysicalDevice, Set<VkQueueFamily>> familyDeviceMap = new HashMap<>();

	public static Set<VkQueueFamily> getDeviceQueueFamilies(VkPhysicalDevice device) {
		if (familyDeviceMap.containsKey(device)) return familyDeviceMap.get(device);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] fCount = new int[1];

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device.getPhysicalDevice(), fCount, null);

			VkQueueFamilyProperties.Buffer buffer = VkQueueFamilyProperties.calloc(fCount[0], stack);

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device.getPhysicalDevice(), fCount, buffer);

			Set<VkQueueFamily> familySet = new HashSet<>();

			for (int i = 0; i < buffer.remaining(); i++) {
				VkQueueFamilyProperties properties = buffer.get(i);

				VkQueueFamily family = new VkQueueFamily(
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

	private VkQueueFamily(int index, int mask, int maxQueues) {
		this.index = index;
		this.mask = mask;
		this.maxQueues = maxQueues;
	}

	private VkCommandPool createCommandPool(VkDeviceContext device, int flags) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCommandPoolCreateInfo info = VkCommandPoolCreateInfo.calloc(stack);
			info.sType$Default();
			info.flags(flags);
			info.queueFamilyIndex(index);

			LongBuffer lb = stack.mallocLong(1);

			int err;
			err = VK14.vkCreateCommandPool(device.device(), info, null, lb);
			if (err != VK14.VK_SUCCESS)
				throw new RuntimeException("Failed to create command pool: " + VkUtils.translateErrorCode(err));

			return new VkCommandPool(this, device, lb.get(0));
		}
	}

	public VkCommandPool createCommandPool(VkDeviceContext device, VkCommandPoolCreateFlags... flags) {
		return createCommandPool(device, VkCommandPoolCreateFlags.getMaskOf(flags));
	}

	public VkCommandPool createCommandPool(VkDeviceContext device, Set<VkCommandPoolCreateFlags> flags) {
		return createCommandPool(device, VkCommandPoolCreateFlags.getMaskOf(flags));
	}

	public VkDeviceBuilderQueueInfo makeCreateInfo() {
		return new VkDeviceBuilderQueueInfo(this);
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

	public boolean isProtected() {
		return (mask & VK14.VK_QUEUE_PROTECTED_BIT) != 0;
	}

	public boolean capableOfVideoDecode() {
		return (mask & KHRVideoDecodeQueue.VK_QUEUE_VIDEO_DECODE_BIT_KHR) != 0;
	}

	public boolean capableOfVideoEncode() {
		return (mask & KHRVideoEncodeQueue.VK_QUEUE_VIDEO_ENCODE_BIT_KHR) != 0;
	}

	public int getMaxQueues() {
		return maxQueues;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "VkQueueFamily{" +
				"index=" + index +
				", maskDec=" + mask +
				", maskBin=" + String.format("%8s", Integer.toBinaryString(mask)).replace(' ', '0') +
				", maxQueues=" + maxQueues +
				", graphics=" + capableOfGraphics() +
				", compute=" + capableOfCompute() +
				", transfer=" + capableOfTransfer() +
				", sparseBinding=" + capableOfSparseBinding() +
				", protected=" + isProtected() +
				", decode=" + capableOfVideoDecode() +
				", encode=" + capableOfVideoEncode() +
				'}';
	}
}
