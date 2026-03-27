package engine.graphics;

import org.lwjgl.system.MemoryStack;

import engine.utils.Resources;

import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * OpenGL 2D texture wrapper with image loading and GPU upload utilities.
 */
public class Texture {
    // Cache for textures loaded from files to avoid duplicate loads and save GPU
    // memory.
    private static final Object CACHE_LOCK = new Object();
    // Maps file paths to cached texture data (OpenGL ID, dimensions, reference
    // count)
    private static final Map<String, CachedTexture> FILE_TEXTURE_CACHE = new HashMap<>();

    /**
     * Represents a texture loaded from a file and stored in OpenGL. Multiple
     * Texture instances can share the same OpenGL texture if they were loaded from
     * the same file path. The refCount tracks how many Texture instances are using
     * this OpenGL texture, and when it reaches 0, the texture will be deleted from
     * GPU memory and removed from the cache.
     */
    private static final class CachedTexture {
        final int id;
        final int width;
        final int height;
        /**
         * Number of Texture instances sharing this OpenGL texture. When this reaches 0,
         * the OpenGL texture will be deleted and removed from the cache.
         */
        int refCount;

        CachedTexture(int id, int width, int height) {
            this.id = id;
            this.width = width;
            this.height = height;
            this.refCount = 1;
        }
    }

    private final int id;
    private final int width;
    private final int height;
    private final String cachedPath;
    // The origin of the texture. Defaults to center of the texture
    private final float originX;
    private final float originY;
    private boolean cleanedUp = false;

    /**
     * Loads a texture from a resource path.
     *
     * @param filePath resource path to image data
     */
    public Texture(String filePath) {
        this.cachedPath = filePath;

        // Synchronize to prevent multiple threads from loading the same texture
        // simultaneously
        synchronized (CACHE_LOCK) {
            CachedTexture cachedTexture = FILE_TEXTURE_CACHE.get(filePath);
            if (cachedTexture != null) {
                cachedTexture.refCount++;
                this.id = cachedTexture.id;
                this.width = cachedTexture.width;
                this.height = cachedTexture.height;
                this.originX = this.width / 2.0f;
                this.originY = this.height / 2.0f;
                System.out.println(
                        "Texture loaded from cache: " + filePath + " (ID: " + id + ", " + width + "x" + height
                                + ", refCount: " + cachedTexture.refCount + ")");
                return;
            }

            ByteBuffer image;

            // Load the image data
            try (MemoryStack stack = stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer chans = stack.mallocInt(1);

                // OpenGL expects the Y-axis to start at the bottom, but images
                // usually start at the top, so we need to flip the image vertically
                // stbi_set_flip_vertically_on_load(true);

                // Load the image data with 4 channels (RGBA)
                ByteBuffer imageBuffer;
                try {
                    imageBuffer = Resources.loadResourceToByteBuffer(filePath);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load texture: " + filePath, e);
                }

                image = stbi_load_from_memory(imageBuffer, w, h, chans, 4);
                if (image == null) {
                    throw new RuntimeException("Failed to decode texture: " + filePath + "\n" + stbi_failure_reason());
                }

                this.width = w.get(0);
                this.height = h.get(0);
            }

            // Set origin to center
            this.originX = this.width / 2.0f;
            this.originY = this.height / 2.0f;

            // Upload to OpenGL

            this.id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            // Tell OpenGL how to unpack the pixel data (1 byte alignment)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            // Upload the texture data to GPU
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA,
                    GL_UNSIGNED_BYTE, image);
            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Free the image memory
            stbi_image_free(image);
            FILE_TEXTURE_CACHE.put(filePath, new CachedTexture(id, width, height));
            System.out.println(
                    "Texture loaded successfully: " + filePath + " (ID: " + id + ", " + width + "x" + height + ")");
        }
    }

    /**
     * Creates a texture from raw RGBA bitmap data using a centered origin.
     *
     * @param bitmap pixel data in RGBA format
     * @param width  texture width in pixels
     * @param height texture height in pixels
     */
    public Texture(ByteBuffer bitmap, int width, int height) {
        this(bitmap, width, height, width / 2.0f, height / 2.0f);
    }

    /**
     * Creates a texture from raw RGBA bitmap data.
     *
     * @param bitmap  pixel data in RGBA format
     * @param width   texture width in pixels
     * @param height  texture height in pixels
     * @param originX origin x used for draw alignment
     * @param originY origin y used for draw alignment
     */
    public Texture(ByteBuffer bitmap, int width, int height, float originX, float originY) {
        this.cachedPath = null;
        this.width = width;
        this.height = height;

        // Set origin
        this.originX = originX;
        this.originY = originY;

        // Upload to OpenGL

        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);
        // Tell OpenGL how to unpack the pixel data (1 byte alignment)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        // Upload the texture data to GPU
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, bitmap);
        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        System.out.println(
                "Bitmap texture created successfully (ID: " + id + ", " + width + "x" + height + ")");
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Deletes the texture from GPU memory. Should be called when the texture is
     * no longer needed.
     */
    public void cleanup() {
        if (cleanedUp) {
            return;
        }
        cleanedUp = true;

        if (cachedPath == null) {
            glDeleteTextures(id);
            System.out.println("Texture deleted: ID " + id);
            return;
        }

        synchronized (CACHE_LOCK) {
            CachedTexture cachedTexture = FILE_TEXTURE_CACHE.get(cachedPath);
            if (cachedTexture == null) {
                glDeleteTextures(id);
                System.out.println("Texture deleted: ID " + id);
                return;
            }

            cachedTexture.refCount--;
            if (cachedTexture.refCount <= 0) {
                FILE_TEXTURE_CACHE.remove(cachedPath);
                glDeleteTextures(cachedTexture.id);
            }
        }
    }

    /** @return texture width in pixels */
    public int getWidth() {
        return width;
    }

    /** @return texture height in pixels */
    public int getHeight() {
        return height;
    }

    /** @return OpenGL texture id */
    public int getId() {
        return id;
    }

    /** @return draw origin x in pixels */
    public float getOriginX() {
        return originX;
    }

    /** @return draw origin y in pixels */
    public float getOriginY() {
        return originY;
    }
}
