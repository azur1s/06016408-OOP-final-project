package com.project.engine.graphics;

import org.lwjgl.system.MemoryStack;

import com.project.utils.Resources;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {
    private final int id;
    private final int width;
    private final int height;
    // The origin of the texture. Defaults to center of the texture
    private final float originX;
    private final float originY;

    public Texture(String filePath) {
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
        System.out.println(
                "Texture loaded successfully: " + filePath + " (ID: " + id + ", " + width + "x" + height + ")");
    }

    public Texture(ByteBuffer bitmap, int width, int height) {
        this(bitmap, width, height, width / 2.0f, height / 2.0f);
    }

    public Texture(ByteBuffer bitmap, int width, int height, float originX, float originY) {
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
        glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }
}
