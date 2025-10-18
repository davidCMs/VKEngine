package dev.davidCMs.vkengine.graphics.vk;

import org.tinylog.TaggedLogger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enum of vulkan extensions
 * */
public enum VkExtension {

	KHR_SURFACE(KHRSurface.VK_KHR_SURFACE_EXTENSION_NAME),
	KHR_XCB_SURFACE(KHRXcbSurface.VK_KHR_XCB_SURFACE_EXTENSION_NAME),
	KHR_WAYLAND_SURFACE(KHRWaylandSurface.VK_KHR_WAYLAND_SURFACE_EXTENSION_NAME),
	KHR_ANDROID_SURFACE(KHRAndroidSurface.VK_KHR_ANDROID_SURFACE_EXTENSION_NAME),
	KHR_XLIB_SURFACE(KHRXlibSurface.VK_KHR_XLIB_SURFACE_EXTENSION_NAME),
	KHR_WIN32_SURFACE(KHRWin32Surface.VK_KHR_WIN32_SURFACE_EXTENSION_NAME),

	EXT_DEBUG_UTILS(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME),

	;

	/** The vulkan name string that the enum represents */
	final String name;

	VkExtension(String name) {
		this.name = name;
	}

	/** {@link HashMap} that links vulkan extension names to enums in {@link VkExtension} */
	private static final Map<String, VkExtension> reverseMap = new HashMap<>();
    private static final TaggedLogger log = org.tinylog.Logger.tag("Vulkan");

	//Initializes the reverse lookup map
	static {
		for (VkExtension ext : values()) {
			reverseMap.put(ext.name, ext);
		}
	}

	/** Utility method to convert between {@link String} and {@link VkExtension}
	 *
	 * @param strings a set of strings
	 *
	 * @return a new set of {@link VkExtension} that represent the stings passed in
	 *  */
	public static Set<VkExtension> of(Set<String> strings) {
		Set<VkExtension> extensions = new HashSet<>();
		for (String s : strings)
			if (reverseMap.containsKey(s))
				extensions.add(reverseMap.get(s));
			//else log.warn("{} not yet defined in VkExtension enum", s);
		return extensions;
	}

	/** Array version of {@link VkExtension#of(Set)} */
	public static VkExtension[] of(String... strings) {
		VkExtension[] extensions = new VkExtension[strings.length];
		for (int i = 0; i < strings.length; i++)
			if (reverseMap.containsKey(strings[i]))
				extensions[i] = reverseMap.get(strings[i]);
			//else log.warn("{} not yet defined in VkExtension enum", strings[i]);
		return extensions;
	}

	/** single argument version of {@link VkExtension#of(String...)}*/
	public static VkExtension of(String s) {
		return of(new String[]{s})[0];
	}

	/** Utility method for getting a set of all available extensions
	 *
	 * @return a new set of {@link VkExtension} representing all the available extensions
	 * */
	public static Set<VkExtension> getAvailableExtension() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] count = new int[1];

			if (VK14.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, null) != VK10.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot get extension count.");

			VkExtensionProperties.Buffer buf = VkExtensionProperties.malloc(count[0], stack);

			if (VK14.vkEnumerateInstanceExtensionProperties((ByteBuffer) null, count, buf) != VK10.VK_SUCCESS)
				throw new VkExtensionQueryException("Cannot get extensions.");


			return buf.stream()
					.map(VkExtensionProperties::extensionNameString)
					.map(VkExtension::of)
					.collect(Collectors.toSet());
		}
	}

	/** Collection version of {@link VkExtension#of(String...)} */
	public static boolean checkAvailabilityOf(Collection<VkExtension> extension) {
		Set<VkExtension> available = getAvailableExtension();
		for (VkExtension ck : extension)
			if (ck == null || !available.contains(ck)) return false;
		return true;
	}

	/** Utility method to check if a specific or multiple extensions are available
	 *
	 * @param extension a list of all extensions that need to be present for this method to return true
	 * @return true if all provided extensions are present
	 * */
	public static boolean checkAvailabilityOf(VkExtension... extension) {
		Set<VkExtension> available = getAvailableExtension();
		for (VkExtension ck : extension) {
			if (ck == null) continue;
			boolean b = false;
			for (VkExtension ac : available) {
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

	/** {@link Set<VkExtension>} version of {@link VkExtension#toPointerBuffer(VkExtension[], MemoryStack)} */
	public static PointerBuffer toPointerBuffer(Set<VkExtension> extensions, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(extensions.size());
		for (VkExtension extension : extensions) {
			ByteBuffer buf = stack.UTF8(extension.name);
			pb.put(buf);
		}
		pb.flip();
		return pb;
	}

	/** Utility method that encodes an array of {@link VkExtension} into a {@link PointerBuffer} that is allocated from a {@link MemoryStack}
	 *
	 * @param extensions an array of {@link VkExtension} of which's name will be encoded
	 * @param stack the {@link MemoryStack} that will be used to allocate the returned pointer buffer
	 *
	 * @return a {@link PointerBuffer} allocated form a {@link MemoryStack} that has the extension names encoded
	 * */
	public static PointerBuffer toPointerBuffer(VkExtension[] extensions, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(extensions.length);
		for (VkExtension extension : extensions) {
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
