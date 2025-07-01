package org.davidCMs.vkengine.shader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static org.lwjgl.util.shaderc.Shaderc.*;

public class ShaderCompiler implements AutoCloseable {

	private static final Logger log = LogManager.getLogger(ShaderCompiler.class);
	private final long opt;

	ShaderCompiler(long opt) {
		this.opt = opt;
	}

	public CompilationResult compile(String source, ShaderStage stage, String identifier) {
		return compile(source, stage, identifier, "main");
	}

	public CompilationResult compile(String source, ShaderStage stage, String identifier, String entryPoint) {
		long compiler = shaderc_compiler_initialize();

		long resultPtr = shaderc_compile_into_spv(
				compiler,
				source,
				stage.getShadercBit(),
				identifier,
				entryPoint,
				opt);

		if (resultPtr == 0)
			throw new RuntimeException("Error compiling shader, shaderc returned 0");

		CompilationResult result = CompilationResult.getFrom(resultPtr);

		shaderc_result_release(resultPtr);
		shaderc_compiler_release(compiler);

		return result;
	}


	@Override
	public void close() throws Exception {
		shaderc_compile_options_release(opt);
	}
}
