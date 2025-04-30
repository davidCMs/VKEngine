package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

public class VkEPhysicalDeviceProperties {

	private final VkPhysicalDeviceProperties properties;
	private final VkPhysicalDeviceFeatures features;
	private final VkPhysicalDeviceSparseProperties sparseProperties;

	public VkEPhysicalDeviceProperties(VkPhysicalDevice device, MemoryStack stack) {

		VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.calloc(stack);
		VK14.vkGetPhysicalDeviceProperties(device, properties);
		this.properties = properties;
		this.sparseProperties = properties.sparseProperties();

		VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);
		VK14.vkGetPhysicalDeviceFeatures(device, features);
		this.features = features;
	}

	public VkEDeviceType deviceType() {
		return VkEDeviceType.getType(properties.deviceType());
	}

	public String deviceNameString() {
		return properties.deviceNameString();
	}

	public VkPhysicalDeviceLimits limits() {
		return properties.limits();
	}

	public VkPhysicalDeviceSparseProperties sparseProperties() {
		return properties.sparseProperties();
	}

	public boolean vertexPipelineStoresAndAtomics() {
		return features.vertexPipelineStoresAndAtomics();
	}

	public boolean fragmentStoresAndAtomics() {
		return features.fragmentStoresAndAtomics();
	}

	public boolean shaderTessellationAndGeometryPointSize() {
		return features.shaderTessellationAndGeometryPointSize();
	}

	public boolean shaderImageGatherExtended() {
		return features.shaderImageGatherExtended();
	}

	public boolean shaderStorageImageExtendedFormats() {
		return features.shaderStorageImageExtendedFormats();
	}

	public boolean shaderStorageImageMultisample() {
		return features.shaderStorageImageMultisample();
	}

	public boolean shaderStorageImageReadWithoutFormat() {
		return features.shaderStorageImageReadWithoutFormat();
	}

	public boolean shaderStorageImageWriteWithoutFormat() {
		return features.shaderStorageImageWriteWithoutFormat();
	}

	public boolean shaderUniformBufferArrayDynamicIndexing() {
		return features.shaderUniformBufferArrayDynamicIndexing();
	}

	public boolean shaderSampledImageArrayDynamicIndexing() {
		return features.shaderSampledImageArrayDynamicIndexing();
	}

	public boolean shaderStorageBufferArrayDynamicIndexing() {
		return features.shaderStorageBufferArrayDynamicIndexing();
	}

	public boolean shaderStorageImageArrayDynamicIndexing() {
		return features.shaderStorageImageArrayDynamicIndexing();
	}

	public boolean shaderClipDistance() {
		return features.shaderClipDistance();
	}

	public boolean shaderCullDistance() {
		return features.shaderCullDistance();
	}

	public boolean shaderFloat64() {
		return features.shaderFloat64();
	}

	public boolean shaderInt64() {
		return features.shaderInt64();
	}

	public boolean shaderInt16() {
		return features.shaderInt16();
	}

	public boolean shaderResourceResidency() {
		return features.shaderResourceResidency();
	}

	public boolean shaderResourceMinLod() {
		return features.shaderResourceMinLod();
	}

	public boolean sparseBinding() {
		return features.sparseBinding();
	}

	public boolean sparseResidencyBuffer() {
		return features.sparseResidencyBuffer();
	}

	public boolean sparseResidencyImage2D() {
		return features.sparseResidencyImage2D();
	}

	public boolean sparseResidencyImage3D() {
		return features.sparseResidencyImage3D();
	}

	public boolean sparseResidency2Samples() {
		return features.sparseResidency2Samples();
	}

	public boolean sparseResidency4Samples() {
		return features.sparseResidency4Samples();
	}

	public boolean sparseResidency8Samples() {
		return features.sparseResidency8Samples();
	}

	public boolean sparseResidency16Samples() {
		return features.sparseResidency16Samples();
	}

	public boolean sparseResidencyAliased() {
		return features.sparseResidencyAliased();
	}

	public boolean variableMultisampleRate() {
		return features.variableMultisampleRate();
	}

	public boolean inheritedQueries() {
		return features.inheritedQueries();
	}

	public boolean residencyStandard2DBlockShape() {
		return sparseProperties.residencyStandard2DBlockShape();
	}

	public boolean residencyStandard2DMultisampleBlockShape() {
		return sparseProperties.residencyStandard2DMultisampleBlockShape();
	}

	public boolean residencyStandard3DBlockShape() {
		return sparseProperties.residencyStandard3DBlockShape();
	}

	public boolean residencyAlignedMipSize() {
		return sparseProperties.residencyAlignedMipSize();
	}

	public boolean residencyNonResidentStrict() {
		return sparseProperties.residencyNonResidentStrict();
	}

}
