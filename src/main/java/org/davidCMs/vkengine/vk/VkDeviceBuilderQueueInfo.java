package org.davidCMs.vkengine.vk;

public class VkDeviceBuilderQueueInfo {

	private final VkEQueueFamily family;
	private float[] priorities = {1};

	public VkDeviceBuilderQueueInfo(VkEQueueFamily family) {
		this.family = family;
	}

	public float[] getPriorities() {
		return priorities;
	}

	public VkEQueueFamily getFamily() {
		return family;
	}

	public VkDeviceBuilderQueueInfo setPriorities(float... priorities) {
		for (int i = 0; i < priorities.length; i++)
			if (priorities[i] > 1 || priorities[i] < 0)
				throw new IllegalArgumentException("Priority at index " + i + " is not in the range of [1, 0], it was: " + priorities[i]);

		if (priorities.length > family.getMaxQueues())
			throw new IllegalArgumentException("To many priorities(" + priorities.length + ") Queue family only supports: " + family.getMaxQueues());

		this.priorities = priorities;
		return this;
	}
}
