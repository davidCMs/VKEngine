package org.davidCMs.vkengine.vk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkLayerProperties;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;

public enum VkLayer {

	KHRONOS_PROFILES("VK_LAYER_KHRONOS_profiles"),
	KHRONOS_VALIDATION("VK_LAYER_KHRONOS_validation"),
	KHRONOS_SYNCHRONIZATION2("VK_LAYER_KHRONOS_synchronization2"),
	KHRONOS_SHADER_OBJECT("VK_LAYER_KHRONOS_shader_object"),
	LUNARG_SCREENSHOT("VK_LAYER_LUNARG_screenshot"),
	LUNARG_CRASH_DIAGNOSTIC("VK_LAYER_LUNARG_crash_diagnostic"),
	LUNARG_API_DUMP("VK_LAYER_LUNARG_api_dump"),
	LUNARG_GFXRECONSTRUCT("VK_LAYER_LUNARG_gfxreconstruct"),
	LUNARG_MONITOR("VK_LAYER_LUNARG_monitor"),

	;

	final String name;

	VkLayer(String name) {
		this.name = name;
	}

	private static final Map<String, VkLayer> reverseMap = new HashMap<>();
	private static final Logger log = LogManager.getLogger(VkLayer.class, VulkanMessageFactory.INSTANCE);

	//Initializes the reverse lookup map
	static {
		for (VkLayer lay : values()) {
			reverseMap.put(lay.name, lay);
		}
	}

	/** Utility method to convert between {@link String} and {@link VkLayer}
	 *
	 * @param strings a set of strings
	 *
	 * @return a new set of {@link VkLayer} that represent the stings passed in
	 *  */
	public static Set<VkLayer> of(Set<String> strings) {
		Set<VkLayer> layers = new HashSet<>();
		for (String s : strings)
			if (reverseMap.containsKey(s))
				layers.add(reverseMap.get(s));
			else log.warn("{} not yet defined in VkLayer enum", s);
		return layers;
	}

	/** Array version of {@link VkLayer#of(Set)} */
	public static VkLayer[] of(String... strings) {
		VkLayer[] layers = new VkLayer[strings.length];
		for (int i = 0; i < strings.length; i++)
			if (reverseMap.containsKey(strings[i]))
				layers[i] = reverseMap.get(strings[i]);
			else log.warn("{} not yet defined in VkLayer enum", strings[i]);
		return layers;
	}

	/** single argument version of {@link VkLayer#of(String...)}*/
	public static VkLayer of(String s) {
		return of(new String[]{s})[0];
	}

	public static Set<VkLayer> getAvailableLayers() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			int[] count = new int[1];

			if (vkEnumerateInstanceLayerProperties(count, null) != VK14.VK_SUCCESS)
				throw new VkLayerQueryException("Cannot get layer count.");

			VkLayerProperties.Buffer buf = VkLayerProperties.calloc(count[0], stack);

			if (vkEnumerateInstanceLayerProperties(count, buf) != VK14.VK_SUCCESS)
				throw new VkLayerQueryException("Cannot get layers.");

			return buf.stream()
					.map(VkLayerProperties::layerNameString)
					.map(VkLayer::of)
					.collect(Collectors.toSet());
		}
	}


	/** Collection version of {@link VkLayer#of(String...)} */
	public static boolean checkAvailabilityOf(Collection<VkLayer> layer) {
		Set<VkLayer> available = getAvailableLayers();
		for (VkLayer ck : layer)
			if (ck == null || !available.contains(ck)) return false;
		return true;
	}

	/** Utility method to check if a specific or multiple layers are available
	 *
	 * @param layer a list of all layers that need to be present for this method to return true
	 * @return true if all provided layers are present
	 * */
	public static boolean checkAvailabilityOf(VkLayer... layer) {
		Set<VkLayer> available = getAvailableLayers();
		for (VkLayer ck : layer) {
			if (ck == null) continue;
			boolean b = false;
			for (VkLayer ac : available) {
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

	/** {@link Set<VkLayer>} version of {@link VkLayer#toPointerBuffer(VkLayer[], MemoryStack)} */
	public static PointerBuffer toPointerBuffer(Set<VkLayer> layers, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(layers.size());
		for (VkLayer layer : layers) {
			ByteBuffer buf = stack.UTF8(layer.name);
			pb.put(buf);
		}
		pb.flip();
		return pb;
	}

	/** Utility method that encodes an array of {@link VkLayer} into a {@link PointerBuffer} that is allocated from a {@link MemoryStack}
	 *
	 * @param layers an array of {@link VkLayer} of which's name will be encoded
	 * @param stack the {@link MemoryStack} that will be used to allocate the returned pointer buffer
	 *
	 * @return a {@link PointerBuffer} allocated form a {@link MemoryStack} that has the layer names encoded
	 * */
	public static PointerBuffer toPointerBuffer(VkLayer[] layers, MemoryStack stack) {
		PointerBuffer pb = stack.mallocPointer(layers.length);
		for (VkLayer layer : layers) {
			ByteBuffer buf = stack.UTF8(layer.name);
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
