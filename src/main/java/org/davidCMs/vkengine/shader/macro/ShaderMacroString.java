package org.davidCMs.vkengine.shader.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record ShaderMacroString(String original, String name, String[] args) {

	public static String removeComments(String string) {
		StringBuilder sb = new StringBuilder();

		boolean inMultiLineComment = false;
		boolean inLineComment = false;
		boolean inString = false;

		for (int i = 0; i < string.length(); i++) {
			char cc = string.charAt(i);
			char pc = i == 0 ? '\u200B' : string.charAt(i-1);
			char nc = i == string.length() - 1 ? '\u200B' : string.charAt(i+1);

			if (cc == '"' && !inLineComment && !inMultiLineComment) inString = !inString;

			if (cc == '\n' && !inString) {
				if (inMultiLineComment || inLineComment ) {
					inLineComment = false;
					continue;
				}
			}

			if (cc == '/' && nc == '/' && !inString)
				inLineComment = true;

			if (cc == '/' && nc == '*' && !inLineComment && !inString)
				inMultiLineComment = true;

			if (pc == '*' && cc == '/' && inMultiLineComment && !inLineComment && !inString) {
				inMultiLineComment = false;
				if (nc == '\n') i++;
				continue;
			}

			if (!inLineComment && !inMultiLineComment) sb.append(cc);
		}

		return sb.toString();
	}

	public static List<String> getMacroInvocations(String string, char macroChar) {

		List<StringBuilder> list = new ArrayList<>();

		int macroCount = 0;
		int brackets = 0;
		boolean readingMacroInvocation = false;

		for (int i = 0; i < string.length(); i++) {
			char cc = string.charAt(i);
			char pc = i == 0 ? '\u200B' : string.charAt(i-1);
			char nc = i == string.length() - 1 ? '\u200B' : string.charAt(i+1);

			if (cc == macroChar) {
				readingMacroInvocation = true;
				list.add(macroCount, new StringBuilder());
			}

			if (readingMacroInvocation && cc == '(') {
				brackets++;
			}

			if (readingMacroInvocation && cc == ')') {
				brackets--;
				if (brackets == 0) {
					readingMacroInvocation = false;
					list.get(macroCount).append(cc);
					macroCount++;
				}
			}

			if (readingMacroInvocation) {
				list.get(macroCount).append(cc);
			}

		}

		return list.stream()
				.map(StringBuilder::toString)
				.toList();
	}

	public static List<ShaderMacroString> getMacros(String string, char macroChar) {
		String commentless = removeComments(string);
		List<String> macros = getMacroInvocations(commentless, macroChar);

		List<ShaderMacroString> list = new ArrayList<>();

		for (String macro : macros) {
			String original = macro;
			macro = macro.replaceFirst(macroChar + "", "");
			macro = macro.replace(" ", "");

			StringBuilder nameBuilder = new StringBuilder();

			for (int i = 0; i < macro.length(); i++) {
				char c = macro.charAt(i);
				if (c == '(') break;
				nameBuilder.append(c);
			}

			String name = nameBuilder.toString();
			macro = macro.replace(name, "");
			macro = macro.replace("(", "");
			macro = macro.replace(")", "");

			String[] args = macro.split(",");

			list.add(new ShaderMacroString(original, name, args));
		}

		return list;
	}

	@Override
	public String toString() {
		return "ShaderMacroString{" +
				"original='" + original + '\'' +
				", name='" + name + '\'' +
				", args=" + Arrays.toString(args) +
				'}';
	}
}
