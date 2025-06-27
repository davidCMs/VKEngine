package org.davidCMs.vkengine.shader;

import static org.lwjgl.util.shaderc.Shaderc.*;

public class ShaderCompilerBuilder {

	private OptimizationLevel optimizationLevel = OptimizationLevel.ZERO;
	private boolean generateDebugInfo = true;
	private boolean suppressWarnings = false;
	private boolean warningsAsErrors = true;
	private boolean invertY = false;
	private boolean autoBindUniforms = false;
	private boolean setNaNClamp = false;

	public OptimizationLevel getOptimizationLevel() {
		return optimizationLevel;
	}

	public ShaderCompilerBuilder setOptimizationLevel(OptimizationLevel optimizationLevel) {
		this.optimizationLevel = optimizationLevel;
		return this;
	}

	public boolean isGenerateDebugInfo() {
		return generateDebugInfo;
	}

	public ShaderCompilerBuilder setGenerateDebugInfo(boolean generateDebugInfo) {
		this.generateDebugInfo = generateDebugInfo;
		return this;
	}

	public boolean isSuppressWarnings() {
		return suppressWarnings;
	}

	public ShaderCompilerBuilder setSuppressWarnings(boolean suppressWarnings) {
		this.suppressWarnings = suppressWarnings;
		return this;
	}

	public boolean isWarningsAsErrors() {
		return warningsAsErrors;
	}

	public ShaderCompilerBuilder setWarningsAsErrors(boolean warningsAsErrors) {
		this.warningsAsErrors = warningsAsErrors;
		return this;
	}

	public boolean isInvertY() {
		return invertY;
	}

	public ShaderCompilerBuilder setInvertY(boolean invertY) {
		this.invertY = invertY;
		return this;
	}

	public boolean isAutoBindUniforms() {
		return autoBindUniforms;
	}

	public ShaderCompilerBuilder setAutoBindUniforms(boolean autoBindUniforms) {
		this.autoBindUniforms = autoBindUniforms;
		return this;
	}

	public boolean isSetNaNClap() {
		return setNaNClamp;
	}

	public ShaderCompilerBuilder setSetNaNClap(boolean setNaNClap) {
		this.setNaNClamp = setNaNClap;
		return this;
	}

	public ShaderCompiler build() {

		long opt = shaderc_compile_options_initialize();

		shaderc_compile_options_set_target_env(opt, shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_4);
		shaderc_compile_options_set_target_spirv(opt ,shaderc_spirv_version_1_6);
		shaderc_compile_options_set_source_language(opt, shaderc_source_language_glsl);
		shaderc_compile_options_set_hlsl_functionality1(opt, false);

		shaderc_compile_options_set_optimization_level(opt, optimizationLevel.getLvl());

		if (generateDebugInfo)
			shaderc_compile_options_set_generate_debug_info(opt);
		if (suppressWarnings)
			shaderc_compile_options_set_suppress_warnings(opt);
		if (warningsAsErrors)
			shaderc_compile_options_set_warnings_as_errors(opt);

		shaderc_compile_options_set_auto_bind_uniforms(opt, autoBindUniforms);
		shaderc_compile_options_set_invert_y(opt, invertY);
		shaderc_compile_options_set_nan_clamp(opt, setNaNClamp);



		return new ShaderCompiler(opt);

	}

}
