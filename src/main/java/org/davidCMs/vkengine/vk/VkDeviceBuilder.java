package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.BufUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class VkDeviceBuilder {

	private VkPhysicalDevice physicalDevice;
	private Set<String> extensions;
	private Set<VkDeviceBuilderQueueInfo> queueInfos;

	private final HashMap<VkEQueueFamily, VkQueue[]> queueMap = new HashMap<>();

	private final AtomicBoolean builtQueues = new AtomicBoolean(false);

	public VkDeviceBuilder() {

	}

	private void collectQueues(VkDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			PointerBuffer ptr = stack.callocPointer(1);
			ptr.put(0, 0);

			for (VkDeviceBuilderQueueInfo queueInfo : queueInfos) {
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
				queueMap.put(queueInfo.getFamily(), queues);
			}
		}
		builtQueues.set(true);
	}

	public VkQueue getQueue(VkEQueueFamily family, int index) {
		if (!queueMap.containsKey(family)) throw new
				IllegalArgumentException("Provided queue family(index: " + family.getIndex() + ") was not created in this device!");
		if (!builtQueues.get())
			throw new IllegalStateException("Device not yet built");
		VkQueue[] queues = queueMap.get(family);
		if (!(index < queues.length))
			throw new ArrayIndexOutOfBoundsException("Provided index is out of bounds. max: " + (queues.length-1) + ", got: " + index);
		return queues[index];
	}

	public VkDevice build() {
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
					.ppEnabledExtensionNames(BufUtil.stringsToPointerBuffer(stack, extensions))
					.pQueueCreateInfos(queueCreateInfos)
					.sType$Default();

			PointerBuffer ptr = stack.callocPointer(1);
			int err;
			err = VK14.vkCreateDevice(physicalDevice, info, null, ptr);
			if (err != VK14.VK_SUCCESS)
				throw new VkEDeviceCreationFailureException("Failed to create device error code: " + err);

			VkDevice device = new VkDevice(ptr.get(), physicalDevice, info);

			collectQueues(device);

			return device;
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

	public HashMap<VkEQueueFamily, VkQueue[]> getQueueMap() {
		return queueMap;
	}

}
