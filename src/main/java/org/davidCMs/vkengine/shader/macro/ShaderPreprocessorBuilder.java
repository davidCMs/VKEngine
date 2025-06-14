package org.davidCMs.vkengine.shader.macro;

import java.util.HashMap;

public class ShaderPreprocessorBuilder {

	private final HashMap<String, ShaderMacroProcessor> processorHashMap = new HashMap<>();
	private char macroChar = '#';

	public ShaderPreprocessorBuilder addMacroProcessor(String macro, ShaderMacroProcessor processor) {
		processorHashMap.put(macro, processor);
		return this;
	}

	public ShaderPreprocessorBuilder setMacroChar(char macroChar) {
		this.macroChar = macroChar;
		return this;
	}

	public ShaderPreprocessor build() {
		return new ShaderPreprocessor(processorHashMap, macroChar);
	}

}
