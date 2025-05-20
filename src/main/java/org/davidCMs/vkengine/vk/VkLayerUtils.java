package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkLayerProperties;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

public class VkLayerUtils {

	public static final String KHRONOS_PROFILES_NAME = "VK_LAYER_KHRONOS_profiles";
	public static final String KHRONOS_VALIDATION_NAME = "VK_LAYER_KHRONOS_validation";
	public static final String KHRONOS_SYNCHRONIZATION2_NAME = "VK_LAYER_KHRONOS_synchronization2";
	public static final String KHRONOS_SHADER_OBJECT_NAME = "VK_LAYER_KHRONOS_shader_object";

	public static final String LUNARG_STREENSHOT_NAME = "VK_LAYER_LUNARG_screenshot";
	public static final String LUNARG_CRASH_DIAGNOSTIC_NAME = "VK_LAYER_LUNARG_crash_diagnostic";
	public static final String LUNARG_API_DUMP_NAME = "VK_LAYER_LUNARG_api_dump";
	public static final String LUNARG_GFXRECONSTRUCT_NAME = "VK_LAYER_LUNARG_gfxreconstruct";
	public static final String LUNARG_MONITOR = "VK_LAYER_LUNARG_monitor";

	public static Set<String> getAvailableLayers() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] count = new int[1];

			vkEnumerateInstanceLayerProperties(count, null);

			VkLayerProperties.Buffer buf = VkLayerProperties.calloc(count[0], stack);

			vkEnumerateInstanceLayerProperties(count, buf);

			Set<String> set = new HashSet<>();
			buf.forEach(layer -> {
				set.add(layer.layerNameString());
			});

			return set;
		}
	}

	public static boolean checkAvailabilityOf(String... layerNames) {
		Set<String> available = getAvailableLayers();
		for (String ck : layerNames) {
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
