package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Class that represents queue families for vulkan devices
 * @implNote It acts like a singleton so each queue family of each device only has one instance of this class */
public class VkQueueFamily {

	/** Map linking sets of instances of this class to physical devices */
	private static final ConcurrentHashMap<VkPhysicalDevice, Set<VkQueueFamily>> familyDeviceMap = new ConcurrentHashMap<>();

	/** gets a set of this class from a {@link VkPhysicalDevice}
	 * @param device device of which queues to get
	 * @return a set of {@link VkQueueFamily}s
	 * @implNote Always returns the same instance of an unmodifiable set with same instances of {@link VkQueueFamily}s */
	public static Set<VkQueueFamily> getDeviceQueueFamilies(VkPhysicalDevice device) {
		if (familyDeviceMap.containsKey(device)) return familyDeviceMap.get(device);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer fCount = stack.mallocInt(1);

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device.getPhysicalDevice(), fCount, null);

			VkQueueFamilyProperties.Buffer buffer = VkQueueFamilyProperties.calloc(fCount.get(0), stack);

			VK14.vkGetPhysicalDeviceQueueFamilyProperties(device.getPhysicalDevice(), fCount, buffer);

			Set<VkQueueFamily> familySet = new HashSet<>();

			for (int i = 0; i < buffer.remaining(); i++) {
				VkQueueFamilyProperties properties = buffer.get(i);

				VkQueueFamily family = new VkQueueFamily(
						i,
						properties.queueFlags(),
						properties.queueCount(),
						device
				);
				familySet.add(family);
			}

			Set<VkQueueFamily> immutable = Collections.unmodifiableSet(familySet);

			familyDeviceMap.put(device, immutable);
			return immutable;
		}
	}

	/** Vulkan index */
	private final int index;
	/** Vulkan flags */
	private final int mask;
	/** The Maximum number of queues that can be created form this family */
	private final int maxQueues;
	/** The {@link VkPhysicalDevice} that owns this queue */
	private final VkPhysicalDevice physicalDevice;

	private VkQueueFamily(int index, int mask, int maxQueues, VkPhysicalDevice physicalDevice) {
		this.index = index;
		this.mask = mask;
		this.maxQueues = maxQueues;
		this.physicalDevice = physicalDevice;
	}

	/** Creates a new {@link VkCommandPool} from this family */
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
		return createCommandPool(device, (int) VkCommandPoolCreateFlags.getMaskOf(flags));
	}

	public VkCommandPool createCommandPool(VkDeviceContext device, Set<VkCommandPoolCreateFlags> flags) {
		return createCommandPool(device, (int) VkCommandPoolCreateFlags.getMaskOf(flags));
	}

	/** Makes a create info that can be passed to {@link VkDeviceBuilder#setQueueInfos(VkDeviceBuilderQueueInfo...)}
	 * @implNote the priorities of queues can be changed on the returned object via {@link VkDeviceBuilderQueueInfo#setPriorities(float...)} */
	public VkDeviceBuilderQueueInfo makeCreateInfo() {
		return new VkDeviceBuilderQueueInfo(this);
	}

	/** Same as {@link VkQueueFamily#makeCreateInfo()} but takes in a vararg of priorities. */
	public VkDeviceBuilderQueueInfo makeCreateInfo(float... priorities) {
		return new VkDeviceBuilderQueueInfo(this).setPriorities(priorities);
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

	public boolean canRenderTo(long surface) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buf = stack.callocInt(1);
			KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.getPhysicalDevice(), index, surface, buf);
			return buf.get(0) == VK14.VK_TRUE;
		}
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

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof VkQueueFamily that)) return false;

        return getIndex() == that.getIndex() && mask == that.mask && getMaxQueues() == that.getMaxQueues() && physicalDevice.equals(that.physicalDevice);
	}

	@Override
	public int hashCode() {
		int result = getIndex();
		result = 31 * result + mask;
		result = 31 * result + getMaxQueues();
		result = 31 * result + physicalDevice.hashCode();
		return result;
	}
}
