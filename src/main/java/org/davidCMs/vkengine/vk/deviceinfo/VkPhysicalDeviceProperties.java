package org.davidCMs.vkengine.vk.deviceinfo;

import org.davidCMs.vkengine.vk.VkVersion;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public record VkPhysicalDeviceProperties(

		VkVersion apiVersion,
		int driverVersion,
		int vendorID,
		int deviceID,
		VkPhysicalDeviceType deviceType,
		String deviceName,
		UUID pipelineCacheUUID,
		VkPhysicalDeviceLimits limits,
		VkPhysicalDeviceSparseProperties sparseProperties

) {

	public static VkPhysicalDeviceProperties getFrom(VkPhysicalDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			org.lwjgl.vulkan.VkPhysicalDeviceProperties properties =
					org.lwjgl.vulkan.VkPhysicalDeviceProperties.calloc(stack);
			VK14.vkGetPhysicalDeviceProperties(device, properties);

			return new VkPhysicalDeviceProperties(
					new VkVersion(properties.apiVersion()),
					properties.driverVersion(),
					properties.vendorID(),
					properties.deviceID(),
					VkPhysicalDeviceType.getType(properties.deviceType()),
					properties.deviceNameString(),
					getUUID(properties),
					VkPhysicalDeviceLimits.getFrom(properties.limits()),
					VkPhysicalDeviceSparseProperties.getFrom(properties.sparseProperties())
			);
		}
	}

	public static UUID getUUID(org.lwjgl.vulkan.VkPhysicalDeviceProperties properties) {
		long MSB, LSB;
		ByteBuffer buffer = properties.pipelineCacheUUID();
		buffer.order(ByteOrder.BIG_ENDIAN);
		buffer.rewind();

		MSB = buffer.getLong();
		LSB = buffer.getLong();

		return new UUID(MSB, LSB);
	}

}
