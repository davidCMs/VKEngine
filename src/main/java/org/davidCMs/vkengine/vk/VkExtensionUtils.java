package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkExtensionProperties;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VkExtensionUtils {

	public static Set<String> getRequiredVkExtensions() {
		if (!GLFWVulkan.glfwVulkanSupported())
			throw new IllegalStateException("This system does not support vulkan.");

		PointerBuffer extensionsPtr = GLFWVulkan.glfwGetRequiredInstanceExtensions();
		if (extensionsPtr == null)
			throw new IllegalStateException("Failed to get extensions required for GLFW.");

		Set<String> extensionNames = new HashSet<>(extensionsPtr.remaining());
		for (int i = 0; i <extensionsPtr.remaining(); i++) {
			long addr = extensionsPtr.get(i);
			extensionNames.add(MemoryUtil.memUTF8Safe(addr));
		}

		return extensionNames;
	}

	public static Set<String> getAvailableExtension() {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			int[] count = new int[1];

			if (VK14.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, null) != VK10.VK_SUCCESS) {
				throw new VkExtensionQueryException("Cannot get extension count.");
			}
			VkExtensionProperties.Buffer extBuff = VkExtensionProperties.malloc(count[0], stack);

			if (VK14.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, extBuff) != VK10.VK_SUCCESS) {
				throw new VkExtensionQueryException("Cannot get extensions.");
			}

			return extBuff.stream()
					.map(VkExtensionProperties::extensionNameString)
					.collect(Collectors.toSet());
		}
	}

	public static boolean checkAvailabilityOf(Collection<String> extensionNames) {
		return checkAvailabilityOf(extensionNames.toArray(new String[0]));
	}

	public static boolean checkAvailabilityOf(String... extensionNames) {
		Set<String> available = getAvailableExtension();
		for (String ck : extensionNames) {
			boolean b = false;
			for (String ac : available) {
				if (ac.equals(ck)) {
					b = true;
					break;
				}
			}
			if (!b) return false;
		}
		return true;
	}

}
