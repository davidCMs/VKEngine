package org.davidCMs.vkengine.util;

import java.io.PrintStream;

public class LogUtil {

	public static void printObj(Object o) {
		printObj(o, System.out);
	}

	public static void printObj(Object o, PrintStream stream) {
		if (o == null) {
			stream.println("null");
			return;
		}

		String s = o.toString();
		char[] chars = s.toCharArray();
		StringBuilder b = new StringBuilder();

		int indent = 0;
		for (int i = 0; i < chars.length; i++) {
			char cc = chars[i];
			char bc = (i == 0) ? '\u200B' : chars[i-1];
			char ac = (i == chars.length-1) ? '\u200B' : chars[i+1];

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
		}
		stream.println(b);
	}

}
