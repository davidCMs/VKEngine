package dev.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

public class VkPhysicalDeviceFeaturesBuilder extends PNextChainable {

	//V1.0
	private boolean robustBufferAccess;
	private boolean fullDrawIndexUint32;
	private boolean imageCubeArray;
	private boolean independentBlend;
	private boolean geometryShader;
	private boolean tessellationShader;
	private boolean sampleRateShading;
	private boolean dualSrcBlend;
	private boolean logicOp;
	private boolean multiDrawIndirect;
	private boolean drawIndirectFirstInstance;
	private boolean depthClamp;
	private boolean depthBiasClamp;
	private boolean fillModeNonSolid;
	private boolean depthBounds;
	private boolean wideLines;
	private boolean largePoints;
	private boolean alphaToOne;
	private boolean multiViewport;
	private boolean samplerAnisotropy;
	private boolean textureCompressionETC2;
	private boolean textureCompressionASTC_LDR;
	private boolean textureCompressionBC;
	private boolean occlusionQueryPrecise;
	private boolean pipelineStatisticsQuery;
	private boolean vertexPipelineStoresAndAtomics;
	private boolean fragmentStoresAndAtomics;
	private boolean shaderTessellationAndGeometryPointSize;
	private boolean shaderImageGatherExtended;
	private boolean shaderStorageImageExtendedFormats;
	private boolean shaderStorageImageMultisample;
	private boolean shaderStorageImageReadWithoutFormat;
	private boolean shaderStorageImageWriteWithoutFormat;
	private boolean shaderUniformBufferArrayDynamicIndexing;
	private boolean shaderSampledImageArrayDynamicIndexing;
	private boolean shaderStorageBufferArrayDynamicIndexing;
	private boolean shaderStorageImageArrayDynamicIndexing;
	private boolean shaderClipDistance;
	private boolean shaderCullDistance;
	private boolean shaderFloat64;
	private boolean shaderInt64;
	private boolean shaderInt16;
	private boolean shaderResourceResidency;
	private boolean shaderResourceMinLod;
	private boolean sparseBinding;
	private boolean sparseResidencyBuffer;
	private boolean sparseResidencyImage2D;
	private boolean sparseResidencyImage3D;
	private boolean sparseResidency2Samples;
	private boolean sparseResidency4Samples;
	private boolean sparseResidency8Samples;
	private boolean sparseResidency16Samples;
	private boolean sparseResidencyAliased;
	private boolean variableMultisampleRate;
	private boolean inheritedQueries;

	//V1.1
	private boolean storageBuffer16BitAccess;
	private boolean uniformAndStorageBuffer16BitAccess;
	private boolean storagePushConstant16;
	private boolean storageInputOutput16;
	private boolean multiview;
	private boolean multiviewGeometryShader;
	private boolean multiviewTessellationShader;
	private boolean variablePointersStorageBuffer;
	private boolean variablePointers;
	private boolean protectedMemory;
	private boolean samplerYcbcrConversion;
	private boolean shaderDrawParameters;

	//V1.2
	private boolean samplerMirrorClampToEdge;
	private boolean drawIndirectCount;
	private boolean storageBuffer8BitAccess;
	private boolean uniformAndStorageBuffer8BitAccess;
	private boolean storagePushConstant8;
	private boolean shaderBufferInt64Atomics;
	private boolean shaderSharedInt64Atomics;
	private boolean shaderFloat16;
	private boolean shaderInt8;
	private boolean descriptorIndexing;
	private boolean shaderInputAttachmentArrayDynamicIndexing;
	private boolean shaderUniformTexelBufferArrayDynamicIndexing;
	private boolean shaderStorageTexelBufferArrayDynamicIndexing;
	private boolean shaderUniformBufferArrayNonUniformIndexing;
	private boolean shaderSampledImageArrayNonUniformIndexing;
	private boolean shaderStorageBufferArrayNonUniformIndexing;
	private boolean shaderStorageImageArrayNonUniformIndexing;
	private boolean shaderInputAttachmentArrayNonUniformIndexing;
	private boolean shaderUniformTexelBufferArrayNonUniformIndexing;
	private boolean shaderStorageTexelBufferArrayNonUniformIndexing;
	private boolean descriptorBindingUniformBufferUpdateAfterBind;
	private boolean descriptorBindingSampledImageUpdateAfterBind;
	private boolean descriptorBindingStorageImageUpdateAfterBind;
	private boolean descriptorBindingStorageBufferUpdateAfterBind;
	private boolean descriptorBindingUniformTexelBufferUpdateAfterBind;
	private boolean descriptorBindingStorageTexelBufferUpdateAfterBind;
	private boolean descriptorBindingUpdateUnusedWhilePending;
	private boolean descriptorBindingPartiallyBound;
	private boolean descriptorBindingVariableDescriptorCount;
	private boolean runtimeDescriptorArray;
	private boolean samplerFilterMinmax;
	private boolean scalarBlockLayout;
	private boolean imagelessFramebuffer;
	private boolean uniformBufferStandardLayout;
	private boolean shaderSubgroupExtendedTypes;
	private boolean separateDepthStencilLayouts;
	private boolean hostQueryReset;
	private boolean timelineSemaphore;
	private boolean bufferDeviceAddress;
	private boolean bufferDeviceAddressCaptureReplay;
	private boolean bufferDeviceAddressMultiDevice;
	private boolean vulkanMemoryModel;
	private boolean vulkanMemoryModelDeviceScope;
	private boolean vulkanMemoryModelAvailabilityVisibilityChains;
	private boolean shaderOutputViewportIndex;
	private boolean shaderOutputLayer;
	private boolean subgroupBroadcastDynamicId;

	//V1.3
	private boolean robustImageAccess;
	private boolean inlineUniformBlock;
	private boolean descriptorBindingInlineUniformBlockUpdateAfterBind;
	private boolean pipelineCreationCacheControl;
	private boolean privateData;
	private boolean shaderDemoteToHelperInvocation;
	private boolean shaderTerminateInvocation;
	private boolean subgroupSizeControl;
	private boolean computeFullSubgroups;
	private boolean synchronization2;
	private boolean textureCompressionASTC_HDR;
	private boolean shaderZeroInitializeWorkgroupMemory;
	private boolean dynamicRendering;
	private boolean shaderIntegerDotProduct;
	private boolean maintenance4;

	//V1.4
	private boolean globalPriorityQuery;
	private boolean shaderSubgroupRotate;
	private boolean shaderSubgroupRotateClustered;
	private boolean shaderFloatControls2;
	private boolean shaderExpectAssume;
	private boolean rectangularLines;
	private boolean bresenhamLines;
	private boolean smoothLines;
	private boolean stippledRectangularLines;
	private boolean stippledBresenhamLines;
	private boolean stippledSmoothLines;
	private boolean vertexAttributeInstanceRateDivisor;
	private boolean vertexAttributeInstanceRateZeroDivisor;
	private boolean indexTypeUint8;
	private boolean dynamicRenderingLocalRead;
	private boolean maintenance5;
	private boolean maintenance6;
	private boolean pipelineProtectedAccess;
	private boolean pipelineRobustness;
	private boolean hostImageCopy;
	private boolean pushDescriptor;

