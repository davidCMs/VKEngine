package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkExtensionProperties;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VkExtensionUtils {

	public static final String KHR_GET_SURFACE_CAPABILITIES2_NAME = "VK_KHR_get_surface_capabilities2";
	public static final String KHR_PORTABILITY_ENUMERATION_NAME = "VK_KHR_portability_enumeration";
	public static final String KHR_SURFACE_PROTECTED_CAPABILITIES_NAME = "VK_KHR_surface_protected_capabilities";
	public static final String KHR_SURFACE_NAME = "VK_KHR_surface";
	public static final String KHR_GET_PHYSICAL_DEVICE_PROPERTIES2_NAME = "VK_KHR_get_physical_device_properties2";
	public static final String KHR_EXTERNAL_SEMAPHORE_CAPABILITIES_NAME = "VK_KHR_external_semaphore_capabilities";
	public static final String KHR_EXTERNAL_FENCE_CAPABILITIES_NAME = "VK_KHR_external_fence_capabilities";
	public static final String KHR_DEVICE_GROUP_CREATION_NAME = "VK_KHR_device_group_creation";
	public static final String KHR_DISPLAY_NAME = "VK_KHR_display";
	public static final String KHR_GET_DISPLAY_PROPERTIES2_NAME = "VK_KHR_get_display_properties2";
	public static final String KHR_EXTERNAL_MEMORY_CAPABILITIES_NAME = "VK_KHR_external_memory_capabilities";

	public static final String LUNARG_DIRECT_DRIVER_LOADING_NAME = "VK_LUNARG_direct_driver_loading";

	public static final String EXT_DIRECT_MODE_DISPLAY_NAME = "VK_EXT_direct_mode_display";
	public static final String EXT_SURFACE_MAINTENANCE1_NAME = "VK_EXT_surface_maintenance1";
	public static final String EXT_SWAPCHAIN_COLORSPACE_NAME = "VK_EXT_swapchain_colorspace";

	public static final String EXT_DEBUG_REPORT_NAME = "VK_EXT_debug_report";
	public static final String EXT_DEBUG_UTILS_NAME = "VK_EXT_debug_utils";

	public static final String KHR_WIN32_SURFACE_NAME = "VK_KHR_win32_surface";

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
