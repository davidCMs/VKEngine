package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkExtensionProperties;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Set;
import java.util.stream.Collectors;

public class VkPhysicalDeviceExtensionUtils {

	public static final String VK_KHR_SWAPCHAIN = "VK_KHR_swapchain";

	public static Set<String> getAvailableExtensions(VkPhysicalDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer count = stack.callocInt(1);

			if (VK14.vkEnumerateDeviceExtensionProperties(device.getPhysicalDevice(), (ByteBuffer) null, count, null) != VK14.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot query device extensions.");

			VkExtensionProperties.Buffer buffer = VkExtensionProperties.calloc(count.get(0), stack);

			if (VK14.vkEnumerateDeviceExtensionProperties(device.getPhysicalDevice(), (ByteBuffer) null, count, buffer) != VK14.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot query device extensions.");

			return buffer.stream()
					.map(VkExtensionProperties::extensionNameString)
					.collect(Collectors.toSet());
		}
	}

	public static boolean checkAvailabilityOf(VkPhysicalDevice device, String... deviceExtensionNames) {
		Set<String> available = getAvailableExtensions(device);
		for (String rq : deviceExtensionNames) {
			boolean isAvailable = false;
			for (String av : available) {
				if (rq.equals(av)) {
					isAvailable = true;
					break;
				}
			}
			if (!isAvailable) return false;
		}
		return true;
	}

}
