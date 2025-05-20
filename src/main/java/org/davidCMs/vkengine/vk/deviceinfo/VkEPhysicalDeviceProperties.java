package org.davidCMs.vkengine.vk.deviceinfo;

import org.davidCMs.vkengine.vk.VkVersion;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public record VkEPhysicalDeviceProperties (

		VkVersion apiVersion,
		int driverVersion,
		int vendorID,
		int deviceID,
		VkPhysicalEDeviceType deviceType,
		String deviceName,
		UUID pipelineCacheUUID,
		VkEPhysicalDeviceLimits limits,
		VkEPhysicalDeviceSparseProperties sparseProperties

) {

	public static VkEPhysicalDeviceProperties getFrom(VkPhysicalDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			VkPhysicalDeviceProperties properties =
					VkPhysicalDeviceProperties.calloc(stack);
			VK14.vkGetPhysicalDeviceProperties(device, properties);

			return new VkEPhysicalDeviceProperties (
					new VkVersion(properties.apiVersion()),
					properties.driverVersion(),
					properties.vendorID(),
					properties.deviceID(),
					VkPhysicalEDeviceType.getType(properties.deviceType()),
					properties.deviceNameString(),
					getUUID(properties),
					VkEPhysicalDeviceLimits.getFrom(properties.limits()),
					VkEPhysicalDeviceSparseProperties.getFrom(properties.sparseProperties())
			);
		}
	}

	public static UUID getUUID(VkPhysicalDeviceProperties properties) {
		long MSB, LSB;
		ByteBuffer buffer = properties.pipelineCacheUUID();
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.rewind();

		MSB = buffer.getLong();
		LSB = buffer.getLong();

		return new UUID(MSB, LSB);
	}

}
