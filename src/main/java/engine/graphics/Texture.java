package engine.graphics;

import org.lwjgl.system.MemoryStack;

import engine.utils.Resources;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * OpenGL 2D texture wrapper with image loading and GPU upload utilities.
 *
 * Loading from a file path is intentionally split into two phases:
 * 1) Decode image bytes on worker threads ({@link #preloadAsync(String...)})
 * 2) Upload decoded pixels to OpenGL in the {@link Texture} constructor
 *
 * This keeps OpenGL calls on the render thread while still parallelizing
 * expensive IO/decode work during scene transitions.
 */
public class Texture {
    // Cache for textures loaded from files to avoid duplicate loads and save GPU
    // memory.
    private static final Object CACHE_LOCK = new Object();
    // Use a small worker pool for decode/IO work only. OpenGL uploads still run on
    // the render thread.
    private static final int TEXTURE_IO_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
    private static final ExecutorService TEXTURE_IO_EXECUTOR = Executors.newFixedThreadPool(
            TEXTURE_IO_THREADS,
            r -> {
                Thread t = new Thread(r, "texture-io");
                t.setDaemon(true);
                return t;
            });
    // Maps file paths to cached texture data (OpenGL ID, dimensions, reference
    // count)
    private static final Map<String, CachedTexture> FILE_TEXTURE_CACHE = new HashMap<>();
    // Maps file paths to decoded image data futures. Decode happens off the render
    // thread; upload is still done on the render thread.
    private static final Map<String, CompletableFuture<DecodedTexture>> FILE_DECODE_CACHE = new HashMap<>();

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

    private static final class DecodedTexture {
        /** STB-owned RGBA pixel buffer. Must be released with stbi_image_free. */
        final ByteBuffer pixels;
        final int width;
        final int height;

        DecodedTexture(ByteBuffer pixels, int width, int height) {
            this.pixels = pixels;
            this.width = width;
            this.height = height;
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

        CompletableFuture<DecodedTexture> decodeFuture;
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

            // Queue decode if needed, or reuse in-flight work from preloadAsync.
            decodeFuture = getOrCreateDecodeFutureLocked(filePath);
        }

        DecodedTexture decodedTexture;
        try {
            decodedTexture = decodeFuture.join();
        } catch (CompletionException e) {
            synchronized (CACHE_LOCK) {
                if (FILE_DECODE_CACHE.get(filePath) == decodeFuture) {
                    FILE_DECODE_CACHE.remove(filePath);
                }
            }
            throw new RuntimeException("Failed to load texture: " + filePath,
                    e.getCause() != null ? e.getCause() : e);
        }

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

            this.width = decodedTexture.width;
            this.height = decodedTexture.height;
            this.originX = this.width / 2.0f;
            this.originY = this.height / 2.0f;

            try {
                // Must happen on the thread that owns the OpenGL context.
                this.id = uploadDecodedTexture(decodedTexture);
            } finally {
                // DecodedTexture.pixels was allocated by STB and must always be freed.
                stbi_image_free(decodedTexture.pixels);
            }

            FILE_TEXTURE_CACHE.put(filePath, new CachedTexture(id, width, height));
            if (FILE_DECODE_CACHE.get(filePath) == decodeFuture) {
                FILE_DECODE_CACHE.remove(filePath);
            }
            System.out.println(
                    "Texture loaded successfully: " + filePath + " (ID: " + id + ", " + width + "x" + height + ")");
        }
    }

    /**
     * Starts decoding one or more textures on worker threads.
     *
     * This method is safe to call repeatedly. Already decoded, in-flight, or
     * uploaded
     * textures are reused through internal caches.
     *
     * @param filePaths classpath texture paths
     */
    public static void preloadAsync(String... filePaths) {
        if (filePaths == null) {
            return;
        }
        for (String filePath : filePaths) {
            if (filePath == null || filePath.isBlank()) {
                continue;
            }
            synchronized (CACHE_LOCK) {
                if (FILE_TEXTURE_CACHE.containsKey(filePath)) {
                    continue;
                }
                getOrCreateDecodeFutureLocked(filePath);
            }
        }
    }

    /**
     * Releases decoded-but-not-yet-uploaded images and shuts down preload workers.
     * Call this during app shutdown.
     */
    public static void shutdownPreloader() {
        synchronized (CACHE_LOCK) {
            for (CompletableFuture<DecodedTexture> future : FILE_DECODE_CACHE.values()) {
                if (!future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
                    continue;
                }
                DecodedTexture decoded = future.getNow(null);
                if (decoded != null && decoded.pixels != null) {
                    stbi_image_free(decoded.pixels);
                }
            }
            FILE_DECODE_CACHE.clear();
        }

        // Daemon workers do not block process exit, but shutting down avoids stale work
        // in tool/test runs.
        TEXTURE_IO_EXECUTOR.shutdownNow();
    }

    /**
     * Returns an existing decode future or creates a new one.
     *
     * Callers must hold {@link #CACHE_LOCK} when invoking this method.
     */
    private static CompletableFuture<DecodedTexture> getOrCreateDecodeFutureLocked(String filePath) {
        CompletableFuture<DecodedTexture> decodeFuture = FILE_DECODE_CACHE.get(filePath);
        if (decodeFuture != null) {
            return decodeFuture;
        }

        decodeFuture = CompletableFuture.supplyAsync(() -> decodeTexture(filePath), TEXTURE_IO_EXECUTOR);
        FILE_DECODE_CACHE.put(filePath, decodeFuture);
        return decodeFuture;
    }

    /**
     * Performs classpath read + STB decode without touching OpenGL.
     */
    private static DecodedTexture decodeTexture(String filePath) {
        ByteBuffer image;

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer chans = stack.mallocInt(1);

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

            return new DecodedTexture(image, w.get(0), h.get(0));
        }
    }

    /**
     * Uploads decoded pixels into GPU texture storage.
     *
     * Must be called from the render thread while the GL context is current.
     */
    private static int uploadDecodedTexture(DecodedTexture decodedTexture) {
        int uploadedId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, uploadedId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, decodedTexture.width, decodedTexture.height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, decodedTexture.pixels);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        return uploadedId;
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
