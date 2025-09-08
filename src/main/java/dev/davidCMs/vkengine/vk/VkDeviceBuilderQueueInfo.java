package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.util.ValueNotNormalizedException;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;

import java.util.Arrays;

/** Abstracts {@link VkDeviceQueueCreateInfo}. This class is not meant to be initialized but
 * acquired from {@link VkQueueFamily#makeCreateInfo()}. Priorities can later be changed via the
 * {@link VkDeviceBuilderQueueInfo#setPriorities(float...)} method.*/
public class VkDeviceBuilderQueueInfo {

	private final VkQueueFamily family;
	private float[] priorities = {1};

	VkDeviceBuilderQueueInfo(VkQueueFamily family) {
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
				throw new ValueNotNormalizedException("All values in priorities MUST be in the range of [1, 0], but bit at index: \"" + i + "\" was \"" + priorities[i] + "\"");

		if (priorities.length > family.getMaxQueues())
			throw new IllegalArgumentException("To many priorities(" + priorities.length + ") Queue family only supports: " + family.getMaxQueues());

		this.priorities = priorities;
		return this;
	}

	public VkDeviceQueueCreateInfo build(MemoryStack stack) {
		VkDeviceQueueCreateInfo info = VkDeviceQueueCreateInfo.calloc(stack);
		info.pQueuePriorities(stack.floats(priorities));
		info.queueFamilyIndex(family.getIndex());
		info.sType$Default();
		return info;
	}

	@Override
	public String toString() {
		return "VkDeviceBuilderQueueInfo{" +
				"family=" + family +
				", priorities=" + Arrays.toString(priorities) +
				'}';
	}
}
