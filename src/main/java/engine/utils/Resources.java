package engine.utils;

import org.lwjgl.BufferUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading resources from the application classpath
 * (src/main/resources).
 */
public final class Resources {

    /**
     * Utility class; not instantiable.
     */
    private Resources() {
    }

    /**
     * Loads a classpath resource into a direct {@link ByteBuffer}.
     *
     * @param resourcePath classpath-relative resource path
     * @return buffer containing the resource bytes, flipped and ready for reading
     * @throws RuntimeException if the resource cannot be found or read
     */
    public static ByteBuffer loadResourceToByteBuffer(String resourcePath) throws RuntimeException {
        byte[] bytes = loadResourceAsBytes(resourcePath);
        ByteBuffer buf = BufferUtils.createByteBuffer(bytes.length);
        buf.put(bytes);
        buf.flip();
        return buf;
    }

    /**
     * Loads a classpath resource as UTF-8 text.
     *
     * @param resourcePath classpath-relative resource path
     * @return decoded UTF-8 string contents
     * @throws RuntimeException if the resource cannot be found or read
     */
    public static String loadResourcesText(String resourcePath) throws RuntimeException {
        byte[] bytes = loadResourceAsBytes(resourcePath);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Loads a classpath resource as a byte array.
     *
     * @param resourcePath classpath-relative resource path
     * @return raw resource bytes
     * @throws RuntimeException if the resource cannot be found or read
     */
    public static byte[] loadResourceAsBytes(String resourcePath) throws RuntimeException {
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