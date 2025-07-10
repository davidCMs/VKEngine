package org.davidCMs.vkengine.vk;

import org.davidCMs.vkengine.util.Copyable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSpecializationInfo;
import org.lwjgl.vulkan.VkSpecializationMapEntry;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class VkSpecializationInfoMapper implements Copyable {

	private final HashMap<Integer, Object> mappings;

	public VkSpecializationInfoMapper(HashMap<Integer, Object> mappings) {
		this.mappings = mappings;
	}

	public VkSpecializationInfoMapper() {
		mappings = new HashMap<>();
	}

	public VkSpecializationInfoMapper mapInt(int constId, int val) {
		mappings.put(constId, val);
		return this;
	}

	public VkSpecializationInfoMapper mapFloat(int constId, float val) {
		mappings.put(constId, val);
		return this;
	}

	public VkSpecializationInfoMapper mapBoolean(int constId, boolean val) {
		mappings.put(constId, val);
		return this;
	}

	private int getMappingsBytes() {
		int sum = 0;
		for (Map.Entry<Integer, Object> mapping : mappings.entrySet()) {
			switch (mapping.getValue()) {
				case Integer ignored -> sum += Integer.BYTES;
				case Float ignored -> sum += Float.BYTES;
				case Boolean ignored -> sum += Byte.BYTES;

				default ->
					throw new IllegalStateException("Illegal value found while processing a mapping entry, Illegal value type: " + mapping.getValue().getClass());
			}
		}
		return sum;
	}

	public VkSpecializationInfo build(MemoryStack stack) {
		ByteBuffer buf = stack.malloc(getMappingsBytes());

		VkSpecializationMapEntry.Buffer entries = VkSpecializationMapEntry.calloc(mappings.size(), stack);

		int i = 0;
		int offset = 0;
		for (Map.Entry<Integer, Object> mapping : mappings.entrySet()) {
			VkSpecializationMapEntry entry = entries.get(i);
			entry.constantID(mapping.getKey());
			entry.offset(offset);

			switch (mapping.getValue()) {
				case Integer val -> {
					buf.putInt(val);
					entry.size(Integer.BYTES);
					offset += Integer.BYTES;
				}
				case Float val -> {
					buf.putFloat(val);
					entry.size(Float.BYTES);
					offset += Float.BYTES;
				}
				case Boolean val -> {
					buf.put(val ? (byte) 1 : (byte) 0);
					entry.size(Byte.BYTES);
					offset += Byte.BYTES;
				}

				default ->
						throw new IllegalStateException("Illegal value found while processing a mapping entry, Illegal value type: " + mapping.getValue().getClass());
			}
			i++;
		}

		buf.flip();

		VkSpecializationInfo info = VkSpecializationInfo.calloc(stack);
		info.pMapEntries(entries);
		info.pData(buf);

		return info;
	}

	@Override
	public VkSpecializationInfoMapper copy() {
		return new VkSpecializationInfoMapper(mappings);
	}
}
