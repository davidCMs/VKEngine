package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.DefaultDebugMessengerCallback;
import org.davidCMs.vkengine.util.BufUtils;
import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;

public class VkInstanceBuilder {

	String applicationName = "App";
	String engineName = "Engine";

	VkVersion applicationVersion = new VkVersion(1, 1, 0, 0);
	VkVersion engineVersion = new VkVersion(1, 1, 0, 0);

	Set<String> enabledLayers = new HashSet<>();
	Set<String> enabledExtensions = new HashSet<>();

	VkDebugMessengerCallback messengerCallback = new DefaultDebugMessengerCallback();
	Set<VkDebugMessageSeverity> debugMessageSeverities = new HashSet<>();
	Set<VkDebugMessageType> debugMessageTypes = new HashSet<>();

	public VkInstanceContext build() {

		for (String layer : enabledLayers) {
			if (!VkLayerUtils.checkAvailabilityOf(layer))
				throw new VkLayerNotFoundException(layer);
		}

		for (String extension : enabledExtensions) {
			if (!VkExtensionUtils.checkAvailabilityOf(extension))
				throw new VkExtensionNotFoundException(extension);
		}

		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkInternalDebugMessengerCallback cb = new VkInternalDebugMessengerCallback(messengerCallback);
			VkInstanceCreateInfo info = VkInstanceCreateInfo.calloc(stack)
					.pApplicationInfo(
							VkApplicationInfo.calloc(stack)
									.apiVersion(VK14.VK_API_VERSION_1_4)
									.pApplicationName(stack.UTF8(applicationName))
									.applicationVersion(applicationVersion.makeVersion())
									.pEngineName(stack.UTF8(engineName))
									.engineVersion(engineVersion.makeVersion())
									.sType$Default())
					.pNext(VkDebugUtilsMessengerCreateInfoEXT.calloc(stack)
							.messageSeverity(VkDebugMessageSeverity.getMaskOf(debugMessageSeverities))
							.messageType(VkDebugMessageType.getMaskOf(debugMessageTypes))
							.pfnUserCallback(cb)
							.sType$Default())
					.ppEnabledLayerNames(BufUtils.stringsToPointerBuffer(stack, enabledLayers))
					.ppEnabledExtensionNames(BufUtils.stringsToPointerBuffer(stack, enabledExtensions))
					.sType$Default();

			PointerBuffer pb = stack.callocPointer(1);

			int err = 0;
			err = VK14.vkCreateInstance(info, null, pb);
			if (err != VK14.VK_SUCCESS)
				throw new VkFailedToCreateInstanceException("Failed to create instance err code: " + VkUtils.translateErrorCode(err));

			return new VkInstanceContext(
					new VkInstance(pb.get(0), info),
					cb,
					this
			);
		}
	}

	public Set<VkDebugMessageType> getDebugMessageTypes() {
		return debugMessageTypes;
	}

	public VkInstanceBuilder setDebugMessageTypes(Set<VkDebugMessageType> debugMessageTypes) {
		this.debugMessageTypes = debugMessageTypes;
		return this;
	}

	public VkInstanceBuilder setDebugMessageTypes(VkDebugMessageType... debugMessageTypes) {
		return setDebugMessageTypes(Set.of(debugMessageTypes));
	}

	public Set<VkDebugMessageSeverity> getDebugMessageSeverities() {
		return debugMessageSeverities;
	}

	public VkInstanceBuilder setDebugMessageSeverities(Set<VkDebugMessageSeverity> debugMessageSeverities) {
		this.debugMessageSeverities = debugMessageSeverities;
		return this;
	}

	public VkInstanceBuilder setDebugMessageSeverities(VkDebugMessageSeverity... debugMessageSeverities) {
		return setDebugMessageSeverities(Set.of(debugMessageSeverities));
	}

	public VkDebugMessengerCallback getMessengerCallback() {
		return messengerCallback;
	}

	public VkInstanceBuilder setMessengerCallback(VkDebugMessengerCallback messengerCallback) {
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

	public VkVersion getEngineVersion() {
		return engineVersion;
	}

	public VkInstanceBuilder setEngineVersion(VkVersion engineVersion) {
		this.engineVersion = engineVersion;
		return this;
	}

	public VkVersion getApplicationVersion() {
		return applicationVersion;
	}

	public VkInstanceBuilder setApplicationVersion(VkVersion applicationVersion) {
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
