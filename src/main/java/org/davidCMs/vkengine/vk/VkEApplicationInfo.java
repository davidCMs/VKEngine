package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VK14;

import java.nio.ByteBuffer;

public class VkEApplicationInfo extends AutoCloseableResource {

	private final org.lwjgl.vulkan.VkApplicationInfo info;
	private VkEVersion engineVersion;
	private VkEVersion applicationVersion;

	public VkEApplicationInfo() {
		info = org.lwjgl.vulkan.VkApplicationInfo.calloc();
		info.sType$Default();
		info.apiVersion(VK14.VK_API_VERSION_1_4);
	}

	public VkEApplicationInfo setApplicationVersion(VkEVersion version) {
		check();
		info.applicationVersion(version.makeVersion());
		this.applicationVersion = version;
		return this;
	}

	public VkEApplicationInfo setApplicationName(String name) {
		check();
		ByteBuffer byteBuffer = MemoryUtil.memUTF8(name);
		info.pApplicationName(byteBuffer);
		return this;
	}

	public VkEApplicationInfo setEngineVersion(VkEVersion version) {
		check();
		info.engineVersion(version.makeVersion());
		this.engineVersion = version;
		return this;
	}

	public VkEApplicationInfo setEngineName(String name) {
		check();
		ByteBuffer byteBuffer = MemoryUtil.memUTF8(name);
		info.pEngineName(byteBuffer);
		return this;
	}

	public String getEngineName() {
		check();
		return info.pEngineNameString();
	}

	public VkEVersion getEngineVersion() {
		check();
		return engineVersion;
	}

	public String getApplicationName() {
		check();
		return info.pApplicationNameString();
	}

	public VkEVersion getApplicationVersion() {
		check();
		return applicationVersion;
	}

	org.lwjgl.vulkan.VkApplicationInfo getInfo() {
		check();
		return info;
	}

	@Override
	public void close() {
		super.close();
		MemoryUtil.memFree(info.pApplicationName());
		MemoryUtil.memFree(info.pEngineName());
		info.close();
	}
}
