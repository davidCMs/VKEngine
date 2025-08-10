package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.DefaultDebugMessengerCallback;
import org.davidCMs.vkengine.common.BuilderSet;
import org.davidCMs.vkengine.util.BufUtils;
import org.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.util.HashSet;
import java.util.Set;

public class VkInstanceBuilder {

	private String applicationName = "App";
	private String engineName = "Engine";

	private VkVersion applicationVersion;
	private VkVersion engineVersion;

	private BuilderSet<VkInstanceBuilder, String> enabledLayers = new BuilderSet<>(this);
	private BuilderSet<VkInstanceBuilder, VkExtension> enabledExtensions = new BuilderSet<>(this);

	private VkDebugMessengerCallback messengerCallback = new DefaultDebugMessengerCallback();
	private final BuilderSet<VkInstanceBuilder, VkDebugMessageSeverity> debugMessageSeverities = new BuilderSet<>(this);
	private final BuilderSet<VkInstanceBuilder, VkDebugMessageType> debugMessageTypes = new BuilderSet<>(this);

	public VkInstanceContext build() {

		for (String layer : enabledLayers) {
			if (!VkLayerUtils.checkAvailabilityOf(layer))
				throw new VkLayerNotFoundException(layer);
		}

		for (VkExtension extension : enabledExtensions) {
			if (!VkExtension.checkAvailabilityOf(extension))
				throw new VkExtensionNotAvailableException(extension);
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
							.messageSeverity(VkDebugMessageSeverity.getMaskOf(debugMessageSeverities.getSet()))
							.messageType(VkDebugMessageType.getMaskOf(debugMessageTypes.getSet()))
							.pfnUserCallback(cb)
							.sType$Default())
					.ppEnabledLayerNames(BufUtils.stringsToPointerBuffer(stack, enabledLayers.getSet()))
					.ppEnabledExtensionNames(VkExtension.toPointerBuffer(enabledExtensions.getSet(), stack))
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

	public String getApplicationName() {
		return applicationName;
	}

	public VkInstanceBuilder setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public String getEngineName() {
		return engineName;
	}

	public VkInstanceBuilder setEngineName(String engineName) {
		this.engineName = engineName;
		return this;
	}

	public VkVersion getApplicationVersion() {
		return applicationVersion;
	}

	public VkInstanceBuilder setApplicationVersion(VkVersion applicationVersion) {
		this.applicationVersion = applicationVersion;
		return this;
	}

	public VkVersion getEngineVersion() {
		return engineVersion;
	}

	public VkInstanceBuilder setEngineVersion(VkVersion engineVersion) {
		this.engineVersion = engineVersion;
		return this;
	}

	public BuilderSet<VkInstanceBuilder, String> enabledLayers() {
		return enabledLayers;
	}

	public BuilderSet<VkInstanceBuilder, VkExtension> enabledExtensions() {
		return enabledExtensions;
	}

	public VkDebugMessengerCallback getMessengerCallback() {
		return messengerCallback;
	}

	public VkInstanceBuilder setMessengerCallback(VkDebugMessengerCallback messengerCallback) {
		this.messengerCallback = messengerCallback;
		return this;
	}

	public BuilderSet<VkInstanceBuilder, VkDebugMessageSeverity> debugMessageSeverities() {
		return debugMessageSeverities;
	}

	public BuilderSet<VkInstanceBuilder, VkDebugMessageType> debugMessageTypes() {
		return debugMessageTypes;
	}

}
