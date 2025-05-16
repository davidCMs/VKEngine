package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;

import java.nio.FloatBuffer;

public class VkEDeviceQueueCreateInfo extends AutoCloseableResource {

	private final VkDeviceQueueCreateInfo info;
	private final VkEQueueFamily family;

	VkEDeviceQueueCreateInfo(VkEQueueFamily family) {
		this.family = family;
		info = VkDeviceQueueCreateInfo.calloc();
		info.sType$Default();
		info.pQueuePriorities(MemoryUtil.memAllocFloat(1));

		info.queueFamilyIndex(family.getIndex());
	}

	public float[] getPriorities() {
		check();

		float[] arr = new float[info.pQueuePriorities().remaining()];

		for (int i = 0; i < arr.length; i++) {
			arr[i] = info.pQueuePriorities().get(i);
		}

		return arr;
	}

	public VkEQueueFamily getFamily() {
		check();
		return family;
	}

	public VkEDeviceQueueCreateInfo setPriorities(float... priorities) {
		check();
		for (int i = 0; i < priorities.length; i++)
			if (priorities[i] > 1 || priorities[i] < 0)
				throw new IllegalArgumentException("Priority at index " + i + " is not in the range of [1, 0], it was: " + priorities[i]);

		MemoryUtil.memFree(info.pQueuePriorities());
		FloatBuffer buffer = MemoryUtil.memCallocFloat(priorities.length);

		for (int i = 0; i < priorities.length; i++) {
			buffer.put(i, priorities[i]);
		}

		info.pQueuePriorities(buffer);
		return this;
	}

	VkDeviceQueueCreateInfo getInfo() {
		check();
		return info;
	}

	@Override
	public void close(){
		super.close();
		MemoryUtil.memFree(info.pQueuePriorities());
		info.close();
	}
}