	public VkPhysicalDeviceFeatures2 build(MemoryStack stack) {
		VkPhysicalDeviceFeatures f0 = VkPhysicalDeviceFeatures.calloc(stack);
		f0.robustBufferAccess(robustBufferAccess);
		f0.fullDrawIndexUint32(fullDrawIndexUint32);
		f0.imageCubeArray(imageCubeArray);
		f0.independentBlend(independentBlend);
		f0.geometryShader(geometryShader);
		f0.tessellationShader(tessellationShader);
		f0.sampleRateShading(sampleRateShading);
		f0.dualSrcBlend(dualSrcBlend);
		f0.logicOp(logicOp);
		f0.multiDrawIndirect(multiDrawIndirect);
		f0.drawIndirectFirstInstance(drawIndirectFirstInstance);
		f0.depthClamp(depthClamp);
		f0.depthBiasClamp(depthBiasClamp);
		f0.fillModeNonSolid(fillModeNonSolid);
		f0.depthBounds(depthBounds);
		f0.wideLines(wideLines);
		f0.largePoints(largePoints);
		f0.alphaToOne(alphaToOne);
		f0.multiViewport(multiViewport);
		f0.samplerAnisotropy(samplerAnisotropy);
		f0.textureCompressionETC2(textureCompressionETC2);
		f0.textureCompressionASTC_LDR(textureCompressionASTC_LDR);
		f0.textureCompressionBC(textureCompressionBC);
		f0.occlusionQueryPrecise(occlusionQueryPrecise);
		f0.pipelineStatisticsQuery(pipelineStatisticsQuery);
		f0.vertexPipelineStoresAndAtomics(vertexPipelineStoresAndAtomics);
		f0.fragmentStoresAndAtomics(fragmentStoresAndAtomics);
		f0.shaderTessellationAndGeometryPointSize(shaderTessellationAndGeometryPointSize);
		f0.shaderImageGatherExtended(shaderImageGatherExtended);
		f0.shaderStorageImageExtendedFormats(shaderStorageImageExtendedFormats);
		f0.shaderStorageImageMultisample(shaderStorageImageMultisample);
		f0.shaderStorageImageReadWithoutFormat(shaderStorageImageReadWithoutFormat);
		f0.shaderStorageImageWriteWithoutFormat(shaderStorageImageWriteWithoutFormat);
		f0.shaderUniformBufferArrayDynamicIndexing(shaderUniformBufferArrayDynamicIndexing);
		f0.shaderSampledImageArrayDynamicIndexing(shaderSampledImageArrayDynamicIndexing);
		f0.shaderStorageBufferArrayDynamicIndexing(shaderStorageBufferArrayDynamicIndexing);
		f0.shaderStorageImageArrayDynamicIndexing(shaderStorageImageArrayDynamicIndexing);
		f0.shaderClipDistance(shaderClipDistance);
		f0.shaderCullDistance(shaderCullDistance);
		f0.shaderFloat64(shaderFloat64);
		f0.shaderInt64(shaderInt64);
		f0.shaderInt16(shaderInt16);
		f0.shaderResourceResidency(shaderResourceResidency);
		f0.shaderResourceMinLod(shaderResourceMinLod);
		f0.sparseBinding(sparseBinding);
		f0.sparseResidencyBuffer(sparseResidencyBuffer);
		f0.sparseResidencyImage2D(sparseResidencyImage2D);
		f0.sparseResidencyImage3D(sparseResidencyImage3D);
		f0.sparseResidency2Samples(sparseResidency2Samples);
		f0.sparseResidency4Samples(sparseResidency4Samples);
		f0.sparseResidency8Samples(sparseResidency8Samples);
		f0.sparseResidency16Samples(sparseResidency16Samples);
		f0.sparseResidencyAliased(sparseResidencyAliased);
		f0.variableMultisampleRate(variableMultisampleRate);
		f0.inheritedQueries(inheritedQueries);

		VkPhysicalDeviceVulkan11Features f1 = VkPhysicalDeviceVulkan11Features.calloc(stack);
		f1.sType$Default();
		f1.storageBuffer16BitAccess(storageBuffer16BitAccess);
		f1.uniformAndStorageBuffer16BitAccess(uniformAndStorageBuffer16BitAccess);
		f1.storagePushConstant16(storagePushConstant16);
		f1.storageInputOutput16(storageInputOutput16);
		f1.multiview(multiview);
		f1.multiviewGeometryShader(multiviewGeometryShader);
		f1.multiviewTessellationShader(multiviewTessellationShader);
		f1.variablePointersStorageBuffer(variablePointersStorageBuffer);
		f1.variablePointers(variablePointers);
		f1.protectedMemory(protectedMemory);
		f1.samplerYcbcrConversion(samplerYcbcrConversion);
		f1.shaderDrawParameters(shaderDrawParameters);

		VkPhysicalDeviceVulkan12Features f2 = VkPhysicalDeviceVulkan12Features.calloc(stack);
		f2.sType$Default();
		f2.samplerMirrorClampToEdge(samplerMirrorClampToEdge);
		f2.drawIndirectCount(drawIndirectCount);
		f2.storageBuffer8BitAccess(storageBuffer8BitAccess);
		f2.uniformAndStorageBuffer8BitAccess(uniformAndStorageBuffer8BitAccess);
		f2.storagePushConstant8(storagePushConstant8);
		f2.shaderBufferInt64Atomics(shaderBufferInt64Atomics);
		f2.shaderSharedInt64Atomics(shaderSharedInt64Atomics);
		f2.shaderFloat16(shaderFloat16);
		f2.shaderInt8(shaderInt8);
		f2.descriptorIndexing(descriptorIndexing);
		f2.shaderInputAttachmentArrayDynamicIndexing(shaderInputAttachmentArrayDynamicIndexing);
		f2.shaderUniformTexelBufferArrayDynamicIndexing(shaderUniformTexelBufferArrayDynamicIndexing);
		f2.shaderStorageTexelBufferArrayDynamicIndexing(shaderStorageTexelBufferArrayDynamicIndexing);
		f2.shaderUniformBufferArrayNonUniformIndexing(shaderUniformBufferArrayNonUniformIndexing);
		f2.shaderSampledImageArrayNonUniformIndexing(shaderSampledImageArrayNonUniformIndexing);
		f2.shaderStorageBufferArrayNonUniformIndexing(shaderStorageBufferArrayNonUniformIndexing);
		f2.shaderStorageImageArrayNonUniformIndexing(shaderStorageImageArrayNonUniformIndexing);
		f2.shaderInputAttachmentArrayNonUniformIndexing(shaderInputAttachmentArrayNonUniformIndexing);
		f2.shaderUniformTexelBufferArrayNonUniformIndexing(shaderUniformTexelBufferArrayNonUniformIndexing);
		f2.shaderStorageTexelBufferArrayNonUniformIndexing(shaderStorageTexelBufferArrayNonUniformIndexing);
		f2.descriptorBindingUniformBufferUpdateAfterBind(descriptorBindingUniformBufferUpdateAfterBind);
		f2.descriptorBindingSampledImageUpdateAfterBind(descriptorBindingSampledImageUpdateAfterBind);
		f2.descriptorBindingStorageImageUpdateAfterBind(descriptorBindingStorageImageUpdateAfterBind);
		f2.descriptorBindingStorageBufferUpdateAfterBind(descriptorBindingStorageBufferUpdateAfterBind);
		f2.descriptorBindingUniformTexelBufferUpdateAfterBind(descriptorBindingUniformTexelBufferUpdateAfterBind);
		f2.descriptorBindingStorageTexelBufferUpdateAfterBind(descriptorBindingStorageTexelBufferUpdateAfterBind);
		f2.descriptorBindingUpdateUnusedWhilePending(descriptorBindingUpdateUnusedWhilePending);
		f2.descriptorBindingPartiallyBound(descriptorBindingPartiallyBound);
		f2.descriptorBindingVariableDescriptorCount(descriptorBindingVariableDescriptorCount);
		f2.runtimeDescriptorArray(runtimeDescriptorArray);
		f2.samplerFilterMinmax(samplerFilterMinmax);
		f2.scalarBlockLayout(scalarBlockLayout);
		f2.imagelessFramebuffer(imagelessFramebuffer);
		f2.uniformBufferStandardLayout(uniformBufferStandardLayout);
		f2.shaderSubgroupExtendedTypes(shaderSubgroupExtendedTypes);
		f2.separateDepthStencilLayouts(separateDepthStencilLayouts);
		f2.hostQueryReset(hostQueryReset);
		f2.timelineSemaphore(timelineSemaphore);
		f2.bufferDeviceAddress(bufferDeviceAddress);
		f2.bufferDeviceAddressCaptureReplay(bufferDeviceAddressCaptureReplay);
		f2.bufferDeviceAddressMultiDevice(bufferDeviceAddressMultiDevice);
		f2.vulkanMemoryModel(vulkanMemoryModel);
		f2.vulkanMemoryModelDeviceScope(vulkanMemoryModelDeviceScope);
		f2.vulkanMemoryModelAvailabilityVisibilityChains(vulkanMemoryModelAvailabilityVisibilityChains);
		f2.shaderOutputViewportIndex(shaderOutputViewportIndex);
		f2.shaderOutputLayer(shaderOutputLayer);
		f2.subgroupBroadcastDynamicId(subgroupBroadcastDynamicId);

		VkPhysicalDeviceVulkan13Features f3 = VkPhysicalDeviceVulkan13Features.calloc(stack);
		f3.sType$Default();
		f3.robustImageAccess(robustImageAccess);
		f3.inlineUniformBlock(inlineUniformBlock);
		f3.descriptorBindingInlineUniformBlockUpdateAfterBind(descriptorBindingInlineUniformBlockUpdateAfterBind);
		f3.pipelineCreationCacheControl(pipelineCreationCacheControl);
		f3.privateData(privateData);
		f3.shaderDemoteToHelperInvocation(shaderDemoteToHelperInvocation);
		f3.shaderTerminateInvocation(shaderTerminateInvocation);
		f3.subgroupSizeControl(subgroupSizeControl);
		f3.computeFullSubgroups(computeFullSubgroups);
		f3.synchronization2(synchronization2);
		f3.textureCompressionASTC_HDR(textureCompressionASTC_HDR);
		f3.shaderZeroInitializeWorkgroupMemory(shaderZeroInitializeWorkgroupMemory);
		f3.dynamicRendering(dynamicRendering);
		f3.shaderIntegerDotProduct(shaderIntegerDotProduct);
		f3.maintenance4(maintenance4);
		f3.pNext(getNextpNext(stack));

		VkPhysicalDeviceVulkan14Features f4 = VkPhysicalDeviceVulkan14Features.calloc(stack);
		f4.sType$Default();
		f4.globalPriorityQuery(globalPriorityQuery);
		f4.shaderSubgroupRotate(shaderSubgroupRotate);
		f4.shaderSubgroupRotateClustered(shaderSubgroupRotateClustered);
		f4.shaderFloatControls2(shaderFloatControls2);
		f4.shaderExpectAssume(shaderExpectAssume);
		f4.rectangularLines(rectangularLines);
		f4.bresenhamLines(bresenhamLines);
		f4.smoothLines(smoothLines);
		f4.stippledRectangularLines(stippledRectangularLines);
		f4.stippledBresenhamLines(stippledBresenhamLines);
		f4.stippledSmoothLines(stippledSmoothLines);
		f4.vertexAttributeInstanceRateDivisor(vertexAttributeInstanceRateDivisor);
		f4.vertexAttributeInstanceRateZeroDivisor(vertexAttributeInstanceRateZeroDivisor);
		f4.indexTypeUint8(indexTypeUint8);
		f4.dynamicRenderingLocalRead(dynamicRenderingLocalRead);
		f4.maintenance5(maintenance5);
		f4.maintenance6(maintenance6);
		f4.pipelineProtectedAccess(pipelineProtectedAccess);
		f4.pipelineRobustness(pipelineRobustness);
		f4.hostImageCopy(hostImageCopy);
		f4.pushDescriptor(pushDescriptor);

		VkPhysicalDeviceFeatures2 ret = VkPhysicalDeviceFeatures2.calloc(stack);
		ret.sType$Default();
		ret.features(f0);

		ret.pNext(f1.address());
		f1.pNext(f2.address());
		f2.pNext(f3.address());
		f3.pNext(f4.address());
		if (pNext != null)
			f4.pNext(pNext.getNextpNext(stack));

		return ret;
	}

