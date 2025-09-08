package dev.davidCMs.vkengine.shader;

import static org.lwjgl.util.shaderc.Shaderc.*;

public enum CompilationStatus {

	COMPILATION_ERROR(shaderc_compilation_status_compilation_error),
	SUCCESS(shaderc_compilation_status_success),
	CONFIGURATION_ERROR(shaderc_compilation_status_configuration_error),
	INVALID_ASSEMBLY(shaderc_compilation_status_invalid_assembly),
	INTERNAL_ERROR(shaderc_compilation_status_internal_error),
	INVALID_STAGE(shaderc_compilation_status_invalid_stage),
	NULL_RESULT_OBJECT(shaderc_compilation_status_null_result_object),
	TRANSFORMATION_ERROR(shaderc_compilation_status_transformation_error),
	VALIDATION_ERROR(shaderc_compilation_status_validation_error)

	;

	private final int bit;

	CompilationStatus(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public static CompilationStatus valueOf(int bit) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].bit == bit) return values()[i];
		}
		throw new IllegalArgumentException("No Value for bit: " + bit);
	}

}
