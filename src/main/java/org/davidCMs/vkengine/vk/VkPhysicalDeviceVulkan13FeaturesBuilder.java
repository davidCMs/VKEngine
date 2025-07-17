package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan13Features;

public class VkPhysicalDeviceVulkan13FeaturesBuilder extends PNextChainable {

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

	public VkPhysicalDeviceVulkan13Features build(MemoryStack stack) {
		VkPhysicalDeviceVulkan13Features info = VkPhysicalDeviceVulkan13Features.calloc(stack);
		info.sType$Default();
		info.robustImageAccess(robustImageAccess);
		info.inlineUniformBlock(inlineUniformBlock);
		info.descriptorBindingInlineUniformBlockUpdateAfterBind(descriptorBindingInlineUniformBlockUpdateAfterBind);
		info.pipelineCreationCacheControl(pipelineCreationCacheControl);
		info.privateData(privateData);
		info.shaderDemoteToHelperInvocation(shaderDemoteToHelperInvocation);
		info.shaderTerminateInvocation(shaderTerminateInvocation);
		info.subgroupSizeControl(subgroupSizeControl);
		info.computeFullSubgroups(computeFullSubgroups);
		info.synchronization2(synchronization2);
		info.textureCompressionASTC_HDR(textureCompressionASTC_HDR);
		info.shaderZeroInitializeWorkgroupMemory(shaderZeroInitializeWorkgroupMemory);
		info.dynamicRendering(dynamicRendering);
		info.shaderIntegerDotProduct(shaderIntegerDotProduct);
		info.maintenance4(maintenance4);

		return info;
	}

	@Override
	public long getpNext(MemoryStack stack) {
		return build(stack).address();
	}

	@Override
	public VkPhysicalDeviceVulkan13FeaturesBuilder copy() {
		return new VkPhysicalDeviceVulkan13FeaturesBuilder()
				.setRobustImageAccess(robustImageAccess)
				.setInlineUniformBlock(inlineUniformBlock)
				.setDescriptorBindingInlineUniformBlockUpdateAfterBind(descriptorBindingInlineUniformBlockUpdateAfterBind)
				.setPipelineCreationCacheControl(pipelineCreationCacheControl)
				.setPrivateData(privateData)
				.setShaderDemoteToHelperInvocation(shaderDemoteToHelperInvocation)
				.setShaderTerminateInvocation(shaderTerminateInvocation)
				.setSubgroupSizeControl(subgroupSizeControl)
				.setComputeFullSubgroups(computeFullSubgroups)
				.setSynchronization2(synchronization2)
				.setTextureCompressionASTC_HDR(textureCompressionASTC_HDR)
				.setShaderZeroInitializeWorkgroupMemory(shaderZeroInitializeWorkgroupMemory)
				.setDynamicRendering(dynamicRendering)
				.setShaderIntegerDotProduct(shaderIntegerDotProduct)
				.setMaintenance4(maintenance4);
	}

	public boolean isRobustImageAccess() {
		return robustImageAccess;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setRobustImageAccess(boolean robustImageAccess) {
		this.robustImageAccess = robustImageAccess;
		return this;
	}

	public boolean isInlineUniformBlock() {
		return inlineUniformBlock;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setInlineUniformBlock(boolean inlineUniformBlock) {
		this.inlineUniformBlock = inlineUniformBlock;
		return this;
	}

	public boolean isDescriptorBindingInlineUniformBlockUpdateAfterBind() {
		return descriptorBindingInlineUniformBlockUpdateAfterBind;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setDescriptorBindingInlineUniformBlockUpdateAfterBind(boolean descriptorBindingInlineUniformBlockUpdateAfterBind) {
		this.descriptorBindingInlineUniformBlockUpdateAfterBind = descriptorBindingInlineUniformBlockUpdateAfterBind;
		return this;
	}

	public boolean isPipelineCreationCacheControl() {
		return pipelineCreationCacheControl;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setPipelineCreationCacheControl(boolean pipelineCreationCacheControl) {
		this.pipelineCreationCacheControl = pipelineCreationCacheControl;
		return this;
	}

	public boolean isPrivateData() {
		return privateData;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setPrivateData(boolean privateData) {
		this.privateData = privateData;
		return this;
	}

	public boolean isShaderDemoteToHelperInvocation() {
		return shaderDemoteToHelperInvocation;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setShaderDemoteToHelperInvocation(boolean shaderDemoteToHelperInvocation) {
		this.shaderDemoteToHelperInvocation = shaderDemoteToHelperInvocation;
		return this;
	}

	public boolean isShaderTerminateInvocation() {
		return shaderTerminateInvocation;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setShaderTerminateInvocation(boolean shaderTerminateInvocation) {
		this.shaderTerminateInvocation = shaderTerminateInvocation;
		return this;
	}

	public boolean isSubgroupSizeControl() {
		return subgroupSizeControl;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setSubgroupSizeControl(boolean subgroupSizeControl) {
		this.subgroupSizeControl = subgroupSizeControl;
		return this;
	}

	public boolean isComputeFullSubgroups() {
		return computeFullSubgroups;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setComputeFullSubgroups(boolean computeFullSubgroups) {
		this.computeFullSubgroups = computeFullSubgroups;
		return this;
	}

	public boolean isSynchronization2() {
		return synchronization2;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setSynchronization2(boolean synchronization2) {
		this.synchronization2 = synchronization2;
		return this;
	}

	public boolean isTextureCompressionASTC_HDR() {
		return textureCompressionASTC_HDR;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setTextureCompressionASTC_HDR(boolean textureCompressionASTC_HDR) {
		this.textureCompressionASTC_HDR = textureCompressionASTC_HDR;
		return this;
	}

	public boolean isShaderZeroInitializeWorkgroupMemory() {
		return shaderZeroInitializeWorkgroupMemory;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setShaderZeroInitializeWorkgroupMemory(boolean shaderZeroInitializeWorkgroupMemory) {
		this.shaderZeroInitializeWorkgroupMemory = shaderZeroInitializeWorkgroupMemory;
		return this;
	}

	public boolean isDynamicRendering() {
		return dynamicRendering;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setDynamicRendering(boolean dynamicRendering) {
		this.dynamicRendering = dynamicRendering;
		return this;
	}

	public boolean isShaderIntegerDotProduct() {
		return shaderIntegerDotProduct;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setShaderIntegerDotProduct(boolean shaderIntegerDotProduct) {
		this.shaderIntegerDotProduct = shaderIntegerDotProduct;
		return this;
	}

	public boolean isMaintenance4() {
		return maintenance4;
	}

	public VkPhysicalDeviceVulkan13FeaturesBuilder setMaintenance4(boolean maintenance4) {
		this.maintenance4 = maintenance4;
		return this;
	}
}
