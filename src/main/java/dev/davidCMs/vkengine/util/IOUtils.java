package dev.davidCMs.vkengine.util;

import dev.davidCMs.vkengine.Main;
import dev.davidCMs.vkengine.common.Image;
import dev.davidCMs.vkengine.common.NativeByteBuffer;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class IOUtils {

	private static final TaggedLogger log = Logger.tag("IOUtils");

	public static final JarFile jar;
	public static final Path resourcePath;

	static {
		log.info("Statically initialising");
		JarFile temp;
		Path path = null;
        try {
            temp = new JarFile(new File(Main.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getFile()), false, ZipFile.OPEN_READ);
			log.info("Running as jar file");
        } catch (IOException e) {
            temp = null;
			log.info("Not running as jar file");
            try {
				path = Path.of(IOUtils.class.getResource("/rsroot").toURI()).getParent();
                log.info("Resources will be loaded from \""
                + path
				+ "\" instead");
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
		}
		resourcePath = path;
		jar = temp;
    }

	public static NativeByteBuffer loadFile(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) throw new FileNotFoundException("Unable to find resource \"" + file.getAbsolutePath() + "\"");

		NativeByteBuffer buf = NativeByteBuffer.malloc((int) Files.size(path));

		try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
			while (buf.readFrom(fc) > 0) ;
		}
		buf.flip().rewind();

		return buf;
	}

	public static NativeByteBuffer loadResource(Path path) throws IOException {
		if (resourcePath != null) {
			String pathStr = path.toString().replace('\\', '/');
			pathStr = pathStr.charAt(0) == '/' ? pathStr.substring(1) : pathStr;
			return loadFile(resourcePath.resolve(Path.of(pathStr)));
		}
		if (jar == null) throw new RuntimeException("Oh god all is fucked");

		String pathStr = path.toString().replace('\\', '/');
		pathStr = pathStr.charAt(0) == '/' ? pathStr : '/' + pathStr;

		InputStream is = IOUtils.class.getResourceAsStream(pathStr);
		if (is == null) throw new FileNotFoundException("Unable to find resource \"" + pathStr + "\"");

		long size = jar.getEntry(pathStr.substring(1)).getSize();

		NativeByteBuffer buf = NativeByteBuffer.malloc((int) size);
		try (ReadableByteChannel channel = Channels.newChannel(is)) {
			while (buf.readFrom(channel) > 0) ;
		}
		buf.flip().rewind();

		return buf;
	}
	
	public static NativeByteBuffer load(Path path) throws IOException {
		if (path.toString().startsWith("/")) path = Path.of(path.toString().replaceFirst("/", ""));
		if (path.toFile().exists()) return loadFile(path);

		return loadResource(path);
	}



	public static Image loadImage(Path p, int channels) throws IOException {
		try (NativeByteBuffer encodedData = load(p)) {
			return ImageUtils.decodeImg(encodedData, channels);
		}
	}


}
