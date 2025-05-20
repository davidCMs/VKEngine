package org.davidCMs.vkengine.vk.deviceinfo;

public record VkPhysicalDeviceSparseProperties(
		boolean residencyStandard2DBlockShape,
		boolean residencyStandard2DMultisampleBlockShape,
		boolean residencyStandard3DBlockShape,
		boolean residencyAlignedMipSize,
		boolean residencyNonResidentStrict
) {

	public static VkPhysicalDeviceSparseProperties getFrom(org.lwjgl.vulkan.VkPhysicalDeviceSparseProperties sparseProperties) {
		return new VkPhysicalDeviceSparseProperties(
				sparseProperties.residencyStandard2DBlockShape(),
				sparseProperties.residencyStandard2DMultisampleBlockShape(),
				sparseProperties.residencyStandard3DBlockShape(),
				sparseProperties.residencyAlignedMipSize(),
				sparseProperties.residencyNonResidentStrict()
		);
	}

}
