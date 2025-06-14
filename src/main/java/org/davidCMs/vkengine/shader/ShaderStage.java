package org.davidCMs.vkengine.shader;

import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.EXTMeshShader;
import org.lwjgl.vulkan.VK14;

public enum ShaderStage {

	VERTEX(VK14.VK_SHADER_STAGE_VERTEX_BIT, Shaderc.shaderc_vertex_shader),
	FRAGMENT(VK14.VK_SHADER_STAGE_FRAGMENT_BIT, Shaderc.shaderc_fragment_shader),
	GEOMETRY(VK14.VK_SHADER_STAGE_GEOMETRY_BIT, Shaderc.shaderc_geometry_shader),
	COMPUTE(VK14.VK_SHADER_STAGE_COMPUTE_BIT, Shaderc.shaderc_compute_shader),
	MESH(EXTMeshShader.VK_SHADER_STAGE_MESH_BIT_EXT, Shaderc.shaderc_mesh_shader),

	;

	private final int vkBit;
	private final int shadercBit;

	ShaderStage(int vkBit, int shadercBit) {
		this.vkBit = vkBit;
		this.shadercBit = shadercBit;
	}

	public int getVkBit() {
		return vkBit;
	}

	public int getShadercBit() {
		return shadercBit;
	}
}
