package org.davidCMs.vkengine.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;

public class IOUtils {

	public static String loadFile(File file) throws IOException {
		if (!file.exists()) throw new FileNotFoundException("Unable to find resource \"" + file.getAbsolutePath() + "\"");
		StringBuilder sb = new StringBuilder();

		ByteBuffer bb = ByteBuffer.allocateDirect(1024*8);

		FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.READ);

		while (true) {
			int read = fc.read(bb);
			if (read < 1) break;
			bb.flip();
			sb.append(StandardCharsets.UTF_8.decode(bb));
		}

		bb.clear();
		fc.close();

		return sb.toString();
	}

	public static String loadResource(String path) throws IOException {
		InputStream is = IOUtils.class.getResourceAsStream(path);
		if (is == null) throw new FileNotFoundException("Unable to find resource \"" + path + "\"");

		StringBuilder sb = new StringBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;

		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();

		return sb.toString();
	}


}
