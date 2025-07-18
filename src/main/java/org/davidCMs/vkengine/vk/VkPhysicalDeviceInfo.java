package org.davidCMs.vkengine.vk;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.UUID;

public record VkPhysicalDeviceInfo(
		VkPhysicalDeviceFeatures features,
		VkPhysicalDeviceProperties properties,
		Set<VkQueueFamily> queueFamilies
		) {

	public static VkPhysicalDeviceInfo getFrom(VkPhysicalDevice device) {
		return new VkPhysicalDeviceInfo(
				VkPhysicalDeviceFeatures.getFrom(device),
				VkPhysicalDeviceProperties.getFrom(device),
				VkQueueFamily.getDeviceQueueFamilies(device)
		);
	}

	public record VkPhysicalDeviceFeatures(
			boolean robustBufferAccess,
			boolean fullDrawIndexUint32,
			boolean imageCubeArray,
			boolean independentBlend,
			boolean geometryShader,
			boolean tessellationShader,
			boolean sampleRateShading,
			boolean dualSrcBlend,
			boolean logicOp,
			boolean multiDrawIndirect,
			boolean drawIndirectFirstInstance,
			boolean depthClamp,
			boolean depthBiasClamp,
			boolean fillModeNonSolid,
			boolean depthBounds,
			boolean wideLines,
			boolean largePoints,
			boolean alphaToOne,
			boolean multiViewport,
			boolean samplerAnisotropy,
			boolean textureCompressionEtc2,
			boolean textureCompressionAstcLdr,
			boolean textureCompressionBc,
			boolean occlusionQueryPrecise,
			boolean pipelineStatisticsQuery,
			boolean vertexPipelineStoresAndAtomics,
			boolean fragmentStoresAndAtomics,
			boolean shaderTessellationAndGeometryPointSize,
			boolean shaderImageGatherExtended,
			boolean shaderStorageImageExtendedFormats,
			boolean shaderStorageImageMultisample,
			boolean shaderStorageImageReadWithoutFormat,
			boolean shaderStorageImageWriteWithoutFormat,
			boolean shaderUniformBufferArrayDynamicIndexing,
			boolean shaderSampledImageArrayDynamicIndexing,
			boolean shaderStorageBufferArrayDynamicIndexing,
			boolean shaderStorageImageArrayDynamicIndexing,
			boolean shaderClipDistance,
			boolean shaderCullDistance,
			boolean shaderFloat64,
			boolean shaderInt64,
			boolean shaderInt16,
			boolean shaderResourceResidency,
			boolean shaderResourceMinLod,
			boolean sparseBinding,
			boolean sparseResidencyBuffer,
			boolean sparseResidencyImage2d,
			boolean sparseResidencyImage3d,
			boolean sparseResidency2Samples,
			boolean sparseResidency4Samples,
			boolean sparseResidency8Samples,
			boolean sparseResidency16Samples,
			boolean sparseResidencyAliased,
			boolean variableMultisampleRate,
			boolean inheritedQueries

	) {
		public static VkPhysicalDeviceFeatures getFrom(VkPhysicalDevice device) {
			try (MemoryStack stack = MemoryStack.stackPush()) {
				org.lwjgl.vulkan.VkPhysicalDeviceFeatures features = org.lwjgl.vulkan.VkPhysicalDeviceFeatures.calloc(stack);
				VK14.vkGetPhysicalDeviceFeatures(device, features);
				return populate(features);
			}
		}

		private static VkPhysicalDeviceFeatures populate(org.lwjgl.vulkan.VkPhysicalDeviceFeatures features) {
			return new VkPhysicalDeviceFeatures(
					features.robustBufferAccess(),
					features.fullDrawIndexUint32(),
					features.imageCubeArray(),
					features.independentBlend(),
					features.geometryShader(),
					features.tessellationShader(),
					features.sampleRateShading(),
					features.dualSrcBlend(),
					features.logicOp(),
					features.multiDrawIndirect(),
					features.drawIndirectFirstInstance(),
					features.depthClamp(),
					features.depthBiasClamp(),
					features.fillModeNonSolid(),
					features.depthBounds(),
					features.wideLines(),
					features.largePoints(),
					features.alphaToOne(),
					features.multiViewport(),
					features.samplerAnisotropy(),
					features.textureCompressionETC2(),
					features.textureCompressionASTC_LDR(),
					features.textureCompressionBC(),
					features.occlusionQueryPrecise(),
					features.pipelineStatisticsQuery(),
					features.vertexPipelineStoresAndAtomics(),
					features.fragmentStoresAndAtomics(),
					features.shaderTessellationAndGeometryPointSize(),
					features.shaderImageGatherExtended(),
					features.shaderStorageImageExtendedFormats(),
					features.shaderStorageImageMultisample(),
					features.shaderStorageImageReadWithoutFormat(),
					features.shaderStorageImageWriteWithoutFormat(),
					features.shaderUniformBufferArrayDynamicIndexing(),
					features.shaderSampledImageArrayDynamicIndexing(),
					features.shaderStorageBufferArrayDynamicIndexing(),
					features.shaderStorageImageArrayDynamicIndexing(),
					features.shaderClipDistance(),
					features.shaderCullDistance(),
					features.shaderFloat64(),
					features.shaderInt64(),
					features.shaderInt16(),
					features.shaderResourceResidency(),
					features.shaderResourceMinLod(),
					features.sparseBinding(),
					features.sparseResidencyBuffer(),
					features.sparseResidencyImage2D(),
					features.sparseResidencyImage3D(),
					features.sparseResidency2Samples(),
					features.sparseResidency4Samples(),
					features.sparseResidency8Samples(),
					features.sparseResidency16Samples(),
					features.sparseResidencyAliased(),
					features.variableMultisampleRate(),
					features.inheritedQueries()
			);
		}
	}

	public record VkPhysicalDeviceProperties(

			VkVersion apiVersion,
			int driverVersion,
			int vendorID,
			int deviceID,
			VkPhysicalDeviceType deviceType,
			String deviceName,
			UUID pipelineCacheUUID,
			VkPhysicalDeviceLimits limits,
			VkPhysicalDeviceSparseProperties sparseProperties

	) {

		public enum VkPhysicalDeviceType {

			OTHER(VK14.VK_PHYSICAL_DEVICE_TYPE_OTHER),
			DISCRETE(VK14.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU),
			INTEGRATED(VK14.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU),
			VIRTUAL(VK14.VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU),
			CPU(VK14.VK_PHYSICAL_DEVICE_TYPE_CPU)

			;

			final int vkInt;

			VkPhysicalDeviceType(int vkInt) {
				this.vkInt = vkInt;
			}

			public static VkPhysicalDeviceType getType(int vkInt) {
				for (VkPhysicalDeviceType t : values()) {
					if (t.vkInt == vkInt) return t;
				}

				throw new IllegalArgumentException("Could not find type that correlates to the vulkan int :" + vkInt);
			}
		}

		public record VkPhysicalDeviceLimits(
				int maxImageDimension1D,
				int maxImageDimension2D,
				int maxImageDimension3D,
				int maxImageDimensionCube,
				int maxImageArrayLayers,
				int maxTexelBufferElements,
				int maxUniformBufferRange,
				int maxStorageBufferRange,
				int maxPushConstantsSize,
				int maxMemoryAllocationCount,
				int maxSamplerAllocationCount,
				long bufferImageGranularity,
				long sparseAddressSpaceSize,
				int maxBoundDescriptorSets,
				int maxPerStageDescriptorSamplers,
				int maxPerStageDescriptorUniformBuffers,
				int maxPerStageDescriptorStorageBuffers,
				int maxPerStageDescriptorSampledImages,
				int maxPerStageDescriptorStorageImages,
				int maxPerStageDescriptorInputAttachments,
				int maxPerStageResources,
				int maxDescriptorSetSamplers,
				int maxDescriptorSetUniformBuffers,
				int maxDescriptorSetUniformBuffersDynamic,
				int maxDescriptorSetStorageBuffers,
				int maxDescriptorSetStorageBuffersDynamic,
				int maxDescriptorSetSampledImages,
				int maxDescriptorSetStorageImages,
				int maxDescriptorSetInputAttachments,
				int maxVertexInputAttributes,
				int maxVertexInputBindings,
				int maxVertexInputAttributeOffset,
				int maxVertexInputBindingStride,
				int maxVertexOutputComponents,
				int maxTessellationGenerationLevel,
				int maxTessellationPatchSize,
				int maxTessellationControlPerVertexInputComponents,
				int maxTessellationControlPerVertexOutputComponents,
				int maxTessellationControlPerPatchOutputComponents,
				int maxTessellationControlTotalOutputComponents,
				int maxTessellationEvaluationInputComponents,
				int maxTessellationEvaluationOutputComponents,
				int maxGeometryShaderInvocations,
				int maxGeometryInputComponents,
				int maxGeometryOutputComponents,
				int maxGeometryOutputVertices,
				int maxGeometryTotalOutputComponents,
				int maxFragmentInputComponents,
				int maxFragmentOutputAttachments,
				int maxFragmentDualSrcAttachments,
				int maxFragmentCombinedOutputResources,
				int maxComputeSharedMemorySize,
				Vector3i maxComputeWorkGroupCount,
				int maxComputeWorkGroupInvocations,
				Vector3i maxComputeWorkGroupSize,
				int subPixelPrecisionBits,
				int subTexelPrecisionBits,
				int mipMapPrecisionBits,
				int maxDrawIndexedIndexValue,
				int maxDrawIndirectCount,
				float maxSamplerLodBias,
				float maxSamplerAnisotropy,
				int maxViewports,
				Vector3i maxViewportDimensions,
				Vector3f viewportBoundsRange,
				int viewportSubPixelBits,
				long minMemoryMapAlignment,
				long minTexelBufferOffsetAlignment,
				long minUniformBufferOffsetAlignment,
				long minStorageBufferOffsetAlignment,
				int minTexelOffset,
				int maxTexelOffset,
				int minTexelGatherOffset,
				int maxTexelGatherOffset,
				float minInterpolationOffset,
				float maxInterpolationOffset,
				int subPixelInterpolationOffsetBits,
				int maxFramebufferWidth,
				int maxFramebufferHeight,
				int maxFramebufferLayers,
				int framebufferColorSampleCounts,
				int framebufferDepthSampleCounts,
				int framebufferStencilSampleCounts,
				int framebufferNoAttachmentsSampleCounts,
				int maxColorAttachments,
				int sampledImageColorSampleCounts,
				int sampledImageIntegerSampleCounts,
				int sampledImageDepthSampleCounts,
				int sampledImageStencilSampleCounts,
				int storageImageSampleCounts,
				int maxSampleMaskWords,
				boolean timestampComputeAndGraphics,
				float timestampPeriod,
				int maxClipDistances,
				int maxCullDistances,
				int maxCombinedClipAndCullDistances,
				int discreteQueuePriorities,
				Vector3f pointSizeRange,
				Vector3f lineWidthRange,
				float pointSizeGranularity,
				float lineWidthGranularity,
				boolean strictLines,
				boolean standardSampleLocations,
				long optimalBufferCopyOffsetAlignment,
				long optimalBufferCopyRowPitchAlignment,
				long nonCoherentAtomSize
		) {
			public static VkPhysicalDeviceLimits getFrom(org.lwjgl.vulkan.VkPhysicalDeviceLimits limits) {

				return new VkPhysicalDeviceLimits(
						limits.maxImageDimension1D(),
						limits.maxImageDimension2D(),
						limits.maxImageDimension3D(),
						limits.maxImageDimensionCube(),
						limits.maxImageArrayLayers(),
						limits.maxTexelBufferElements(),
						limits.maxUniformBufferRange(),
						limits.maxStorageBufferRange(),
						limits.maxPushConstantsSize(),
						limits.maxMemoryAllocationCount(),
						limits.maxSamplerAllocationCount(),
						limits.bufferImageGranularity(),
						limits.sparseAddressSpaceSize(),
						limits.maxBoundDescriptorSets(),
						limits.maxPerStageDescriptorSamplers(),
						limits.maxPerStageDescriptorUniformBuffers(),
						limits.maxPerStageDescriptorStorageBuffers(),
						limits.maxPerStageDescriptorSampledImages(),
						limits.maxPerStageDescriptorStorageImages(),
						limits.maxPerStageDescriptorInputAttachments(),
						limits.maxPerStageResources(),
						limits.maxDescriptorSetSamplers(),
						limits.maxDescriptorSetUniformBuffers(),
						limits.maxDescriptorSetUniformBuffersDynamic(),
						limits.maxDescriptorSetStorageBuffers(),
						limits.maxDescriptorSetStorageBuffersDynamic(),
						limits.maxDescriptorSetSampledImages(),
						limits.maxDescriptorSetStorageImages(),
						limits.maxDescriptorSetInputAttachments(),
						limits.maxVertexInputAttributes(),
						limits.maxVertexInputBindings(),
						limits.maxVertexInputAttributeOffset(),
						limits.maxVertexInputBindingStride(),
						limits.maxVertexOutputComponents(),
						limits.maxTessellationGenerationLevel(),
						limits.maxTessellationPatchSize(),
						limits.maxTessellationControlPerVertexInputComponents(),
						limits.maxTessellationControlPerVertexOutputComponents(),
						limits.maxTessellationControlPerPatchOutputComponents(),
						limits.maxTessellationControlTotalOutputComponents(),
						limits.maxTessellationEvaluationInputComponents(),
						limits.maxTessellationEvaluationOutputComponents(),
						limits.maxGeometryShaderInvocations(),
						limits.maxGeometryInputComponents(),
						limits.maxGeometryOutputComponents(),
						limits.maxGeometryOutputVertices(),
						limits.maxGeometryTotalOutputComponents(),
						limits.maxFragmentInputComponents(),
						limits.maxFragmentOutputAttachments(),
						limits.maxFragmentDualSrcAttachments(),
						limits.maxFragmentCombinedOutputResources(),
						limits.maxComputeSharedMemorySize(),
						new Vector3i(limits.maxComputeWorkGroupCount()),
						limits.maxComputeWorkGroupInvocations(),
						new Vector3i(limits.maxComputeWorkGroupSize()),
						limits.subPixelPrecisionBits(),
						limits.subTexelPrecisionBits(),
						limits.mipmapPrecisionBits(),
						limits.maxDrawIndexedIndexValue(),
						limits.maxDrawIndirectCount(),
						limits.maxSamplerLodBias(),
						limits.maxSamplerAnisotropy(),
						limits.maxViewports(),
						new Vector3i(limits.maxViewportDimensions()),
						new Vector3f(limits.viewportBoundsRange()),
						limits.viewportSubPixelBits(),
						limits.minMemoryMapAlignment(),
						limits.minTexelBufferOffsetAlignment(),
						limits.minUniformBufferOffsetAlignment(),
						limits.minStorageBufferOffsetAlignment(),
						limits.minTexelOffset(),
						limits.maxTexelOffset(),
						limits.minTexelGatherOffset(),
						limits.maxTexelGatherOffset(),
						limits.minInterpolationOffset(),
						limits.maxInterpolationOffset(),
						limits.subPixelInterpolationOffsetBits(),
						limits.maxFramebufferWidth(),
						limits.maxFramebufferHeight(),
						limits.maxFramebufferLayers(),
						limits.framebufferColorSampleCounts(),
						limits.framebufferDepthSampleCounts(),
						limits.framebufferStencilSampleCounts(),
						limits.framebufferNoAttachmentsSampleCounts(),
						limits.maxColorAttachments(),
						limits.sampledImageColorSampleCounts(),
						limits.sampledImageIntegerSampleCounts(),
						limits.sampledImageDepthSampleCounts(),
						limits.sampledImageStencilSampleCounts(),
						limits.storageImageSampleCounts(),
						limits.maxSampleMaskWords(),
						limits.timestampComputeAndGraphics(),
						limits.timestampPeriod(),
						limits.maxClipDistances(),
						limits.maxCullDistances(),
						limits.maxCombinedClipAndCullDistances(),
						limits.discreteQueuePriorities(),
						new Vector3f(limits.pointSizeRange()),
						new Vector3f(limits.lineWidthRange()),
						limits.pointSizeGranularity(),
						limits.lineWidthGranularity(),
						limits.strictLines(),
						limits.standardSampleLocations(),
						limits.optimalBufferCopyOffsetAlignment(),
						limits.optimalBufferCopyRowPitchAlignment(),
						limits.nonCoherentAtomSize()
				);
			}
		}

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


		public static VkPhysicalDeviceProperties getFrom(VkPhysicalDevice device) {
			try (MemoryStack stack = MemoryStack.stackPush()) {

				VkSurfaceFormatKHR formatKHR;

				org.lwjgl.vulkan.VkPhysicalDeviceProperties properties =
						org.lwjgl.vulkan.VkPhysicalDeviceProperties.calloc(stack);
				VK14.vkGetPhysicalDeviceProperties(device, properties);

				return new VkPhysicalDeviceProperties(
						new VkVersion(properties.apiVersion()),
						properties.driverVersion(),
						properties.vendorID(),
						properties.deviceID(),
						VkPhysicalDeviceType.getType(properties.deviceType()),
						properties.deviceNameString(),
						getUUID(properties),
						VkPhysicalDeviceLimits.getFrom(properties.limits()),
						VkPhysicalDeviceSparseProperties.getFrom(properties.sparseProperties())
				);
			}
		}

		public static UUID getUUID(org.lwjgl.vulkan.VkPhysicalDeviceProperties properties) {
			long MSB, LSB;
			ByteBuffer buffer = properties.pipelineCacheUUID();
			buffer.order(ByteOrder.BIG_ENDIAN);
			buffer.rewind();

			MSB = buffer.getLong();
			LSB = buffer.getLong();

			return new UUID(MSB, LSB);
		}

	}

}
