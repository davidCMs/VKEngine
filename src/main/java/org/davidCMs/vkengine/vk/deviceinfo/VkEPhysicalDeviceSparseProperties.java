package org.davidCMs.vkengine.vk.deviceinfo;

import org.lwjgl.vulkan.VkPhysicalDeviceSparseProperties;

public record VkEPhysicalDeviceSparseProperties(
		boolean residencyStandard2DBlockShape,
		boolean residencyStandard2DMultisampleBlockShape,
		boolean residencyStandard3DBlockShape,
		boolean residencyAlignedMipSize,
		boolean residencyNonResidentStrict
) {

	public static VkEPhysicalDeviceSparseProperties getFrom(VkPhysicalDeviceSparseProperties sparseProperties) {
		return new VkEPhysicalDeviceSparseProperties(
				sparseProperties.residencyStandard2DBlockShape(),
				sparseProperties.residencyStandard2DMultisampleBlockShape(),
				sparseProperties.residencyStandard3DBlockShape(),
				sparseProperties.residencyAlignedMipSize(),
				sparseProperties.residencyNonResidentStrict()
		);
	}

}
