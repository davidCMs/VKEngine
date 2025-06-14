package org.davidCMs.vkengine.util;

public class LogUtil {


	public static String beautify(Object o) {
		if (o == null) {
			return "null";
		}

		String s = o.toString();
		char[] chars = s.toCharArray();
		StringBuilder b = new StringBuilder();

		int indent = 0;
		boolean inString = false;
		for (int i = 0; i < chars.length; i++) {
			char cc = chars[i];
			char bc = (i == 0) ? '\u200B' : chars[i-1];
			char ac = (i == chars.length-1) ? '\u200B' : chars[i+1];

			if (cc == '"') inString = !inString;

			if (!inString) {
				switch (cc) {
					case '[', '{' -> {
						indent++;
						b.append(" [\n" + "\t".repeat(indent));
					}
					case ',' -> {
						b.append(",\n" + "\t".repeat(indent));
					}
					case ']', '}' -> {
						indent--;
						b.append("\n" + "\t".repeat(indent) + "]");
					}
					case '=' -> {
						if (bc != ' ')
							b.append(" ");
						b.append("=");
						if (ac != ' ')
							b.append(" ");
					}

					default -> {
						if (cc == ' ' && bc == ',')
							break;
						b.append(cc);
					}
				}
			} else b.append(cc);
		}
		return b.toString();
	}

}
