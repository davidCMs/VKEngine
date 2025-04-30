package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.util.HashSet;
import java.util.Set;

public class VkEPhysicalDeviceUtils {

	public static Set<VkPhysicalDevice> getAvailablePhysicalDevices(VkEInstance instance) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			int[] devCount = new int[1];
			VK14.vkEnumeratePhysicalDevices(instance.getInstance(), devCount, null);
			PointerBuffer devBuf = stack.callocPointer(devCount[0]);
			VK14.vkEnumeratePhysicalDevices(instance.getInstance(), devCount, devBuf);

			if (devCount[0] < 1)
				throw new IllegalStateException("Unable to find any graphical device.");

			Set<VkPhysicalDevice> devices = new HashSet<>();

			for (int i = 0; i < devBuf.remaining(); i++) {
				devices.add(new VkPhysicalDevice(devBuf.get(i), instance.getInstance()));
			}
			return devices;
		}
	}

}
