package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.common.Destroyable;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record VkDeviceContext(
		VkDevice device,
		HashMap<VkQueueFamily, VkQueue[]> queueMap,
		VkDeviceBuilder builder,
		VkPhysicalDevice physicalDevice,
        long allocator
) implements Destroyable {

    private static long createAlloc(VkDevice dev, VkPhysicalDevice pd, VkDeviceBuilder builder) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            List<Integer> enabled = new ArrayList<>();
            if (builder.getExtensions().contains(KHRDedicatedAllocation.VK_KHR_DEDICATED_ALLOCATION_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_DEDICATED_ALLOCATION_BIT);
            if (builder.getExtensions().contains(KHRBindMemory2.VK_KHR_BIND_MEMORY_2_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_DEDICATED_ALLOCATION_BIT);
            if (builder.getExtensions().contains(KHRMaintenance2.VK_KHR_MAINTENANCE_2_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_BIND_MEMORY2_BIT);
            if (builder.getExtensions().contains(KHRMaintenance4.VK_KHR_MAINTENANCE_4_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_MAINTENANCE4_BIT);
            if (builder.getExtensions().contains(KHRMaintenance5.VK_KHR_MAINTENANCE_5_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_MAINTENANCE5_BIT);
            if (builder.getExtensions().contains(EXTMemoryBudget.VK_EXT_MEMORY_BUDGET_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_EXT_MEMORY_BUDGET_BIT);
            if (builder.getExtensions().contains(KHRBufferDeviceAddress.VK_KHR_BUFFER_DEVICE_ADDRESS_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_BUFFER_DEVICE_ADDRESS_BIT);
            if (builder.getExtensions().contains(EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_EXT_MEMORY_PRIORITY_BIT);
            if (builder.getExtensions().contains(AMDDeviceCoherentMemory.VK_AMD_DEVICE_COHERENT_MEMORY_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_AMD_DEVICE_COHERENT_MEMORY_BIT);
            if (builder.getExtensions().contains(KHRExternalMemoryWin32.VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME))
                enabled.add(Vma.VMA_ALLOCATOR_CREATE_KHR_EXTERNAL_MEMORY_WIN32_BIT);

            int flags = 0;
            for (int bit : enabled)
                flags |= bit;


            VmaAllocatorCreateInfo alloc = VmaAllocatorCreateInfo.calloc(stack)
                    .flags(flags)
                    .physicalDevice(dev.getPhysicalDevice())
                    .device(dev)
                    .instance(pd.getPhysicalDevice().getInstance())
                    .pVulkanFunctions(VmaVulkanFunctions.calloc(stack)
                            .set(dev.getPhysicalDevice().getInstance(), dev));

            int err;
            PointerBuffer pb = stack.callocPointer(1);
            err = Vma.vmaCreateAllocator(alloc, pb);
            if (!VkUtils.successful(err)) {
                throw new RuntimeException("Failed to create a VMA allocator for device: " + pd.getInfo().properties().deviceName() + " (" + VkUtils.translateErrorCode(err) + ")");
            }

            return pb.get(0);
        }
    }

    public VkDeviceContext(VkDevice device, HashMap<VkQueueFamily, VkQueue[]> queueMap, VkDeviceBuilder builder, VkPhysicalDevice physicalDevice) {
        this(device, queueMap, builder, physicalDevice, createAlloc(device, physicalDevice, builder));
    }

    public VkQueue getQueue(VkQueueFamily family, int index) {
		if (!queueMap.containsKey(family)) throw new
				IllegalArgumentException("Provided queue family(index: " + family.getIndex() + ") was not created in this device!");
		VkQueue[] queues = queueMap.get(family);
		if (!(index < queues.length))
			throw new ArrayIndexOutOfBoundsException("Provided index is out of bounds. max: " + (queues.length-1) + ", got: " + index);
		return queues[index];
	}

    @Override
	public void destroy() {
        Vma.vmaDestroyAllocator(allocator);
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

	public VkInstance getInstance() {
		return device.getPhysicalDevice().getInstance();
	}

}
