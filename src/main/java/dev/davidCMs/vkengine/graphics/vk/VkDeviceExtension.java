package dev.davidCMs.vkengine.graphics.vk;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;
import org.tinylog.TaggedLogger;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enum of vulkan device extensions
 * */
public enum VkDeviceExtension {

	VK_KHR_DEDICATED_ALLOCATION(KHRDedicatedAllocation.VK_KHR_DEDICATED_ALLOCATION_EXTENSION_NAME),
	VK_KHR_BIND_MEMORY_2(KHRBindMemory2.VK_KHR_BIND_MEMORY_2_EXTENSION_NAME),
	VK_KHR_MAINTENANCE_2(KHRMaintenance2.VK_KHR_MAINTENANCE_2_EXTENSION_NAME),
	VK_KHR_MAINTENANCE_4(KHRMaintenance4.VK_KHR_MAINTENANCE_4_EXTENSION_NAME),
	VK_KHR_MAINTENANCE_5(KHRMaintenance5.VK_KHR_MAINTENANCE_5_EXTENSION_NAME),
	VK_EXT_MEMORY_BUDGET(EXTMemoryBudget.VK_EXT_MEMORY_BUDGET_EXTENSION_NAME),
	VK_KHR_BUFFER_DEVICE_ADDRESS(KHRBufferDeviceAddress.VK_KHR_BUFFER_DEVICE_ADDRESS_EXTENSION_NAME),
	VK_EXT_MEMORY_PRIORITY(EXTMemoryPriority.VK_EXT_MEMORY_PRIORITY_EXTENSION_NAME),
	VK_AMD_DEVICE_COHERENT_MEMORY(AMDDeviceCoherentMemory.VK_AMD_DEVICE_COHERENT_MEMORY_EXTENSION_NAME),
	VK_KHR_EXTERNAL_MEMORY_WIN32(KHRExternalMemoryWin32.VK_KHR_EXTERNAL_MEMORY_WIN32_EXTENSION_NAME),
	VK_KHR_SWAPCHAIN(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME),
	VK_KHR_DYNAMIC_RENDERING(KHRDynamicRendering.VK_KHR_DYNAMIC_RENDERING_EXTENSION_NAME),
	VK_EXT_SWAPCHAIN_MAINTENANCE_1(EXTSwapchainMaintenance1.VK_EXT_SWAPCHAIN_MAINTENANCE_1_EXTENSION_NAME),
	VK_EXT_SURFACE_MAINTENANCE_1(EXTSurfaceMaintenance1.VK_EXT_SURFACE_MAINTENANCE_1_EXTENSION_NAME)

	;
	/** The vulkan name string that the enum represents */
	final String name;

	VkDeviceExtension(String name) {
		this.name = name;
	}

	/** {@link HashMap} that links vulkan extension names to enums in {@link VkDeviceExtension} */
	private static final Map<String, VkDeviceExtension> reverseMap = new HashMap<>();
    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");

	//Initializes the reverse lookup map
	static {
		for (VkDeviceExtension ext : values()) {
			reverseMap.put(ext.name, ext);
		}
	}

	/** Utility method to convert between {@link String} and {@link VkDeviceExtension}
	 *
	 * @param strings a set of strings
	 *
	 * @return a new set of {@link VkDeviceExtension} that represent the stings passed in
	 *  */
	public static Set<VkDeviceExtension> of(Set<String> strings) {
		Set<VkDeviceExtension> extensions = new HashSet<>();
		for (String s : strings)
			if (reverseMap.containsKey(s))
				extensions.add(reverseMap.get(s));
			//else log.warn("{} not yet defined in VkExtension enum", s);
		return extensions;
	}

	/** Array version of {@link VkDeviceExtension#of(Set)} */
	public static VkDeviceExtension[] of(String... strings) {
		VkDeviceExtension[] extensions = new VkDeviceExtension[strings.length];
		for (int i = 0; i < strings.length; i++)
			if (reverseMap.containsKey(strings[i]))
				extensions[i] = reverseMap.get(strings[i]);
			//else log.warn("{} not yet defined in VkExtension enum", strings[i]);
		return extensions;
	}

	/** single argument version of {@link VkDeviceExtension#of(String...)}*/
	public static VkDeviceExtension of(String s) {
		return of(new String[]{s})[0];
	}

	/** Utility method for getting a set of all available extensions
	 *
	 * @return a new set of {@link VkDeviceExtension} representing all the available extensions
	 * */
	public static Set<VkDeviceExtension> getAvailableExtension(VkPhysicalDevice device) {
		try (MemoryStack stack = MemoryStack.stackPush()) {

			IntBuffer count = stack.callocInt(1);

			if (VK14.vkEnumerateDeviceExtensionProperties(device.getPhysicalDevice(), (ByteBuffer) null, count, null) != VK14.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot query device extensions.");

			VkExtensionProperties.Buffer buffer = VkExtensionProperties.calloc(count.get(0), stack);

			if (VK14.vkEnumerateDeviceExtensionProperties(device.getPhysicalDevice(), (ByteBuffer) null, count, buffer) != VK14.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot query device extensions.");

			return buffer.stream()
					.map(VkExtensionProperties::extensionNameString)
					.map(VkDeviceExtension::of)
					.collect(Collectors.toSet());
		}
	}

	/** Collection version of {@link VkDeviceExtension#of(String...)} */
	public static boolean checkAvailabilityOf(VkPhysicalDevice device, Collection<VkDeviceExtension> extension) {
		Set<VkDeviceExtension> available = getAvailableExtension(device);
		for (VkDeviceExtension ck : extension)
			if (ck == null || !available.contains(ck)) return false;
		return true;
	}

	/** Utility method to check if a specific or multiple extensions are available
	 *
	 * @param extension a list of all extensions that need to be present for this method to return true
	 * @return true if all provided extensions are present
	 * */
	public static boolean checkAvailabilityOf(VkPhysicalDevice device, VkDeviceExtension... extension) {
		Set<VkDeviceExtension> available = getAvailableExtension(device);
		for (VkDeviceExtension ck : extension) {
			if (ck == null) continue;
			boolean b = false;
			for (VkDeviceExtension ac : available) {
				if (ac == null) continue;
				if (ac.equals(ck)) {
					b = true;
					break;
				}
			}
			if (!b) return false;
		}
		return true;
	}

	/** {@link Set< VkDeviceExtension >} version of {@link VkDeviceExtension#toPointerBuffer(VkDeviceExtension[], MemoryStack)} */
	public static PointerBuffer toPointerBuffer(Set<VkDeviceExtension> extensions, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(extensions.size());
		for (VkDeviceExtension extension : extensions) {
			ByteBuffer buf = stack.UTF8(extension.name);
			pb.put(buf);
		}
		pb.flip();
		return pb;
	}

	/** Utility method that encodes an array of {@link VkDeviceExtension} into a {@link PointerBuffer} that is allocated from a {@link MemoryStack}
	 *
	 * @param extensions an array of {@link VkDeviceExtension} of which's name will be encoded
	 * @param stack the {@link MemoryStack} that will be used to allocate the returned pointer buffer
	 *
	 * @return a {@link PointerBuffer} allocated form a {@link MemoryStack} that has the extension names encoded
	 * */
	public static PointerBuffer toPointerBuffer(VkDeviceExtension[] extensions, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(extensions.length);
		for (VkDeviceExtension extension : extensions) {
			ByteBuffer buf = stack.UTF8(extension.name);
			pb.put(buf);
		}
		pb.flip();
		return pb;
	}

	@Override
	public String toString() {
		return name;
	}

}
