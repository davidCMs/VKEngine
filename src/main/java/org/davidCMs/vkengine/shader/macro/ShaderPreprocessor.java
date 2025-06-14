package org.davidCMs.vkengine.shader.macro;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ShaderPreprocessor {

	private static final Logger log = LogManager.getLogger(ShaderPreprocessor.class);

	public static final ShaderMacroProcessor DEFAULT = (macroName, macroArgs) -> {
		String msg = "Unimplemented macro \"" + macroName + "\" with args " + Arrays.toString(macroArgs);

		log.warn(msg);
		return "//" + msg + "\n";
	};

	private final HashMap<String, ShaderMacroProcessor> processorHashMap;
	private final char macroChar;

	public ShaderPreprocessor(HashMap<String, ShaderMacroProcessor> processorHashMap, char macroChar) {
		this.processorHashMap = processorHashMap;
		this.macroChar = macroChar;
	}

	public String processShader(String shader) {
		List<ShaderMacroString> macroStrings = ShaderMacroString.getMacros(shader, macroChar);
		shader = ShaderMacroString.removeComments(shader);

		for (ShaderMacroString macroString : macroStrings) {
			String replacement = processorHashMap.getOrDefault(macroString.name(), DEFAULT)
					.process(macroString.name(), macroString.args());
			shader = shader.replace(macroString.original(), replacement);
		}

		return shader;
	}

}
