package dev.davidCMs.vkengine.vk;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.vulkan.KHRSurface.*;

public enum VkSurfaceTransform {

	HORIZONTAL_MIRROR(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_BIT_KHR),
	IDENTITY(VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR),
	INHERIT(VK_SURFACE_TRANSFORM_INHERIT_BIT_KHR),
	ROTATE_90(VK_SURFACE_TRANSFORM_ROTATE_90_BIT_KHR),
	ROTATE_180(VK_SURFACE_TRANSFORM_ROTATE_180_BIT_KHR),
	ROTATE_270(VK_SURFACE_TRANSFORM_ROTATE_270_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_90(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_90_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_180(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_180_BIT_KHR),
	HORIZONTAL_MIRROR_ROTATE_270(VK_SURFACE_TRANSFORM_HORIZONTAL_MIRROR_ROTATE_270_BIT_KHR)

	;

	final int bit;

	VkSurfaceTransform(int bit) {
		this.bit = bit;
	}

	public static Set<VkSurfaceTransform> getFromMask(int mask) {
		Set<VkSurfaceTransform> set = new HashSet<>();
		for (int i = 0; i < values().length; i++) {
			VkSurfaceTransform transform = values()[i];
			if ((transform.bit & mask) != 0) set.add(transform);
		}
		return set;
	}

}
