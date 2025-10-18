package dev.davidCMs.vkengine.util;

public class ValueNotNormalizedException extends RuntimeException {

	public ValueNotNormalizedException(String message) {
		super(message);
	}

	public ValueNotNormalizedException(float unnormalizedValue) {
		this("Value MUST be in the range [0, 1], but it was: " + unnormalizedValue);
	}

	public ValueNotNormalizedException(String valueName, float unnormalizedValue) {
		this("Value \"" + valueName + "\" MUST be in the range [0, 1], but it was: " + unnormalizedValue);
	}

	public static void check(float value) {
		if (value > 1 || value < 0)
			throw new ValueNotNormalizedException(value);
	}

	public static void check(String valueName, float value) {
		if (value > 1 || value < 0)
			throw new ValueNotNormalizedException(valueName, value);
	}

}
