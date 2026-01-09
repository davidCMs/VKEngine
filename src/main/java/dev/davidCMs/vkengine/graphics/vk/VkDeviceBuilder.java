package dev.davidCMs.vkengine.graphics.vk;

import org.tinylog.TaggedLogger;
import dev.davidCMs.vkengine.util.BufUtils;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class VkDeviceBuilder {

    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");
	private VkPhysicalDevice physicalDevice;
	private VkDeviceExtensionInfo extInfo;
	private Set<VkDeviceBuilderQueueInfo> queueInfos;
	private PNextChainable pNext;

	public VkDeviceBuilder() {

	}

	private HashMap<VkQueueFamily, VkQueue[]> collectQueues(VkDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			HashMap<VkQueueFamily, VkQueue[]> queueMap = new HashMap<>();
			PointerBuffer ptr = stack.callocPointer(1);
			ptr.put(0, 0);

			for (VkDeviceBuilderQueueInfo queueInfo : queueInfos) {
				VkQueue[] queues = new VkQueue[queueInfo.getPriorities().length];

				for (int i = 0; i < queueInfo.getPriorities().length; i++) {
					VK14.vkGetDeviceQueue(device, queueInfo.getFamily().getIndex(), i, ptr);
					if (ptr.get(0) == -1)
						throw new VkCannotGetQueueException("Cannot get the " + i
								+ " queue from queue family " + queueInfo.getFamily());

					queues[i] = new VkQueue(new org.lwjgl.vulkan.VkQueue(ptr.get(0), device), queueInfo.getFamily());
					ptr.put(0, -1);
				}
				log.debug("Got {} queues from queue family {}", queues.length, queueInfo.getFamily().getIndex());
				queueMap.put(queueInfo.getFamily(), queues);
			}
			return queueMap;
		}
	}

	public VkDeviceContext build() {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(queueInfos.size(), stack);
			int i = 0;
			for (VkDeviceBuilderQueueInfo builderQueueInfo : queueInfos) {
				queueCreateInfos.put(i, builderQueueInfo.build(stack));
				i++;
			}

			Set<VkDeviceExtension> enabledExtensions = new HashSet<>();

			for (VkDeviceExtension extension : extInfo.requiredExtension()) {
				if (!VkDeviceExtension.checkAvailabilityOf(physicalDevice, extension))
					throw new VkDeviceExtensionNotAvailableException(extension);
				enabledExtensions.add(extension);
			}

			for (VkDeviceExtension extension : extInfo.wantedExtensions()) {
				if (!VkDeviceExtension.checkAvailabilityOf(physicalDevice, extension))
					log.warn("Wanted extension not available: " + extension);
				else enabledExtensions.add(extension);
			}

			VkDeviceCreateInfo info = VkDeviceCreateInfo.calloc(stack)
					.ppEnabledExtensionNames(VkDeviceExtension.toPointerBuffer(enabledExtensions, stack))
					.pQueueCreateInfos(queueCreateInfos)
					.sType$Default()
					.pNext(pNext.getpNext(stack));

			PointerBuffer ptr = stack.callocPointer(1);
			int err;
			err = VK14.vkCreateDevice(physicalDevice.getPhysicalDevice(), info, null, ptr);
			if (err != VK14.VK_SUCCESS)
				throw new VkDeviceCreationFailureException("Failed to create device error code: " + VkUtils.translateErrorCode(err));

			VkDevice device = new VkDevice(ptr.get(), physicalDevice.getPhysicalDevice(), info);

			return new VkDeviceContext(
					device,
					collectQueues(device),
					enabledExtensions,
					physicalDevice
			);
		}
	}

	public VkDeviceBuilder setpNext(PNextChainable pNext) {
		this.pNext = pNext;
		return this;
	}

	public VkPhysicalDevice getPhysicalDevice() {
		return physicalDevice;
	}

	public VkDeviceBuilder setPhysicalDevice(VkPhysicalDevice physicalDevice) {
		this.physicalDevice = physicalDevice;
		return this;
	}

	public VkDeviceExtensionInfo getDeviceExtensionInfo() {
		return extInfo;
	}

	public VkDeviceBuilder setExtensions(VkDeviceExtensionInfo extInfo) {
		this.extInfo = extInfo;
		return this;
	}

	public Set<VkDeviceBuilderQueueInfo> getQueueInfos() {
		return queueInfos;
	}

	public VkDeviceBuilder setQueueInfos(Set<VkDeviceBuilderQueueInfo> queueInfos) {
		this.queueInfos = queueInfos;
		return this;
	}

	public VkDeviceBuilder setQueueInfos(VkDeviceBuilderQueueInfo... queueInfos) {
		return setQueueInfos(Set.of(queueInfos));
	}

}
