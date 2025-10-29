package dev.davidCMs.vkengine.util;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkExtent2D;

import javax.swing.*;

public class VkUtils {

	public static VkExtent2D vector2iToExtent2D(Vector2i vec, MemoryStack stack) {
		return VkExtent2D.calloc(stack).set(vec.x, vec.y);
	}

    public static Vector2i extent2DToVector2i(VkExtent2D extent) {
        return new Vector2i(extent.width(), extent.height());
    }

	public static String translateErrorCode(int err) {
		return switch (err) {

			case 0 -> "Success";
			case 1 -> "Not Ready";
			case 2 -> "Timeout";
			case 3 -> "Event Set";
			case 4 -> "Event Reset";
			case 5 -> "Incomplete";
			case KHRSwapchain.VK_SUBOPTIMAL_KHR -> "Suboptimal";

			case -1 -> "Out Of Host Memory";
			case -2 -> "Out Of Device Memory";
			case -3 -> "Initialization Failed";
			case -4 -> "Device Lost";
			case -5 -> "Memory Map Failed";
			case -6 -> "Layer Not Present";
			case -7 -> "Extension Not Present";
			case -8 -> "Feature Not Present";
			case -9 -> "Incompatible Driver";
			case -10 -> "To Many Objects";
			case -11 -> "Format Not Supported";
			case -12 -> "Fragmented Pool";
			case KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR -> "Out Of Date";
            case KHRSurface.VK_ERROR_SURFACE_LOST_KHR -> "Surface Lost";

			default -> "Unknown";

		};
	}

	public static boolean successful(int err) {
		return err > -1;
	}

}