	@Override
	public long getpNext(MemoryStack stack) {
		return build(stack).address();
	}

	@Override
	public VkPhysicalDeviceFeaturesBuilder copy() {
		VkPhysicalDeviceFeaturesBuilder builder = new VkPhysicalDeviceFeaturesBuilder();
		builder.robustBufferAccess = robustBufferAccess;
		builder.fullDrawIndexUint32 = fullDrawIndexUint32;
		builder.imageCubeArray = imageCubeArray;
		builder.independentBlend = independentBlend;
		builder.geometryShader = geometryShader;
		builder.tessellationShader = tessellationShader;
		builder.sampleRateShading = sampleRateShading;
		builder.dualSrcBlend = dualSrcBlend;
		builder.logicOp = logicOp;
		builder.multiDrawIndirect = multiDrawIndirect;
		builder.drawIndirectFirstInstance = drawIndirectFirstInstance;
		builder.depthClamp = depthClamp;
		builder.depthBiasClamp = depthBiasClamp;
		builder.fillModeNonSolid = fillModeNonSolid;
		builder.depthBounds = depthBounds;
		builder.wideLines = wideLines;
		builder.largePoints = largePoints;
		builder.alphaToOne = alphaToOne;
		builder.multiViewport = multiViewport;
		builder.samplerAnisotropy = samplerAnisotropy;
		builder.textureCompressionETC2 = textureCompressionETC2;
		builder.textureCompressionASTC_LDR = textureCompressionASTC_LDR;
		builder.textureCompressionBC = textureCompressionBC;
		builder.occlusionQueryPrecise = occlusionQueryPrecise;
		builder.pipelineStatisticsQuery = pipelineStatisticsQuery;
		builder.vertexPipelineStoresAndAtomics = vertexPipelineStoresAndAtomics;
		builder.fragmentStoresAndAtomics = fragmentStoresAndAtomics;
		builder.shaderTessellationAndGeometryPointSize = shaderTessellationAndGeometryPointSize;
		builder.shaderImageGatherExtended = shaderImageGatherExtended;
		builder.shaderStorageImageExtendedFormats = shaderStorageImageExtendedFormats;
		builder.shaderStorageImageMultisample = shaderStorageImageMultisample;
		builder.shaderStorageImageReadWithoutFormat = shaderStorageImageReadWithoutFormat;
		builder.shaderStorageImageWriteWithoutFormat = shaderStorageImageWriteWithoutFormat;
		builder.shaderUniformBufferArrayDynamicIndexing = shaderUniformBufferArrayDynamicIndexing;
		builder.shaderSampledImageArrayDynamicIndexing = shaderSampledImageArrayDynamicIndexing;
		builder.shaderStorageBufferArrayDynamicIndexing = shaderStorageBufferArrayDynamicIndexing;
		builder.shaderStorageImageArrayDynamicIndexing = shaderStorageImageArrayDynamicIndexing;
		builder.shaderClipDistance = shaderClipDistance;
		builder.shaderCullDistance = shaderCullDistance;
		builder.shaderFloat64 = shaderFloat64;
		builder.shaderInt64 = shaderInt64;
		builder.shaderInt16 = shaderInt16;
		builder.shaderResourceResidency = shaderResourceResidency;
		builder.shaderResourceMinLod = shaderResourceMinLod;
		builder.sparseBinding = sparseBinding;
		builder.sparseResidencyBuffer = sparseResidencyBuffer;
		builder.sparseResidencyImage2D = sparseResidencyImage2D;
		builder.sparseResidencyImage3D = sparseResidencyImage3D;
		builder.sparseResidency2Samples = sparseResidency2Samples;
		builder.sparseResidency4Samples = sparseResidency4Samples;
		builder.sparseResidency8Samples = sparseResidency8Samples;
		builder.sparseResidency16Samples = sparseResidency16Samples;
		builder.sparseResidencyAliased = sparseResidencyAliased;
		builder.variableMultisampleRate = variableMultisampleRate;
		builder.inheritedQueries = inheritedQueries;
		builder.storageBuffer16BitAccess = storageBuffer16BitAccess;
		builder.uniformAndStorageBuffer16BitAccess = uniformAndStorageBuffer16BitAccess;
		builder.storagePushConstant16 = storagePushConstant16;
		builder.storageInputOutput16 = storageInputOutput16;
		builder.multiview = multiview;
		builder.multiviewGeometryShader = multiviewGeometryShader;
		builder.multiviewTessellationShader = multiviewTessellationShader;
		builder.variablePointersStorageBuffer = variablePointersStorageBuffer;
		builder.variablePointers = variablePointers;
		builder.protectedMemory = protectedMemory;
		builder.samplerYcbcrConversion = samplerYcbcrConversion;
		builder.shaderDrawParameters = shaderDrawParameters;
		builder.samplerMirrorClampToEdge = samplerMirrorClampToEdge;
		builder.drawIndirectCount = drawIndirectCount;
		builder.storageBuffer8BitAccess = storageBuffer8BitAccess;
		builder.uniformAndStorageBuffer8BitAccess = uniformAndStorageBuffer8BitAccess;
		builder.storagePushConstant8 = storagePushConstant8;
		builder.shaderBufferInt64Atomics = shaderBufferInt64Atomics;
		builder.shaderSharedInt64Atomics = shaderSharedInt64Atomics;
		builder.shaderFloat16 = shaderFloat16;
		builder.shaderInt8 = shaderInt8;
		builder.descriptorIndexing = descriptorIndexing;
		builder.shaderInputAttachmentArrayDynamicIndexing = shaderInputAttachmentArrayDynamicIndexing;
		builder.shaderUniformTexelBufferArrayDynamicIndexing = shaderUniformTexelBufferArrayDynamicIndexing;
		builder.shaderStorageTexelBufferArrayDynamicIndexing = shaderStorageTexelBufferArrayDynamicIndexing;
		builder.shaderUniformBufferArrayNonUniformIndexing = shaderUniformBufferArrayNonUniformIndexing;
		builder.shaderSampledImageArrayNonUniformIndexing = shaderSampledImageArrayNonUniformIndexing;
		builder.shaderStorageBufferArrayNonUniformIndexing = shaderStorageBufferArrayNonUniformIndexing;
		builder.shaderStorageImageArrayNonUniformIndexing = shaderStorageImageArrayNonUniformIndexing;
		builder.shaderInputAttachmentArrayNonUniformIndexing = shaderInputAttachmentArrayNonUniformIndexing;
		builder.shaderUniformTexelBufferArrayNonUniformIndexing = shaderUniformTexelBufferArrayNonUniformIndexing;
		builder.shaderStorageTexelBufferArrayNonUniformIndexing = shaderStorageTexelBufferArrayNonUniformIndexing;
		builder.descriptorBindingUniformBufferUpdateAfterBind = descriptorBindingUniformBufferUpdateAfterBind;
		builder.descriptorBindingSampledImageUpdateAfterBind = descriptorBindingSampledImageUpdateAfterBind;
		builder.descriptorBindingStorageImageUpdateAfterBind = descriptorBindingStorageImageUpdateAfterBind;
		builder.descriptorBindingStorageBufferUpdateAfterBind = descriptorBindingStorageBufferUpdateAfterBind;
		builder.descriptorBindingUniformTexelBufferUpdateAfterBind = descriptorBindingUniformTexelBufferUpdateAfterBind;
		builder.descriptorBindingStorageTexelBufferUpdateAfterBind = descriptorBindingStorageTexelBufferUpdateAfterBind;
		builder.descriptorBindingUpdateUnusedWhilePending = descriptorBindingUpdateUnusedWhilePending;
		builder.descriptorBindingPartiallyBound = descriptorBindingPartiallyBound;
		builder.descriptorBindingVariableDescriptorCount = descriptorBindingVariableDescriptorCount;
		builder.runtimeDescriptorArray = runtimeDescriptorArray;
		builder.samplerFilterMinmax = samplerFilterMinmax;
		builder.scalarBlockLayout = scalarBlockLayout;
		builder.imagelessFramebuffer = imagelessFramebuffer;
		builder.uniformBufferStandardLayout = uniformBufferStandardLayout;
		builder.shaderSubgroupExtendedTypes = shaderSubgroupExtendedTypes;
		builder.separateDepthStencilLayouts = separateDepthStencilLayouts;
		builder.hostQueryReset = hostQueryReset;
		builder.timelineSemaphore = timelineSemaphore;
		builder.bufferDeviceAddress = bufferDeviceAddress;
		builder.bufferDeviceAddressCaptureReplay = bufferDeviceAddressCaptureReplay;
		builder.bufferDeviceAddressMultiDevice = bufferDeviceAddressMultiDevice;
		builder.vulkanMemoryModel = vulkanMemoryModel;
		builder.vulkanMemoryModelDeviceScope = vulkanMemoryModelDeviceScope;
		builder.vulkanMemoryModelAvailabilityVisibilityChains = vulkanMemoryModelAvailabilityVisibilityChains;
		builder.shaderOutputViewportIndex = shaderOutputViewportIndex;
		builder.shaderOutputLayer = shaderOutputLayer;
		builder.subgroupBroadcastDynamicId = subgroupBroadcastDynamicId;
		builder.robustImageAccess = robustImageAccess;
		builder.inlineUniformBlock = inlineUniformBlock;
		builder.descriptorBindingInlineUniformBlockUpdateAfterBind = descriptorBindingInlineUniformBlockUpdateAfterBind;
		builder.pipelineCreationCacheControl = pipelineCreationCacheControl;
		builder.privateData = privateData;
		builder.shaderDemoteToHelperInvocation = shaderDemoteToHelperInvocation;
		builder.shaderTerminateInvocation = shaderTerminateInvocation;
		builder.subgroupSizeControl = subgroupSizeControl;
		builder.computeFullSubgroups = computeFullSubgroups;
		builder.synchronization2 = synchronization2;
		builder.textureCompressionASTC_HDR = textureCompressionASTC_HDR;
		builder.shaderZeroInitializeWorkgroupMemory = shaderZeroInitializeWorkgroupMemory;
		builder.dynamicRendering = dynamicRendering;
		builder.shaderIntegerDotProduct = shaderIntegerDotProduct;
		builder.maintenance4 = maintenance4;
		builder.globalPriorityQuery = globalPriorityQuery;
		builder.shaderSubgroupRotate = shaderSubgroupRotate;
		builder.shaderSubgroupRotateClustered = shaderSubgroupRotateClustered;
		builder.shaderFloatControls2 = shaderFloatControls2;
		builder.shaderExpectAssume = shaderExpectAssume;
		builder.rectangularLines = rectangularLines;
		builder.bresenhamLines = bresenhamLines;
		builder.smoothLines = smoothLines;
		builder.stippledRectangularLines = stippledRectangularLines;
		builder.stippledBresenhamLines = stippledBresenhamLines;
		builder.stippledSmoothLines = stippledSmoothLines;
		builder.vertexAttributeInstanceRateDivisor = vertexAttributeInstanceRateDivisor;
		builder.vertexAttributeInstanceRateZeroDivisor = vertexAttributeInstanceRateZeroDivisor;
		builder.indexTypeUint8 = indexTypeUint8;
		builder.dynamicRenderingLocalRead = dynamicRenderingLocalRead;
		builder.maintenance5 = maintenance5;
		builder.maintenance6 = maintenance6;
		builder.pipelineProtectedAccess = pipelineProtectedAccess;
		builder.pipelineRobustness = pipelineRobustness;
		builder.hostImageCopy = hostImageCopy;
		builder.pushDescriptor = pushDescriptor;
		return builder;
	}

