package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;

import java.nio.ByteBuffer;

public class VkEApplicationInfo implements AutoCloseable {

	private final MemoryStack stack;
	private final org.lwjgl.vulkan.VkApplicationInfo info;

	private VkEVersion applicationVersion;
	private String applicationName;
	private VkEVersion engineVersion;
	private String engineName;

	public VkEApplicationInfo() {
		stack = MemoryStack.stackPush();
		info = org.lwjgl.vulkan.VkApplicationInfo.calloc(stack);
		info.sType$Default();
		info.apiVersion(VK14.VK_API_VERSION_1_4);
	}

	public VkEApplicationInfo setApplicationVersion(VkEVersion version) {
		info.applicationVersion(VK14.VK_MAKE_VERSION(version.major(), version.minor(), version.patch()));
		this.applicationVersion = version;
		return this;
	}

	public VkEApplicationInfo setApplicationName(String name) {
		ByteBuffer byteBuffer = stack.UTF8(name);
		info.pApplicationName(byteBuffer);
		this.applicationName = name;
		return this;
	}

	public VkEApplicationInfo setEngineVersion(VkEVersion version) {
		info.engineVersion(VK14.VK_MAKE_VERSION(version.major(), version.minor(), version.patch()));
		this.engineVersion = version;
		return this;
	}

	public VkEApplicationInfo setEngineName(String name) {
		ByteBuffer byteBuffer = stack.UTF8(name);
		info.pEngineName(byteBuffer);
		this.engineName = name;
		return this;
	}

	public String getEngineName() {
		return engineName;
	}

	public VkEVersion getEngineVersion() {
		return engineVersion;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public VkEVersion getApplicationVersion() {
		return applicationVersion;
	}

	org.lwjgl.vulkan.VkApplicationInfo getInfo() { return info; }

	@Override
	public void close() throws Exception {
		stack.close();
	}
}
