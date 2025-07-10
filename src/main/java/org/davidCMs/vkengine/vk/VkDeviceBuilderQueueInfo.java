package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.ValueNotNormalizedException;

import java.util.Arrays;

public class VkDeviceBuilderQueueInfo {

	private final VkQueueFamily family;
	private float[] priorities = {1};

	public VkDeviceBuilderQueueInfo(VkQueueFamily family) {
		this.family = family;
	}

	public float[] getPriorities() {
		return priorities;
	}

	public VkQueueFamily getFamily() {
		return family;
	}

	public VkDeviceBuilderQueueInfo setPriorities(float... priorities) {
		for (int i = 0; i < priorities.length; i++)
			if (priorities[i] > 1 || priorities[i] < 0)
				throw new ValueNotNormalizedException("All values in priorities MUST be in the range of [1, 0], but value at index: \"" + i + "\" was \"" + priorities[i] + "\"");

		if (priorities.length > family.getMaxQueues())
			throw new IllegalArgumentException("To many priorities(" + priorities.length + ") Queue family only supports: " + family.getMaxQueues());

		this.priorities = priorities;
		return this;
	}

	@Override
	public String toString() {
		return "VkDeviceBuilderQueueInfo{" +
				"family=" + family +
				", priorities=" + Arrays.toString(priorities) +
				'}';
	}
}
