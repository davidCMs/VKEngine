package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkEInstanceCreateInfo implements AutoCloseable {

	private final MemoryStack stack;
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
		stack = MemoryStack.stackPush();
		info = org.lwjgl.vulkan.VkInstanceCreateInfo.calloc(stack);
		info.sType$Default();
		messengerInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
		messengerInfo.sType$Default();
		info.pNext(messengerInfo);
		messengerInfo.pfnUserCallback(internalMessengerCallback);
	}

	public VkEInstanceCreateInfo setApplicationCreateInfo(VkEApplicationInfo appInfo) {
		info.pApplicationInfo(appInfo.getInfo());
		return this;
	}

	public VkEInstanceCreateInfo setEnabledLayerNames(String... layers) {
		for (String name : layers) {
			if (!VkELayerUtils.checkAvailabilityOf(name))
				throw new VkELayerNotFoundException("Layer \"" + name + "\" not found.");
		}

		PointerBuffer namesPtr = stack.mallocPointer(layers.length);

		for (String name : layers) {
			ByteBuffer buf = stack.UTF8(name);
			namesPtr.put(buf);
		}

		namesPtr.flip();

		info.ppEnabledLayerNames(namesPtr);

		return this;
	}

	public Set<String> getEnabledLayerNames() {
		PointerBuffer namesPtr = info.ppEnabledLayerNames();
		if (namesPtr == null) {
			System.out.println("No layers enabled");
			return Set.of();
		}

		Set<String> names = new HashSet<>(info.enabledLayerCount());

		for (int i = 0; i < info.enabledLayerCount(); i++) {
			long addr = namesPtr.get(i);
			names.add(MemoryUtil.memUTF8Safe(addr));
		}

		return names;
	}

	public VkEInstanceCreateInfo setEnabledExtensionsNames(String... extensions) {
		for (String name : extensions) {
			if (!VkEExtensionUtils.checkAvailabilityOf(name)) {
				throw new VkEExtensionNotFoundException("Extension \"" + name + "\" not found.");
			}
		}

		PointerBuffer namesPtr = stack.mallocPointer(extensions.length);

		for (String name : extensions) {
			ByteBuffer buf = stack.UTF8(name);
			namesPtr.put(buf);
		}

		namesPtr.flip();

		info.ppEnabledExtensionNames(namesPtr);

		return this;
	}

	public Set<String> getEnabledExtensionNames() {
		PointerBuffer namesPtr = info.ppEnabledExtensionNames();
		if (namesPtr == null) {
			System.out.println("No extensions enabled");
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
		messengerInfo.messageType(VkEDebugMessageType.getValueOf(types));
		return this;
	}

	public VkEInstanceCreateInfo setDebugMessageSeverity(VkEDebugMessageSeverity... severities) {
		messengerInfo.messageSeverity(VkEDebugMessageSeverity.getValueOf(severities));
		return this;
	}

	public VkEInstanceCreateInfo setMessengerCallback(VkEDebugMessengerCallback callback) {
		this.messengerCallback = callback;
		return this;
	}

	org.lwjgl.vulkan.VkInstanceCreateInfo getInfo() {return info;}
	VkDebugUtilsMessengerCreateInfoEXT getMessengerInfo() {return messengerInfo;}
	VkEInternalDebugMessengerCallback getInternalMessengerCallback() {return internalMessengerCallback;}
	void setInternalMessengerCallback (VkEInternalDebugMessengerCallback callback) {internalMessengerCallback = callback;}

	@Override
	public void close() throws Exception {
		if (internalMessengerCallback != null)
			internalMessengerCallback.close();
		stack.close();
	}
}
