package org.davidCMs.vkengine.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkDeviceCreateInfo;
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class VkEDeviceCreateInfo extends AutoCloseableResource {

	private final VkDeviceCreateInfo info;

	private final Set<VkEDeviceQueueCreateInfo> queueCreateInfos = new HashSet<>();
	private final Set<String> extensionNames = new HashSet<>();

	public VkEDeviceCreateInfo() {
		info = VkDeviceCreateInfo.calloc();
		info.sType$Default();

		info.pQueueCreateInfos(VkDeviceQueueCreateInfo.calloc(0));
		info.ppEnabledExtensionNames(MemoryUtil.memAllocPointer(0));
		info.ppEnabledLayerNames(MemoryUtil.memAllocPointer(0));
	}

	public VkEDeviceCreateInfo setEnabledExtensions(Set<String> names) {
		check();
		if (info.ppEnabledExtensionNames() != null)
			clearExtensionBuffer();
		PointerBuffer buffer = MemoryUtil.memCallocPointer(names.size());

		int i = 0;

		for (String s : names) {
			ByteBuffer buf = MemoryUtil.memUTF8(s);
			buffer.put(i, buf);
			i++;
		}

		info.ppEnabledExtensionNames(buffer);
		extensionNames.clear();
		extensionNames.addAll(names);
		return this;
	}

	public Set<String> getEnabledExtensions() {
		check();
		return extensionNames;
	}

	public VkEDeviceCreateInfo setQueueCreateInfos(Set<VkEDeviceQueueCreateInfo> infos) {
		check();
		clearQueueInfosBuffer();
		VkDeviceQueueCreateInfo.Buffer buffer = VkDeviceQueueCreateInfo.malloc(infos.size());

		int i = 0;
		for (VkEDeviceQueueCreateInfo info : infos) {
			buffer.put(i, info.getInfo());
			i++;
		}

		info.pQueueCreateInfos(buffer);

		queueCreateInfos.clear();
		queueCreateInfos.addAll(infos);
		return this;
	}

	public Set<VkEDeviceQueueCreateInfo> getQueueCreateInfos() {
		check();
		if (info.pQueueCreateInfos() == null) return Set.of();
		return queueCreateInfos;
	}

	private void clearQueueInfosBuffer() {
		if (info.pQueueCreateInfos() == null) return;
		VkDeviceQueueCreateInfo.Buffer buffer = info.pQueueCreateInfos();
		for (int i = 0; i < buffer.remaining(); i++) {
			buffer.get(i).close();
		}
		MemoryUtil.memFree(buffer);
	}

	private void clearExtensionBuffer() {
		if (info.ppEnabledExtensionNames() == null) return;
		PointerBuffer buffer = info.ppEnabledExtensionNames();
		for (int i = 0; i < buffer.remaining(); i++) {
			MemoryUtil.nmemFree(buffer.get(i));
		}
		MemoryUtil.memFree(buffer);
	}

	private void clearLayerBuffer() {
		if (info.ppEnabledLayerNames() == null) return;
		PointerBuffer buffer = info.ppEnabledLayerNames();
		for (int i = 0; i < buffer.remaining(); i++) {
			MemoryUtil.nmemFree(buffer.get(i));
		}
		MemoryUtil.memFree(buffer);
	}

	VkDeviceCreateInfo getInfo() {
		check();
		return info;
	}

	@Override
	public void close() {
		super.close();

		clearQueueInfosBuffer();
		clearExtensionBuffer();
		clearLayerBuffer();

		info.close();
	}
}