	public boolean isRobustBufferAccess() {
		return robustBufferAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setRobustBufferAccess(boolean robustBufferAccess) {
		this.robustBufferAccess = robustBufferAccess;
		return this;
	}

	public boolean isFullDrawIndexUint32() {
		return fullDrawIndexUint32;
	}

	public VkPhysicalDeviceFeaturesBuilder setFullDrawIndexUint32(boolean fullDrawIndexUint32) {
		this.fullDrawIndexUint32 = fullDrawIndexUint32;
		return this;
	}

	public boolean isImageCubeArray() {
		return imageCubeArray;
	}

	public VkPhysicalDeviceFeaturesBuilder setImageCubeArray(boolean imageCubeArray) {
		this.imageCubeArray = imageCubeArray;
		return this;
	}

	public boolean isIndependentBlend() {
		return independentBlend;
	}

	public VkPhysicalDeviceFeaturesBuilder setIndependentBlend(boolean independentBlend) {
		this.independentBlend = independentBlend;
		return this;
	}

	public boolean isGeometryShader() {
		return geometryShader;
	}

	public VkPhysicalDeviceFeaturesBuilder setGeometryShader(boolean geometryShader) {
		this.geometryShader = geometryShader;
		return this;
	}

	public boolean isTessellationShader() {
		return tessellationShader;
	}

	public VkPhysicalDeviceFeaturesBuilder setTessellationShader(boolean tessellationShader) {
		this.tessellationShader = tessellationShader;
		return this;
	}

	public boolean isSampleRateShading() {
		return sampleRateShading;
	}

	public VkPhysicalDeviceFeaturesBuilder setSampleRateShading(boolean sampleRateShading) {
		this.sampleRateShading = sampleRateShading;
		return this;
	}

	public boolean isDualSrcBlend() {
		return dualSrcBlend;
	}

	public VkPhysicalDeviceFeaturesBuilder setDualSrcBlend(boolean dualSrcBlend) {
		this.dualSrcBlend = dualSrcBlend;
		return this;
	}

	public boolean isLogicOp() {
		return logicOp;
	}

	public VkPhysicalDeviceFeaturesBuilder setLogicOp(boolean logicOp) {
		this.logicOp = logicOp;
		return this;
	}

	public boolean isMultiDrawIndirect() {
		return multiDrawIndirect;
	}

	public VkPhysicalDeviceFeaturesBuilder setMultiDrawIndirect(boolean multiDrawIndirect) {
		this.multiDrawIndirect = multiDrawIndirect;
		return this;
	}

	public boolean isDrawIndirectFirstInstance() {
		return drawIndirectFirstInstance;
	}

	public VkPhysicalDeviceFeaturesBuilder setDrawIndirectFirstInstance(boolean drawIndirectFirstInstance) {
		this.drawIndirectFirstInstance = drawIndirectFirstInstance;
		return this;
	}

	public boolean isDepthClamp() {
		return depthClamp;
	}

	public VkPhysicalDeviceFeaturesBuilder setDepthClamp(boolean depthClamp) {
		this.depthClamp = depthClamp;
		return this;
	}

	public boolean isDepthBiasClamp() {
		return depthBiasClamp;
	}

	public VkPhysicalDeviceFeaturesBuilder setDepthBiasClamp(boolean depthBiasClamp) {
		this.depthBiasClamp = depthBiasClamp;
		return this;
	}

	public boolean isFillModeNonSolid() {
		return fillModeNonSolid;
	}

	public VkPhysicalDeviceFeaturesBuilder setFillModeNonSolid(boolean fillModeNonSolid) {
		this.fillModeNonSolid = fillModeNonSolid;
		return this;
	}

	public boolean isDepthBounds() {
		return depthBounds;
	}

	public VkPhysicalDeviceFeaturesBuilder setDepthBounds(boolean depthBounds) {
		this.depthBounds = depthBounds;
		return this;
	}

	public boolean isWideLines() {
		return wideLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setWideLines(boolean wideLines) {
		this.wideLines = wideLines;
		return this;
	}

	public boolean isLargePoints() {
		return largePoints;
	}

	public VkPhysicalDeviceFeaturesBuilder setLargePoints(boolean largePoints) {
		this.largePoints = largePoints;
		return this;
	}

	public boolean isAlphaToOne() {
		return alphaToOne;
	}

	public VkPhysicalDeviceFeaturesBuilder setAlphaToOne(boolean alphaToOne) {
		this.alphaToOne = alphaToOne;
		return this;
	}

	public boolean isMultiViewport() {
		return multiViewport;
	}

	public VkPhysicalDeviceFeaturesBuilder setMultiViewport(boolean multiViewport) {
		this.multiViewport = multiViewport;
		return this;
	}

	public boolean isSamplerAnisotropy() {
		return samplerAnisotropy;
	}

	public VkPhysicalDeviceFeaturesBuilder setSamplerAnisotropy(boolean samplerAnisotropy) {
		this.samplerAnisotropy = samplerAnisotropy;
		return this;
	}

	public boolean isTextureCompressionETC2() {
		return textureCompressionETC2;
	}

	public VkPhysicalDeviceFeaturesBuilder setTextureCompressionETC2(boolean textureCompressionETC2) {
		this.textureCompressionETC2 = textureCompressionETC2;
		return this;
	}

	public boolean isTextureCompressionASTC_LDR() {
		return textureCompressionASTC_LDR;
	}

	public VkPhysicalDeviceFeaturesBuilder setTextureCompressionASTC_LDR(boolean textureCompressionASTC_LDR) {
		this.textureCompressionASTC_LDR = textureCompressionASTC_LDR;
		return this;
	}

	public boolean isTextureCompressionBC() {
		return textureCompressionBC;
	}

	public VkPhysicalDeviceFeaturesBuilder setTextureCompressionBC(boolean textureCompressionBC) {
		this.textureCompressionBC = textureCompressionBC;
		return this;
	}

	public boolean isOcclusionQueryPrecise() {
		return occlusionQueryPrecise;
	}

	public VkPhysicalDeviceFeaturesBuilder setOcclusionQueryPrecise(boolean occlusionQueryPrecise) {
		this.occlusionQueryPrecise = occlusionQueryPrecise;
		return this;
	}

	public boolean isPipelineStatisticsQuery() {
		return pipelineStatisticsQuery;
	}

	public VkPhysicalDeviceFeaturesBuilder setPipelineStatisticsQuery(boolean pipelineStatisticsQuery) {
		this.pipelineStatisticsQuery = pipelineStatisticsQuery;
		return this;
	}

	public boolean isVertexPipelineStoresAndAtomics() {
		return vertexPipelineStoresAndAtomics;
	}

	public VkPhysicalDeviceFeaturesBuilder setVertexPipelineStoresAndAtomics(boolean vertexPipelineStoresAndAtomics) {
		this.vertexPipelineStoresAndAtomics = vertexPipelineStoresAndAtomics;
		return this;
	}

	public boolean isFragmentStoresAndAtomics() {
		return fragmentStoresAndAtomics;
	}

	public VkPhysicalDeviceFeaturesBuilder setFragmentStoresAndAtomics(boolean fragmentStoresAndAtomics) {
		this.fragmentStoresAndAtomics = fragmentStoresAndAtomics;
		return this;
	}

	public boolean isShaderTessellationAndGeometryPointSize() {
		return shaderTessellationAndGeometryPointSize;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderTessellationAndGeometryPointSize(boolean shaderTessellationAndGeometryPointSize) {
		this.shaderTessellationAndGeometryPointSize = shaderTessellationAndGeometryPointSize;
		return this;
	}

	public boolean isShaderImageGatherExtended() {
		return shaderImageGatherExtended;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderImageGatherExtended(boolean shaderImageGatherExtended) {
		this.shaderImageGatherExtended = shaderImageGatherExtended;
		return this;
	}

	public boolean isShaderStorageImageExtendedFormats() {
		return shaderStorageImageExtendedFormats;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageExtendedFormats(boolean shaderStorageImageExtendedFormats) {
		this.shaderStorageImageExtendedFormats = shaderStorageImageExtendedFormats;
		return this;
	}

	public boolean isShaderStorageImageMultisample() {
		return shaderStorageImageMultisample;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageMultisample(boolean shaderStorageImageMultisample) {
		this.shaderStorageImageMultisample = shaderStorageImageMultisample;
		return this;
	}

	public boolean isShaderStorageImageReadWithoutFormat() {
		return shaderStorageImageReadWithoutFormat;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageReadWithoutFormat(boolean shaderStorageImageReadWithoutFormat) {
		this.shaderStorageImageReadWithoutFormat = shaderStorageImageReadWithoutFormat;
		return this;
	}

	public boolean isShaderStorageImageWriteWithoutFormat() {
		return shaderStorageImageWriteWithoutFormat;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageWriteWithoutFormat(boolean shaderStorageImageWriteWithoutFormat) {
		this.shaderStorageImageWriteWithoutFormat = shaderStorageImageWriteWithoutFormat;
		return this;
	}

	public boolean isShaderUniformBufferArrayDynamicIndexing() {
		return shaderUniformBufferArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderUniformBufferArrayDynamicIndexing(boolean shaderUniformBufferArrayDynamicIndexing) {
		this.shaderUniformBufferArrayDynamicIndexing = shaderUniformBufferArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderSampledImageArrayDynamicIndexing() {
		return shaderSampledImageArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSampledImageArrayDynamicIndexing(boolean shaderSampledImageArrayDynamicIndexing) {
		this.shaderSampledImageArrayDynamicIndexing = shaderSampledImageArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderStorageBufferArrayDynamicIndexing() {
		return shaderStorageBufferArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageBufferArrayDynamicIndexing(boolean shaderStorageBufferArrayDynamicIndexing) {
		this.shaderStorageBufferArrayDynamicIndexing = shaderStorageBufferArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderStorageImageArrayDynamicIndexing() {
		return shaderStorageImageArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageArrayDynamicIndexing(boolean shaderStorageImageArrayDynamicIndexing) {
		this.shaderStorageImageArrayDynamicIndexing = shaderStorageImageArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderClipDistance() {
		return shaderClipDistance;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderClipDistance(boolean shaderClipDistance) {
		this.shaderClipDistance = shaderClipDistance;
		return this;
	}

	public boolean isShaderCullDistance() {
		return shaderCullDistance;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderCullDistance(boolean shaderCullDistance) {
		this.shaderCullDistance = shaderCullDistance;
		return this;
	}

	public boolean isShaderFloat64() {
		return shaderFloat64;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderFloat64(boolean shaderFloat64) {
		this.shaderFloat64 = shaderFloat64;
		return this;
	}

	public boolean isShaderInt64() {
		return shaderInt64;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderInt64(boolean shaderInt64) {
		this.shaderInt64 = shaderInt64;
		return this;
	}

	public boolean isShaderInt16() {
		return shaderInt16;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderInt16(boolean shaderInt16) {
		this.shaderInt16 = shaderInt16;
		return this;
	}

	public boolean isShaderResourceResidency() {
		return shaderResourceResidency;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderResourceResidency(boolean shaderResourceResidency) {
		this.shaderResourceResidency = shaderResourceResidency;
		return this;
	}

	public boolean isShaderResourceMinLod() {
		return shaderResourceMinLod;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderResourceMinLod(boolean shaderResourceMinLod) {
		this.shaderResourceMinLod = shaderResourceMinLod;
		return this;
	}

	public boolean isSparseBinding() {
		return sparseBinding;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseBinding(boolean sparseBinding) {
		this.sparseBinding = sparseBinding;
		return this;
	}

	public boolean isSparseResidencyBuffer() {
		return sparseResidencyBuffer;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidencyBuffer(boolean sparseResidencyBuffer) {
		this.sparseResidencyBuffer = sparseResidencyBuffer;
		return this;
	}

	public boolean isSparseResidencyImage2D() {
		return sparseResidencyImage2D;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidencyImage2D(boolean sparseResidencyImage2D) {
		this.sparseResidencyImage2D = sparseResidencyImage2D;
		return this;
	}

	public boolean isSparseResidencyImage3D() {
		return sparseResidencyImage3D;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidencyImage3D(boolean sparseResidencyImage3D) {
		this.sparseResidencyImage3D = sparseResidencyImage3D;
		return this;
	}

	public boolean isSparseResidency2Samples() {
		return sparseResidency2Samples;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidency2Samples(boolean sparseResidency2Samples) {
		this.sparseResidency2Samples = sparseResidency2Samples;
		return this;
	}

	public boolean isSparseResidency4Samples() {
		return sparseResidency4Samples;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidency4Samples(boolean sparseResidency4Samples) {
		this.sparseResidency4Samples = sparseResidency4Samples;
		return this;
	}

	public boolean isSparseResidency8Samples() {
		return sparseResidency8Samples;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidency8Samples(boolean sparseResidency8Samples) {
		this.sparseResidency8Samples = sparseResidency8Samples;
		return this;
	}

	public boolean isSparseResidency16Samples() {
		return sparseResidency16Samples;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidency16Samples(boolean sparseResidency16Samples) {
		this.sparseResidency16Samples = sparseResidency16Samples;
		return this;
	}

	public boolean isSparseResidencyAliased() {
		return sparseResidencyAliased;
	}

	public VkPhysicalDeviceFeaturesBuilder setSparseResidencyAliased(boolean sparseResidencyAliased) {
		this.sparseResidencyAliased = sparseResidencyAliased;
		return this;
	}

	public boolean isVariableMultisampleRate() {
		return variableMultisampleRate;
	}

	public VkPhysicalDeviceFeaturesBuilder setVariableMultisampleRate(boolean variableMultisampleRate) {
		this.variableMultisampleRate = variableMultisampleRate;
		return this;
	}

	public boolean isInheritedQueries() {
		return inheritedQueries;
	}

	public VkPhysicalDeviceFeaturesBuilder setInheritedQueries(boolean inheritedQueries) {
		this.inheritedQueries = inheritedQueries;
		return this;
	}

	public boolean isStorageBuffer16BitAccess() {
		return storageBuffer16BitAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setStorageBuffer16BitAccess(boolean storageBuffer16BitAccess) {
		this.storageBuffer16BitAccess = storageBuffer16BitAccess;
		return this;
	}

	public boolean isUniformAndStorageBuffer16BitAccess() {
		return uniformAndStorageBuffer16BitAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setUniformAndStorageBuffer16BitAccess(boolean uniformAndStorageBuffer16BitAccess) {
		this.uniformAndStorageBuffer16BitAccess = uniformAndStorageBuffer16BitAccess;
		return this;
	}

	public boolean isStoragePushConstant16() {
		return storagePushConstant16;
	}

	public VkPhysicalDeviceFeaturesBuilder setStoragePushConstant16(boolean storagePushConstant16) {
		this.storagePushConstant16 = storagePushConstant16;
		return this;
	}

	public boolean isStorageInputOutput16() {
		return storageInputOutput16;
	}

	public VkPhysicalDeviceFeaturesBuilder setStorageInputOutput16(boolean storageInputOutput16) {
		this.storageInputOutput16 = storageInputOutput16;
		return this;
	}

	public boolean isMultiview() {
		return multiview;
	}

	public VkPhysicalDeviceFeaturesBuilder setMultiview(boolean multiview) {
		this.multiview = multiview;
		return this;
	}

	public boolean isMultiviewGeometryShader() {
		return multiviewGeometryShader;
	}

	public VkPhysicalDeviceFeaturesBuilder setMultiviewGeometryShader(boolean multiviewGeometryShader) {
		this.multiviewGeometryShader = multiviewGeometryShader;
		return this;
	}

	public boolean isMultiviewTessellationShader() {
		return multiviewTessellationShader;
	}

	public VkPhysicalDeviceFeaturesBuilder setMultiviewTessellationShader(boolean multiviewTessellationShader) {
		this.multiviewTessellationShader = multiviewTessellationShader;
		return this;
	}

	public boolean isVariablePointersStorageBuffer() {
		return variablePointersStorageBuffer;
	}

	public VkPhysicalDeviceFeaturesBuilder setVariablePointersStorageBuffer(boolean variablePointersStorageBuffer) {
		this.variablePointersStorageBuffer = variablePointersStorageBuffer;
		return this;
	}

	public boolean isVariablePointers() {
		return variablePointers;
	}

	public VkPhysicalDeviceFeaturesBuilder setVariablePointers(boolean variablePointers) {
		this.variablePointers = variablePointers;
		return this;
	}

	public boolean isProtectedMemory() {
		return protectedMemory;
	}

	public VkPhysicalDeviceFeaturesBuilder setProtectedMemory(boolean protectedMemory) {
		this.protectedMemory = protectedMemory;
		return this;
	}

	public boolean isSamplerYcbcrConversion() {
		return samplerYcbcrConversion;
	}

	public VkPhysicalDeviceFeaturesBuilder setSamplerYcbcrConversion(boolean samplerYcbcrConversion) {
		this.samplerYcbcrConversion = samplerYcbcrConversion;
		return this;
	}

	public boolean isShaderDrawParameters() {
		return shaderDrawParameters;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderDrawParameters(boolean shaderDrawParameters) {
		this.shaderDrawParameters = shaderDrawParameters;
		return this;
	}

	public boolean isSamplerMirrorClampToEdge() {
		return samplerMirrorClampToEdge;
	}

	public VkPhysicalDeviceFeaturesBuilder setSamplerMirrorClampToEdge(boolean samplerMirrorClampToEdge) {
		this.samplerMirrorClampToEdge = samplerMirrorClampToEdge;
		return this;
	}

	public boolean isDrawIndirectCount() {
		return drawIndirectCount;
	}

	public VkPhysicalDeviceFeaturesBuilder setDrawIndirectCount(boolean drawIndirectCount) {
		this.drawIndirectCount = drawIndirectCount;
		return this;
	}

	public boolean isStorageBuffer8BitAccess() {
		return storageBuffer8BitAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setStorageBuffer8BitAccess(boolean storageBuffer8BitAccess) {
		this.storageBuffer8BitAccess = storageBuffer8BitAccess;
		return this;
	}

	public boolean isUniformAndStorageBuffer8BitAccess() {
		return uniformAndStorageBuffer8BitAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setUniformAndStorageBuffer8BitAccess(boolean uniformAndStorageBuffer8BitAccess) {
		this.uniformAndStorageBuffer8BitAccess = uniformAndStorageBuffer8BitAccess;
		return this;
	}

	public boolean isStoragePushConstant8() {
		return storagePushConstant8;
	}

	public VkPhysicalDeviceFeaturesBuilder setStoragePushConstant8(boolean storagePushConstant8) {
		this.storagePushConstant8 = storagePushConstant8;
		return this;
	}

	public boolean isShaderBufferInt64Atomics() {
		return shaderBufferInt64Atomics;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderBufferInt64Atomics(boolean shaderBufferInt64Atomics) {
		this.shaderBufferInt64Atomics = shaderBufferInt64Atomics;
		return this;
	}

	public boolean isShaderSharedInt64Atomics() {
		return shaderSharedInt64Atomics;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSharedInt64Atomics(boolean shaderSharedInt64Atomics) {
		this.shaderSharedInt64Atomics = shaderSharedInt64Atomics;
		return this;
	}

	public boolean isShaderFloat16() {
		return shaderFloat16;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderFloat16(boolean shaderFloat16) {
		this.shaderFloat16 = shaderFloat16;
		return this;
	}

	public boolean isShaderInt8() {
		return shaderInt8;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderInt8(boolean shaderInt8) {
		this.shaderInt8 = shaderInt8;
		return this;
	}

	public boolean isDescriptorIndexing() {
		return descriptorIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorIndexing(boolean descriptorIndexing) {
		this.descriptorIndexing = descriptorIndexing;
		return this;
	}

	public boolean isShaderInputAttachmentArrayDynamicIndexing() {
		return shaderInputAttachmentArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderInputAttachmentArrayDynamicIndexing(boolean shaderInputAttachmentArrayDynamicIndexing) {
		this.shaderInputAttachmentArrayDynamicIndexing = shaderInputAttachmentArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderUniformTexelBufferArrayDynamicIndexing() {
		return shaderUniformTexelBufferArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderUniformTexelBufferArrayDynamicIndexing(boolean shaderUniformTexelBufferArrayDynamicIndexing) {
		this.shaderUniformTexelBufferArrayDynamicIndexing = shaderUniformTexelBufferArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderStorageTexelBufferArrayDynamicIndexing() {
		return shaderStorageTexelBufferArrayDynamicIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageTexelBufferArrayDynamicIndexing(boolean shaderStorageTexelBufferArrayDynamicIndexing) {
		this.shaderStorageTexelBufferArrayDynamicIndexing = shaderStorageTexelBufferArrayDynamicIndexing;
		return this;
	}

	public boolean isShaderUniformBufferArrayNonUniformIndexing() {
		return shaderUniformBufferArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderUniformBufferArrayNonUniformIndexing(boolean shaderUniformBufferArrayNonUniformIndexing) {
		this.shaderUniformBufferArrayNonUniformIndexing = shaderUniformBufferArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderSampledImageArrayNonUniformIndexing() {
		return shaderSampledImageArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSampledImageArrayNonUniformIndexing(boolean shaderSampledImageArrayNonUniformIndexing) {
		this.shaderSampledImageArrayNonUniformIndexing = shaderSampledImageArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderStorageBufferArrayNonUniformIndexing() {
		return shaderStorageBufferArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageBufferArrayNonUniformIndexing(boolean shaderStorageBufferArrayNonUniformIndexing) {
		this.shaderStorageBufferArrayNonUniformIndexing = shaderStorageBufferArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderStorageImageArrayNonUniformIndexing() {
		return shaderStorageImageArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageImageArrayNonUniformIndexing(boolean shaderStorageImageArrayNonUniformIndexing) {
		this.shaderStorageImageArrayNonUniformIndexing = shaderStorageImageArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderInputAttachmentArrayNonUniformIndexing() {
		return shaderInputAttachmentArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderInputAttachmentArrayNonUniformIndexing(boolean shaderInputAttachmentArrayNonUniformIndexing) {
		this.shaderInputAttachmentArrayNonUniformIndexing = shaderInputAttachmentArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderUniformTexelBufferArrayNonUniformIndexing() {
		return shaderUniformTexelBufferArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderUniformTexelBufferArrayNonUniformIndexing(boolean shaderUniformTexelBufferArrayNonUniformIndexing) {
		this.shaderUniformTexelBufferArrayNonUniformIndexing = shaderUniformTexelBufferArrayNonUniformIndexing;
		return this;
	}

	public boolean isShaderStorageTexelBufferArrayNonUniformIndexing() {
		return shaderStorageTexelBufferArrayNonUniformIndexing;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderStorageTexelBufferArrayNonUniformIndexing(boolean shaderStorageTexelBufferArrayNonUniformIndexing) {
		this.shaderStorageTexelBufferArrayNonUniformIndexing = shaderStorageTexelBufferArrayNonUniformIndexing;
		return this;
	}

	public boolean isDescriptorBindingUniformBufferUpdateAfterBind() {
		return descriptorBindingUniformBufferUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingUniformBufferUpdateAfterBind(boolean descriptorBindingUniformBufferUpdateAfterBind) {
		this.descriptorBindingUniformBufferUpdateAfterBind = descriptorBindingUniformBufferUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingSampledImageUpdateAfterBind() {
		return descriptorBindingSampledImageUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingSampledImageUpdateAfterBind(boolean descriptorBindingSampledImageUpdateAfterBind) {
		this.descriptorBindingSampledImageUpdateAfterBind = descriptorBindingSampledImageUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingStorageImageUpdateAfterBind() {
		return descriptorBindingStorageImageUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingStorageImageUpdateAfterBind(boolean descriptorBindingStorageImageUpdateAfterBind) {
		this.descriptorBindingStorageImageUpdateAfterBind = descriptorBindingStorageImageUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingStorageBufferUpdateAfterBind() {
		return descriptorBindingStorageBufferUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingStorageBufferUpdateAfterBind(boolean descriptorBindingStorageBufferUpdateAfterBind) {
		this.descriptorBindingStorageBufferUpdateAfterBind = descriptorBindingStorageBufferUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingUniformTexelBufferUpdateAfterBind() {
		return descriptorBindingUniformTexelBufferUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingUniformTexelBufferUpdateAfterBind(boolean descriptorBindingUniformTexelBufferUpdateAfterBind) {
		this.descriptorBindingUniformTexelBufferUpdateAfterBind = descriptorBindingUniformTexelBufferUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingStorageTexelBufferUpdateAfterBind() {
		return descriptorBindingStorageTexelBufferUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingStorageTexelBufferUpdateAfterBind(boolean descriptorBindingStorageTexelBufferUpdateAfterBind) {
		this.descriptorBindingStorageTexelBufferUpdateAfterBind = descriptorBindingStorageTexelBufferUpdateAfterBind;
		return this;
	}

	public boolean isDescriptorBindingUpdateUnusedWhilePending() {
		return descriptorBindingUpdateUnusedWhilePending;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingUpdateUnusedWhilePending(boolean descriptorBindingUpdateUnusedWhilePending) {
		this.descriptorBindingUpdateUnusedWhilePending = descriptorBindingUpdateUnusedWhilePending;
		return this;
	}

	public boolean isDescriptorBindingPartiallyBound() {
		return descriptorBindingPartiallyBound;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingPartiallyBound(boolean descriptorBindingPartiallyBound) {
		this.descriptorBindingPartiallyBound = descriptorBindingPartiallyBound;
		return this;
	}

	public boolean isDescriptorBindingVariableDescriptorCount() {
		return descriptorBindingVariableDescriptorCount;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingVariableDescriptorCount(boolean descriptorBindingVariableDescriptorCount) {
		this.descriptorBindingVariableDescriptorCount = descriptorBindingVariableDescriptorCount;
		return this;
	}

	public boolean isRuntimeDescriptorArray() {
		return runtimeDescriptorArray;
	}

	public VkPhysicalDeviceFeaturesBuilder setRuntimeDescriptorArray(boolean runtimeDescriptorArray) {
		this.runtimeDescriptorArray = runtimeDescriptorArray;
		return this;
	}

	public boolean isSamplerFilterMinmax() {
		return samplerFilterMinmax;
	}

	public VkPhysicalDeviceFeaturesBuilder setSamplerFilterMinmax(boolean samplerFilterMinmax) {
		this.samplerFilterMinmax = samplerFilterMinmax;
		return this;
	}

	public boolean isScalarBlockLayout() {
		return scalarBlockLayout;
	}

	public VkPhysicalDeviceFeaturesBuilder setScalarBlockLayout(boolean scalarBlockLayout) {
		this.scalarBlockLayout = scalarBlockLayout;
		return this;
	}

	public boolean isImagelessFramebuffer() {
		return imagelessFramebuffer;
	}

	public VkPhysicalDeviceFeaturesBuilder setImagelessFramebuffer(boolean imagelessFramebuffer) {
		this.imagelessFramebuffer = imagelessFramebuffer;
		return this;
	}

	public boolean isUniformBufferStandardLayout() {
		return uniformBufferStandardLayout;
	}

	public VkPhysicalDeviceFeaturesBuilder setUniformBufferStandardLayout(boolean uniformBufferStandardLayout) {
		this.uniformBufferStandardLayout = uniformBufferStandardLayout;
		return this;
	}

	public boolean isShaderSubgroupExtendedTypes() {
		return shaderSubgroupExtendedTypes;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSubgroupExtendedTypes(boolean shaderSubgroupExtendedTypes) {
		this.shaderSubgroupExtendedTypes = shaderSubgroupExtendedTypes;
		return this;
	}

	public boolean isSeparateDepthStencilLayouts() {
		return separateDepthStencilLayouts;
	}

	public VkPhysicalDeviceFeaturesBuilder setSeparateDepthStencilLayouts(boolean separateDepthStencilLayouts) {
		this.separateDepthStencilLayouts = separateDepthStencilLayouts;
		return this;
	}

	public boolean isHostQueryReset() {
		return hostQueryReset;
	}

	public VkPhysicalDeviceFeaturesBuilder setHostQueryReset(boolean hostQueryReset) {
		this.hostQueryReset = hostQueryReset;
		return this;
	}

	public boolean isTimelineSemaphore() {
		return timelineSemaphore;
	}

	public VkPhysicalDeviceFeaturesBuilder setTimelineSemaphore(boolean timelineSemaphore) {
		this.timelineSemaphore = timelineSemaphore;
		return this;
	}

	public boolean isBufferDeviceAddress() {
		return bufferDeviceAddress;
	}

	public VkPhysicalDeviceFeaturesBuilder setBufferDeviceAddress(boolean bufferDeviceAddress) {
		this.bufferDeviceAddress = bufferDeviceAddress;
		return this;
	}

	public boolean isBufferDeviceAddressCaptureReplay() {
		return bufferDeviceAddressCaptureReplay;
	}

	public VkPhysicalDeviceFeaturesBuilder setBufferDeviceAddressCaptureReplay(boolean bufferDeviceAddressCaptureReplay) {
		this.bufferDeviceAddressCaptureReplay = bufferDeviceAddressCaptureReplay;
		return this;
	}

	public boolean isBufferDeviceAddressMultiDevice() {
		return bufferDeviceAddressMultiDevice;
	}

	public VkPhysicalDeviceFeaturesBuilder setBufferDeviceAddressMultiDevice(boolean bufferDeviceAddressMultiDevice) {
		this.bufferDeviceAddressMultiDevice = bufferDeviceAddressMultiDevice;
		return this;
	}

	public boolean isVulkanMemoryModel() {
		return vulkanMemoryModel;
	}

	public VkPhysicalDeviceFeaturesBuilder setVulkanMemoryModel(boolean vulkanMemoryModel) {
		this.vulkanMemoryModel = vulkanMemoryModel;
		return this;
	}

	public boolean isVulkanMemoryModelDeviceScope() {
		return vulkanMemoryModelDeviceScope;
	}

	public VkPhysicalDeviceFeaturesBuilder setVulkanMemoryModelDeviceScope(boolean vulkanMemoryModelDeviceScope) {
		this.vulkanMemoryModelDeviceScope = vulkanMemoryModelDeviceScope;
		return this;
	}

	public boolean isVulkanMemoryModelAvailabilityVisibilityChains() {
		return vulkanMemoryModelAvailabilityVisibilityChains;
	}

	public VkPhysicalDeviceFeaturesBuilder setVulkanMemoryModelAvailabilityVisibilityChains(boolean vulkanMemoryModelAvailabilityVisibilityChains) {
		this.vulkanMemoryModelAvailabilityVisibilityChains = vulkanMemoryModelAvailabilityVisibilityChains;
		return this;
	}

	public boolean isShaderOutputViewportIndex() {
		return shaderOutputViewportIndex;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderOutputViewportIndex(boolean shaderOutputViewportIndex) {
		this.shaderOutputViewportIndex = shaderOutputViewportIndex;
		return this;
	}

	public boolean isShaderOutputLayer() {
		return shaderOutputLayer;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderOutputLayer(boolean shaderOutputLayer) {
		this.shaderOutputLayer = shaderOutputLayer;
		return this;
	}

	public boolean isSubgroupBroadcastDynamicId() {
		return subgroupBroadcastDynamicId;
	}

	public VkPhysicalDeviceFeaturesBuilder setSubgroupBroadcastDynamicId(boolean subgroupBroadcastDynamicId) {
		this.subgroupBroadcastDynamicId = subgroupBroadcastDynamicId;
		return this;
	}

	public boolean isRobustImageAccess() {
		return robustImageAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setRobustImageAccess(boolean robustImageAccess) {
		this.robustImageAccess = robustImageAccess;
		return this;
	}

	public boolean isInlineUniformBlock() {
		return inlineUniformBlock;
	}

	public VkPhysicalDeviceFeaturesBuilder setInlineUniformBlock(boolean inlineUniformBlock) {
		this.inlineUniformBlock = inlineUniformBlock;
		return this;
	}

	public boolean isDescriptorBindingInlineUniformBlockUpdateAfterBind() {
		return descriptorBindingInlineUniformBlockUpdateAfterBind;
	}

	public VkPhysicalDeviceFeaturesBuilder setDescriptorBindingInlineUniformBlockUpdateAfterBind(boolean descriptorBindingInlineUniformBlockUpdateAfterBind) {
		this.descriptorBindingInlineUniformBlockUpdateAfterBind = descriptorBindingInlineUniformBlockUpdateAfterBind;
		return this;
	}

	public boolean isPipelineCreationCacheControl() {
		return pipelineCreationCacheControl;
	}

	public VkPhysicalDeviceFeaturesBuilder setPipelineCreationCacheControl(boolean pipelineCreationCacheControl) {
		this.pipelineCreationCacheControl = pipelineCreationCacheControl;
		return this;
	}

	public boolean isPrivateData() {
		return privateData;
	}

	public VkPhysicalDeviceFeaturesBuilder setPrivateData(boolean privateData) {
		this.privateData = privateData;
		return this;
	}

	public boolean isShaderDemoteToHelperInvocation() {
		return shaderDemoteToHelperInvocation;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderDemoteToHelperInvocation(boolean shaderDemoteToHelperInvocation) {
		this.shaderDemoteToHelperInvocation = shaderDemoteToHelperInvocation;
		return this;
	}

	public boolean isShaderTerminateInvocation() {
		return shaderTerminateInvocation;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderTerminateInvocation(boolean shaderTerminateInvocation) {
		this.shaderTerminateInvocation = shaderTerminateInvocation;
		return this;
	}

	public boolean isSubgroupSizeControl() {
		return subgroupSizeControl;
	}

	public VkPhysicalDeviceFeaturesBuilder setSubgroupSizeControl(boolean subgroupSizeControl) {
		this.subgroupSizeControl = subgroupSizeControl;
		return this;
	}

	public boolean isComputeFullSubgroups() {
		return computeFullSubgroups;
	}

	public VkPhysicalDeviceFeaturesBuilder setComputeFullSubgroups(boolean computeFullSubgroups) {
		this.computeFullSubgroups = computeFullSubgroups;
		return this;
	}

	public boolean isSynchronization2() {
		return synchronization2;
	}

	public VkPhysicalDeviceFeaturesBuilder setSynchronization2(boolean synchronization2) {
		this.synchronization2 = synchronization2;
		return this;
	}

	public boolean isTextureCompressionASTC_HDR() {
		return textureCompressionASTC_HDR;
	}

	public VkPhysicalDeviceFeaturesBuilder setTextureCompressionASTC_HDR(boolean textureCompressionASTC_HDR) {
		this.textureCompressionASTC_HDR = textureCompressionASTC_HDR;
		return this;
	}

	public boolean isShaderZeroInitializeWorkgroupMemory() {
		return shaderZeroInitializeWorkgroupMemory;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderZeroInitializeWorkgroupMemory(boolean shaderZeroInitializeWorkgroupMemory) {
		this.shaderZeroInitializeWorkgroupMemory = shaderZeroInitializeWorkgroupMemory;
		return this;
	}

	public boolean isDynamicRendering() {
		return dynamicRendering;
	}

	public VkPhysicalDeviceFeaturesBuilder setDynamicRendering(boolean dynamicRendering) {
		this.dynamicRendering = dynamicRendering;
		return this;
	}

	public boolean isShaderIntegerDotProduct() {
		return shaderIntegerDotProduct;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderIntegerDotProduct(boolean shaderIntegerDotProduct) {
		this.shaderIntegerDotProduct = shaderIntegerDotProduct;
		return this;
	}

	public boolean isMaintenance4() {
		return maintenance4;
	}

	public VkPhysicalDeviceFeaturesBuilder setMaintenance4(boolean maintenance4) {
		this.maintenance4 = maintenance4;
		return this;
	}

	public boolean isGlobalPriorityQuery() {
		return globalPriorityQuery;
	}

	public VkPhysicalDeviceFeaturesBuilder setGlobalPriorityQuery(boolean globalPriorityQuery) {
		this.globalPriorityQuery = globalPriorityQuery;
		return this;
	}

	public boolean isShaderSubgroupRotate() {
		return shaderSubgroupRotate;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSubgroupRotate(boolean shaderSubgroupRotate) {
		this.shaderSubgroupRotate = shaderSubgroupRotate;
		return this;
	}

	public boolean isShaderSubgroupRotateClustered() {
		return shaderSubgroupRotateClustered;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderSubgroupRotateClustered(boolean shaderSubgroupRotateClustered) {
		this.shaderSubgroupRotateClustered = shaderSubgroupRotateClustered;
		return this;
	}

	public boolean isShaderFloatControls2() {
		return shaderFloatControls2;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderFloatControls2(boolean shaderFloatControls2) {
		this.shaderFloatControls2 = shaderFloatControls2;
		return this;
	}

	public boolean isShaderExpectAssume() {
		return shaderExpectAssume;
	}

	public VkPhysicalDeviceFeaturesBuilder setShaderExpectAssume(boolean shaderExpectAssume) {
		this.shaderExpectAssume = shaderExpectAssume;
		return this;
	}

	public boolean isRectangularLines() {
		return rectangularLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setRectangularLines(boolean rectangularLines) {
		this.rectangularLines = rectangularLines;
		return this;
	}

	public boolean isBresenhamLines() {
		return bresenhamLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setBresenhamLines(boolean bresenhamLines) {
		this.bresenhamLines = bresenhamLines;
		return this;
	}

	public boolean isSmoothLines() {
		return smoothLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setSmoothLines(boolean smoothLines) {
		this.smoothLines = smoothLines;
		return this;
	}

	public boolean isStippledRectangularLines() {
		return stippledRectangularLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setStippledRectangularLines(boolean stippledRectangularLines) {
		this.stippledRectangularLines = stippledRectangularLines;
		return this;
	}

	public boolean isStippledBresenhamLines() {
		return stippledBresenhamLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setStippledBresenhamLines(boolean stippledBresenhamLines) {
		this.stippledBresenhamLines = stippledBresenhamLines;
		return this;
	}

	public boolean isStippledSmoothLines() {
		return stippledSmoothLines;
	}

	public VkPhysicalDeviceFeaturesBuilder setStippledSmoothLines(boolean stippledSmoothLines) {
		this.stippledSmoothLines = stippledSmoothLines;
		return this;
	}

	public boolean isVertexAttributeInstanceRateDivisor() {
		return vertexAttributeInstanceRateDivisor;
	}

	public VkPhysicalDeviceFeaturesBuilder setVertexAttributeInstanceRateDivisor(boolean vertexAttributeInstanceRateDivisor) {
		this.vertexAttributeInstanceRateDivisor = vertexAttributeInstanceRateDivisor;
		return this;
	}

	public boolean isVertexAttributeInstanceRateZeroDivisor() {
		return vertexAttributeInstanceRateZeroDivisor;
	}

	public VkPhysicalDeviceFeaturesBuilder setVertexAttributeInstanceRateZeroDivisor(boolean vertexAttributeInstanceRateZeroDivisor) {
		this.vertexAttributeInstanceRateZeroDivisor = vertexAttributeInstanceRateZeroDivisor;
		return this;
	}

	public boolean isIndexTypeUint8() {
		return indexTypeUint8;
	}

	public VkPhysicalDeviceFeaturesBuilder setIndexTypeUint8(boolean indexTypeUint8) {
		this.indexTypeUint8 = indexTypeUint8;
		return this;
	}

	public boolean isDynamicRenderingLocalRead() {
		return dynamicRenderingLocalRead;
	}

	public VkPhysicalDeviceFeaturesBuilder setDynamicRenderingLocalRead(boolean dynamicRenderingLocalRead) {
		this.dynamicRenderingLocalRead = dynamicRenderingLocalRead;
		return this;
	}

	public boolean isMaintenance5() {
		return maintenance5;
	}

	public VkPhysicalDeviceFeaturesBuilder setMaintenance5(boolean maintenance5) {
		this.maintenance5 = maintenance5;
		return this;
	}

	public boolean isMaintenance6() {
		return maintenance6;
	}

	public VkPhysicalDeviceFeaturesBuilder setMaintenance6(boolean maintenance6) {
		this.maintenance6 = maintenance6;
		return this;
	}

	public boolean isPipelineProtectedAccess() {
		return pipelineProtectedAccess;
	}

	public VkPhysicalDeviceFeaturesBuilder setPipelineProtectedAccess(boolean pipelineProtectedAccess) {
		this.pipelineProtectedAccess = pipelineProtectedAccess;
		return this;
	}

	public boolean isPipelineRobustness() {
		return pipelineRobustness;
	}

	public VkPhysicalDeviceFeaturesBuilder setPipelineRobustness(boolean pipelineRobustness) {
		this.pipelineRobustness = pipelineRobustness;
		return this;
	}

	public boolean isHostImageCopy() {
		return hostImageCopy;
	}

	public VkPhysicalDeviceFeaturesBuilder setHostImageCopy(boolean hostImageCopy) {
		this.hostImageCopy = hostImageCopy;
		return this;
	}

	public boolean isPushDescriptor() {
		return pushDescriptor;
	}

	public VkPhysicalDeviceFeaturesBuilder setPushDescriptor(boolean pushDescriptor) {
		this.pushDescriptor = pushDescriptor;
		return this;
	}
}
