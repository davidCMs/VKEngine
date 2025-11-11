package dev.davidCMs.vkengine.graphics.vk;

import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum VkPresentMode {
	IMMEDIATE(VK_PRESENT_MODE_IMMEDIATE_KHR),
	MAILBOX(VK_PRESENT_MODE_MAILBOX_KHR),
	FIFO(VK_PRESENT_MODE_FIFO_KHR),
	FIFO_RELAXED(VK_PRESENT_MODE_FIFO_RELAXED_KHR)

	;

	final int value;

	VkPresentMode(int ord) {
		this.value = ord;
	}

	public static Set<VkPresentMode> getFrom(MemoryStack stack, VkPhysicalDevice physicalDevice, long surface) {
        IntBuffer count = stack.callocInt(1);
        int err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice.getPhysicalDevice(), surface, count, null);
        if (!VkUtils.successful(err))
            throw new RuntimeException("Could not query presentation modes: " + VkUtils.translateErrorCode(err));

        IntBuffer ints = stack.callocInt(count.get(0));
        err = vkGetPhysicalDeviceSurfacePresentModesKHR(physicalDevice.getPhysicalDevice(), surface, count, ints);
        if (!VkUtils.successful(err))
            throw new RuntimeException("Could not query presentation modes: " + VkUtils.translateErrorCode(err));

        Set<VkPresentMode> set = new HashSet<>();
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