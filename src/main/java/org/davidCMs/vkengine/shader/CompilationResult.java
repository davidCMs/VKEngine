package org.davidCMs.vkengine.shader;

import org.davidCMs.vkengine.util.BufUtils;

import static org.lwjgl.util.shaderc.Shaderc.*;

import java.nio.ByteBuffer;

public record CompilationResult(ByteBuffer bin, CompilationStatus status, String errors) {

	static CompilationResult getFrom(long resultPtr) {
		return new CompilationResult(
				BufUtils.cloneByteBuffer(shaderc_result_get_bytes(resultPtr)),
				CompilationStatus.valueOf(shaderc_result_get_compilation_status(resultPtr)),
				shaderc_result_get_error_message(resultPtr)
		);
	}

	@Override
	public ByteBuffer bin() {
		if (status() != CompilationStatus.SUCCESS)
			throw new IllegalStateException("Cannot get the binary as the status is " + status().toString() + " so no binary was generated!");
		return bin;
	}
}
