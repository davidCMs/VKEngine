package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.BufUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;

public class VkInstanceBuilder {

	String applicationName = "App";
	String engineName = "Engine";

	VkEVersion applicationVersion = new VkEVersion(1, 1, 0, 0);
	VkEVersion engineVersion = new VkEVersion(1, 1, 0, 0);

	Set<String> enabledLayers = new HashSet<>();
	Set<String> enabledExtensions = new HashSet<>();

	VkEDebugMessengerCallback messengerCallback = VkEDebugMessengerCallback.defaultCallBack;
	Set<VkEDebugMessageSeverity> debugMessageSeverities = new HashSet<>();
	Set<VkEDebugMessageType> debugMessageTypes = new HashSet<>();

	public VkInstance build() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkInstanceCreateInfo info = VkInstanceCreateInfo.calloc(stack)
					.pApplicationInfo(
							VkApplicationInfo.calloc(stack)
									.apiVersion(VK14.VK_API_VERSION_1_4)
									.pApplicationName(stack.UTF8(applicationName))
									.applicationVersion(applicationVersion.makeVersion())
									.pEngineName(stack.UTF8(engineName))
									.engineVersion(engineVersion.makeVersion()))
					.pNext(VkDebugUtilsMessengerCreateInfoEXT.calloc(stack)
							.messageSeverity(VkEDebugMessageSeverity.getValueOf(debugMessageSeverities))
							.messageType(VkEDebugMessageType.getValueOf(debugMessageTypes))
							.pfnUserCallback(new VkEInternalDebugMessengerCallback(messengerCallback)))
					.ppEnabledLayerNames(BufUtil.stringsToPointerBuffer(stack, enabledLayers))
					.ppEnabledExtensionNames(BufUtil.stringsToPointerBuffer(stack, enabledExtensions));

			PointerBuffer pb = stack.callocPointer(0);

			int err = 0;
			err = VK14.vkCreateInstance(info, null, pb);
			if (err != VK14.VK_SUCCESS)
				throw new VkEFailedToCreateInstanceException("Failed to create instance err code: " + err);

			return new VkInstance(pb.get(0), info);
		}
	}

	public Set<VkEDebugMessageType> getDebugMessageTypes() {
		return debugMessageTypes;
	}

	public VkInstanceBuilder setDebugMessageTypes(Set<VkEDebugMessageType> debugMessageTypes) {
		this.debugMessageTypes = debugMessageTypes;
		return this;
	}

	public VkInstanceBuilder setDebugMessageTypes(VkEDebugMessageType... debugMessageTypes) {
		return setDebugMessageTypes(Set.of(debugMessageTypes));
	}

	public Set<VkEDebugMessageSeverity> getDebugMessageSeverities() {
		return debugMessageSeverities;
	}

	public VkInstanceBuilder setDebugMessageSeverities(Set<VkEDebugMessageSeverity> debugMessageSeverities) {
		this.debugMessageSeverities = debugMessageSeverities;
		return this;
	}

	public VkInstanceBuilder setDebugMessageSeverities(VkEDebugMessageSeverity... debugMessageSeverities) {
		return setDebugMessageSeverities(Set.of(debugMessageSeverities));
	}

	public VkEDebugMessengerCallback getMessengerCallback() {
		return messengerCallback;
	}

	public VkInstanceBuilder setMessengerCallback(VkEDebugMessengerCallback messengerCallback) {
		this.messengerCallback = messengerCallback;
		return this;
	}

	public Set<String> getEnabledExtensions() {
		return enabledExtensions;
	}

	public VkInstanceBuilder setEnabledExtensions(Set<String> enabledExtensions) {
		this.enabledExtensions = enabledExtensions;
		return this;
	}

	public VkInstanceBuilder setEnabledExtensions(String... enabledExtensions) {
		return setEnabledExtensions(Set.of(enabledExtensions));
	}

	public Set<String> getEnabledLayers() {
		return enabledLayers;
	}

	public VkInstanceBuilder setEnabledLayers(Set<String> enabledLayers) {
		this.enabledLayers = enabledLayers;
		return this;
	}

	public VkInstanceBuilder setEnabledLayers(String... enabledLayers) {
		return setEnabledLayers(Set.of(enabledLayers));
	}

	public VkEVersion getEngineVersion() {
		return engineVersion;
	}

	public VkInstanceBuilder setEngineVersion(VkEVersion engineVersion) {
		this.engineVersion = engineVersion;
		return this;
	}

	public VkEVersion getApplicationVersion() {
		return applicationVersion;
	}

	public VkInstanceBuilder setApplicationVersion(VkEVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
		return this;
	}

	public String getEngineName() {
		return engineName;
	}

	public VkInstanceBuilder setEngineName(String engineName) {
		this.engineName = engineName;
		return this;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public VkInstanceBuilder setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}
}
