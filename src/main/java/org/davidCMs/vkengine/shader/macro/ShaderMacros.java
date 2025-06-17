package org.davidCMs.vkengine.shader.macro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ShaderMacros {
    public static final ShaderMacroProcessor INCLUDE = ShaderMacros::include;

    public static String includeFile(Path path) {
        if (!path.toFile().exists()) return "//Macro processing error: file does not exist";
        StringBuilder sb = new StringBuilder();

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024*8);

        try (FileChannel file = FileChannel.open(path, StandardOpenOption.READ)) {
            while (true) {
                int read = file.read(byteBuffer);
                if (read < 1) break;
                byteBuffer.flip();
                sb.append(StandardCharsets.UTF_8.decode(byteBuffer));
            }
        } catch (IOException e) {
            return "//Macro processing error: error while reading file";
        }
        byteBuffer.clear();

        return sb.toString();
    }

    public static String includeResource(String path) {
        InputStream is = ShaderMacros.class.getResourceAsStream(path);
        if (is == null) return "//Macro processing error: could not open specified resource";

        StringBuilder sb = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            return "//Macro processing error: error while reading resource";
        }

        return sb.toString();
    }

    private static String include(String name, String[] args, ShaderPreprocessor preprocessor) {

        if (args.length != 1) return "//Macro processing error: this macro accepts exactly 1 argument";

        String path = args[0];

        int lastIndex = path.length() - 1;

        switch (path.charAt(0)) {
            case '<' -> {
                if (path.charAt(lastIndex) != '>')
                    return "//Macro processing error: macro argument starts with '<' but does not end with '>'";
                return preprocessor.processShader(includeResource(path.replaceAll("<|>", "")));
            }
            case '"' -> {
                if (path.charAt(lastIndex) != '"')
                    return "//Macro processing error: macro argument starts with '\"' but does not end with '\"'";
                return preprocessor.processShader(includeFile(Path.of(path.replaceAll("\"", ""))));
            }
            default -> {
                return "//Macro processing error: macro argument must start with eater '\"' or '<'";
            }
        }
    }
}
