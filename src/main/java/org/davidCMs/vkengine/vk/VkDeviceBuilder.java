package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.davidCMs.vkengine.util.BufUtils;
import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashMap;
import java.util.Set;

public class VkDeviceBuilder {

	private static final Logger log = LogManager.getLogger(VkDeviceBuilder.class, VulkanMessageFactory.INSTANCE);
	private VkPhysicalDevice physicalDevice;
	private Set<String> extensions;
	private Set<VkDeviceBuilderQueueInfo> queueInfos;

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

					queues[i] = new VkQueue(ptr.get(0), device);
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
				VkDeviceQueueCreateInfo queueCreateInfo = VkDeviceQueueCreateInfo.calloc(stack);
				queueCreateInfo.pQueuePriorities(stack.floats(builderQueueInfo.getPriorities()));
				queueCreateInfo.queueFamilyIndex(builderQueueInfo.getFamily().getIndex());
				queueCreateInfo.sType$Default();
				queueCreateInfos.put(i, queueCreateInfo);
				i++;
			}

			VkDeviceCreateInfo info = VkDeviceCreateInfo.calloc(stack)
					.ppEnabledExtensionNames(BufUtils.stringsToPointerBuffer(stack, extensions))
					.pQueueCreateInfos(queueCreateInfos)
					.sType$Default();

			PointerBuffer ptr = stack.callocPointer(1);
			int err;
			err = VK14.vkCreateDevice(physicalDevice, info, null, ptr);
			if (err != VK14.VK_SUCCESS)
				throw new VkDeviceCreationFailureException("Failed to create device error code: " + VkUtils.translateErrorCode(err));

			VkDevice device = new VkDevice(ptr.get(), physicalDevice, info);



			return new VkDeviceContext(
					device,
					collectQueues(device),
					this
			);
		}
	}

	public VkPhysicalDevice getPhysicalDevice() {
		return physicalDevice;
	}

	public VkDeviceBuilder setPhysicalDevice(VkPhysicalDevice physicalDevice) {
		this.physicalDevice = physicalDevice;
		return this;
	}

	public Set<String> getExtensions() {
		return extensions;
	}

	public VkDeviceBuilder setExtensions(Set<String> extensions) {
		this.extensions = extensions;
		return this;
	}

	public VkDeviceBuilder setExtensions(String... extensions) {
		return setExtensions(Set.of(extensions));
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
