package org.davidCMs.vkengine.shader.macro;

@FunctionalInterface
public interface ShaderMacroProcessor {

	String process(String macroName, String[] macroArgs, ShaderPreprocessor preprocessor);

}
