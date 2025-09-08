package dev.davidCMs.vkengine.shader;

import org.lwjgl.util.shaderc.Shaderc;

public enum OptimizationLevel {

	ZERO(Shaderc.shaderc_optimization_level_zero),
	SIZE(Shaderc.shaderc_optimization_level_size),
	PERFORMANCE(Shaderc.shaderc_optimization_level_performance)

	;

	private final int lvl;

	OptimizationLevel(int lvl) {
		this.lvl = lvl;
	}

	public int getLvl() {
		return lvl;
	}
}
