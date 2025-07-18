package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkPhysicalDeviceUtils {

	public static Set<VkPhysicalDevice> getAvailablePhysicalDevices(VkInstance instance) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			int[] devCount = new int[1];
			VK14.vkEnumeratePhysicalDevices(instance, devCount, null);
			PointerBuffer devBuf = stack.callocPointer(devCount[0]);
			VK14.vkEnumeratePhysicalDevices(instance, devCount, devBuf);

			if (devCount[0] < 1)
				throw new IllegalStateException("Unable to find any graphical device.");

			Set<VkPhysicalDevice> devices = new HashSet<>();

			for (int i = 0; i < devBuf.remaining(); i++) {
				devices.add(new VkPhysicalDevice(devBuf.get(i), instance));
			}
			return devices;
		}
	}

	public static boolean canRenderTo(VkPhysicalDevice physicalDevice, VkQueueFamily queueFamily, long surface) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer buf = stack.callocInt(1);
			KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice, queueFamily.getIndex(), surface, buf);
			return buf.get(0) == VK14.VK_TRUE;
		}
	}

}
