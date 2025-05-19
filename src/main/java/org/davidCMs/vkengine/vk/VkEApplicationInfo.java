package org.davidCMs.vkengine.vk;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkApplicationInfo;

public class VkEApplicationInfo {

	private VkEVersion engineVersion;
	private VkEVersion applicationVersion;
	private String applicationName;
	private String engineName;

	public VkEApplicationInfo() {

	}

	public VkEApplicationInfo(VkEVersion engineVersion, VkEVersion applicationVersion, String applicationName, String engineName) {
		this.engineVersion = engineVersion;
		this.applicationVersion = applicationVersion;
		this.applicationName = applicationName;
		this.engineName = engineName;
	}

	public VkEApplicationInfo setApplicationVersion(VkEVersion version) {
		this.applicationVersion = version;
		return this;
	}

	public VkEApplicationInfo setApplicationName(String name) {
		this.applicationName = name;
		return this;
	}

	public VkEApplicationInfo setEngineVersion(VkEVersion version) {
		this.engineVersion = version;
		return this;
	}

	public VkEApplicationInfo setEngineName(String name) {
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

	org.lwjgl.vulkan.VkApplicationInfo getInfo() {
		return VkApplicationInfo.calloc()
				.applicationVersion(applicationVersion.makeVersion())
				.pApplicationName(MemoryUtil.memUTF8(applicationName))
				.engineVersion(engineVersion.makeVersion())
				.pEngineName(MemoryUtil.memUTF8(engineName));
	}
}
