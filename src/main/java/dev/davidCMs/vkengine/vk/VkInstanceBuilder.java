package dev.davidCMs.vkengine.vk;

import dev.davidCMs.vkengine.DefaultDebugMessengerCallback;
import dev.davidCMs.vkengine.common.BuilderSet;
import dev.davidCMs.vkengine.util.VkUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

/** Builder class for {@link VkInstanceContext}
 *
 * @implNote This class combines {@link VkApplicationInfo}, {@link VkDebugUtilsMessengerCreateInfoEXT} and {@link VkInstanceCreateInfo} into one builder
 * @see VkInstanceContext
 * @see VkApplicationInfo
 * @see VkDebugUtilsMessengerCreateInfoEXT
 * @see VkInstanceCreateInfo*/
public class VkInstanceBuilder {

	/** Name of the application */
	private String applicationName = "App";
	/** Name of the engine */
	private String engineName = "Engine";

	/** Version of the application */
	private VkVersion applicationVersion;
	/** Version of the engine */
	private VkVersion engineVersion;

	/** Set of explicitly enabled layers */
	private BuilderSet<VkInstanceBuilder, VkLayer> enabledLayers = new BuilderSet<>(this);
	/** Set of enabled extensions */
	private BuilderSet<VkInstanceBuilder, VkExtension> enabledExtensions = new BuilderSet<>(this);

	/** The {@link VkDebugMessengerCallback} that will be called when a debug message needs to be printed */
	private VkDebugMessengerCallback messengerCallback = new DefaultDebugMessengerCallback();

	/** Set of {@link VkDebugMessageSeverity} that defines for what severities of messages the {@link VkInstanceBuilder#messengerCallback} gets called */
	private final BuilderSet<VkInstanceBuilder, VkDebugMessageSeverity> debugMessageSeverities = new BuilderSet<>(this);
	/** Set of {@link VkDebugMessageType} that defines for what types of messages the {@link VkInstanceBuilder#messengerCallback} gets called */
	private final BuilderSet<VkInstanceBuilder, VkDebugMessageType> debugMessageTypes = new BuilderSet<>(this);

	/** Builds a new {@link VkInstanceContext} instance
	 * @return a new {@link VkInstanceContext} instance */
	public VkInstanceContext build() {

		for (VkLayer layer : enabledLayers) {
			if (!VkLayer.checkAvailabilityOf(layer))
				throw new VkLayerNotAvailableException(layer);
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
					.ppEnabledLayerNames(VkLayer.toPointerBuffer(enabledLayers.getSet(), stack))
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

	public BuilderSet<VkInstanceBuilder, VkLayer> enabledLayers() {
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
