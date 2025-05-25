package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum PresentMode {
	IMMEDIATE(VK_PRESENT_MODE_IMMEDIATE_KHR),
	MAILBOX(VK_PRESENT_MODE_MAILBOX_KHR),
	FIFO(VK_PRESENT_MODE_FIFO_KHR),
	FIFO_RELAXED(VK_PRESENT_MODE_FIFO_RELAXED_KHR)

	;

	final int value;

	PresentMode(int ord) {
		this.value = ord;
	}

	public static Set<PresentMode> getFrom(VkPhysicalDevice physicalDevice, long surface) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer count = stack.callocInt(1);
			int result = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, count, null);
			if (result != VK14.VK_SUCCESS)
				throw new RuntimeException("Could not query presentation modes");
			IntBuffer ints = stack.callocInt(count.get(0));
			vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice, surface, count, ints);

			Set<PresentMode> set = new HashSet<>();
			for (int i = 0; i < count.get(0); i++) {
				for (int j = 0; j < values().length; j++) {
					if (ints.get(i) == values()[j].value) {
						set.add(values()[j]);
						break;
					}
				}
			}
			return set;
		}
	}
}