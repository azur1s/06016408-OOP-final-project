package com.project.utils;

import org.lwjgl.BufferUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading resources from the classpath (src/classpath)
 */
public final class Resources {

    private Resources() {
    }

    public static ByteBuffer loadResourceToByteBuffer(String resourcePath) {
        byte[] bytes = loadResourceAsBytes(resourcePath);
        ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length);
        buf.put(bytes);
        buf.flip();
        return buf;
    }

    public static String loadResourcesText(String resourcePath) {
        byte[] bytes = loadResourceAsBytes(resourcePath);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Shared logic to read an InputStream into a byte array.
     */
    public static byte[] loadResourceAsBytes(String resourcePath) {
        // Use try-with-resources to ensure the InputStream is closed automatically
        try (InputStream src = Resources.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (src == null) {
                throw new RuntimeException("Failed to find resource: " + resourcePath);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = src.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }
}