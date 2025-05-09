package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.windows.MSG;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkEInstanceCreateInfo extends AutoCloseableResource {

	private final org.lwjgl.vulkan.VkInstanceCreateInfo info;
	private final VkDebugUtilsMessengerCreateInfoEXT messengerInfo;

	private VkEApplicationInfo applicationInfo;
	private VkEDebugMessengerCallback messengerCallback = ((severity, type, data) -> {
		String s = "[Vulkan] [" + type + "] [" + severity + "] " + data.pMessageString();

		switch (severity) {
			case INFO:
			case WARNING:
			case VERBOSE:
				System.out.println(s);
				break;
			case ERROR:
				System.err.println(s);
		}
	});

	private VkEInternalDebugMessengerCallback internalMessengerCallback = new VkEInternalDebugMessengerCallback(messengerCallback);

	public VkEInstanceCreateInfo() {
		info = org.lwjgl.vulkan.VkInstanceCreateInfo.calloc();
		info.sType$Default();
		messengerInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc();
		messengerInfo.sType$Default();
		info.pNext(messengerInfo);
		messengerInfo.pfnUserCallback(internalMessengerCallback);
	}

	public VkEInstanceCreateInfo setApplicationCreateInfo(VkEApplicationInfo appInfo) {
		check();
		info.pApplicationInfo(appInfo.getInfo());
		return this;
	}

	public VkEInstanceCreateInfo setEnabledLayerNames(String... layers) {
		return setEnabledLayerNames(Set.of(layers));
	}

	public VkEInstanceCreateInfo setEnabledLayerNames(Set<String> layers) {
		check();
		if (info.ppEnabledLayerNames() != null)
			freeLayerNames();

		for (String name : layers) {
			if (!VkELayerUtils.checkAvailabilityOf(name))
				throw new VkELayerNotFoundException("Layer \"" + name + "\" not found.");
		}

		PointerBuffer namesPtr = MemoryUtil.memAllocPointer(layers.size());

		for (String name : layers) {
			ByteBuffer buf = MemoryUtil.memUTF8(name);
			namesPtr.put(buf);
		}

		namesPtr.flip();
		info.ppEnabledLayerNames(namesPtr);
		return this;
	}

	public Set<String> getEnabledLayerNames() {
		check();
		PointerBuffer namesPtr = info.ppEnabledLayerNames();
		if (namesPtr == null) {
			return Set.of();
		}

		Set<String> names = new HashSet<>(info.enabledLayerCount());

		for (int i = 0; i < info.enabledLayerCount(); i++) {
			long addr = namesPtr.get(i);
			names.add(MemoryUtil.memUTF8Safe(addr));
		}

		return names;
	}

	public VkEInstanceCreateInfo setEnabledExtensionNames(String extension) {
		return setEnabledExtensionNames(Set.of(extension));
	}

	public VkEInstanceCreateInfo setEnabledExtensionNames(Set<String> extensions) {
		check();
		if (info.ppEnabledExtensionNames() != null)
			freeExtensionNames();

		for (String name : extensions) {
			if (!VkEExtensionUtils.checkAvailabilityOf(name)) {
				throw new VkEExtensionNotFoundException("Extension \"" + name + "\" not found.");
			}
		}

		PointerBuffer namesPtr = MemoryUtil.memAllocPointer(extensions.size());

		for (String name : extensions) {
			ByteBuffer buf = MemoryUtil.memUTF8(name);
			namesPtr.put(buf);
		}

		namesPtr.flip();
		info.ppEnabledExtensionNames(namesPtr);
		return this;
	}

	public Set<String> getEnabledExtensionNames() {
		check();
		PointerBuffer namesPtr = info.ppEnabledExtensionNames();
		if (namesPtr == null) {
			return Set.of();
		}

		Set<String> names = new HashSet<>(info.enabledLayerCount());

		for (int i = 0; i < info.enabledExtensionCount(); i++) {
			long addr = namesPtr.get(i);
			names.add(MemoryUtil.memUTF8Safe(addr));
		}

		return names;
	}

	public VkEInstanceCreateInfo setDebugMessageTypes(VkEDebugMessageType... types) {
		check();
		messengerInfo.messageType(VkEDebugMessageType.getValueOf(types));
		return this;
	}

	public VkEInstanceCreateInfo setDebugMessageSeverity(VkEDebugMessageSeverity... severities) {
		check();
		messengerInfo.messageSeverity(VkEDebugMessageSeverity.getValueOf(severities));
		return this;
	}

	public VkEInstanceCreateInfo setMessengerCallback(VkEDebugMessengerCallback callback) {
		check();
		this.messengerCallback = callback;
		return this;
	}

	org.lwjgl.vulkan.VkInstanceCreateInfo getInfo() {
		check();
		return info;
	}

	VkDebugUtilsMessengerCreateInfoEXT getMessengerInfo() {
		check();
		return messengerInfo;
	}

	VkEInternalDebugMessengerCallback getInternalMessengerCallback() {
		check();
		return internalMessengerCallback;
	}

	void setInternalMessengerCallback (VkEInternalDebugMessengerCallback callback) {
		check();
		internalMessengerCallback = callback;
	}

	private void freeLayerNames() {
		PointerBuffer namesPtr = info.ppEnabledLayerNames();

		if (namesPtr == null)
			throw new IllegalStateException("Cannot free layer names as there is nothing to be freed");

		for (int i = 0; i < namesPtr.remaining(); i++) {
			MemoryUtil.nmemFree(namesPtr.get(i));
		}

		MemoryUtil.memFree(namesPtr);
	}

	private void freeExtensionNames() {
		PointerBuffer namesPtr = info.ppEnabledExtensionNames();

		if (namesPtr == null)
			throw new IllegalStateException("Cannot free extension names as there is nothing to be freed");

		for (int i = 0; i < namesPtr.remaining(); i++) {
			MemoryUtil.nmemFree(namesPtr.get(i));
		}

		MemoryUtil.memFree(namesPtr);
	}

	@Override
	public void close() {
		super.close();

		try {
			freeLayerNames();
			freeExtensionNames();
		} catch (IllegalStateException ignored) {}
		info.close();
		messengerInfo.close();

		if (internalMessengerCallback != null)
			internalMessengerCallback.close();
	}
}
