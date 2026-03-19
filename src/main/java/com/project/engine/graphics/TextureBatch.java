package com.project.engine.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import com.project.engine.math.Matrix4f;
import com.project.engine.math.Vec2;
import com.project.engine.utils.Resources;

public class TextureBatch {
    private static final String TEXTURE_VERT = Resources.loadResourcesText("shaders/texture.vert");
    private static final String TEXTURE_FRAG = Resources.loadResourcesText("shaders/texture.frag");

    // A single texture is a quad (2 triangles = 6 vertices).
    // Each vertex needs 4 floats (X, Y, U, V).
    // 6 * 4 = 24 floats per texture.
    private static final int FLOATS_PER_TEXTURE = 24;
    private static final int MAX_TEXTURES = 1000; // Should be enough :)

    private final float[] vertices = new float[MAX_TEXTURES * FLOATS_PER_TEXTURE];
    // How many textures are currently in CPU array
    private int texCount = 0;

    // Current shader

    private Texture lastTex = null;
    private boolean isDrawing = false;
    private Color currentColor = Color.WHITE;

    private final int vaoId, vboId;
    private Shader shader;
    private final Matrix4f projection = new Matrix4f();

    public enum ShaderType {
        TEXTURE, MSDF
    }

    public TextureBatch() {
        shader = new Shader(TEXTURE_VERT, TEXTURE_FRAG);

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        // GL_DYNAMIC_DRAW means that we will update this buffer frequently (every
        // frame)
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Position (X, Y)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture Coords (U, V)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void begin() {
        if (isDrawing)
            throw new IllegalStateException("Already drawing! Call end() before beginning again.");

        isDrawing = true;
        shader.bind();
        shader.setUniformMat4f("u_projection", projection);
        shader.setUniform4f("u_color", currentColor.r, currentColor.g, currentColor.b, currentColor.a);
    }

    /**
     * Adds a texture to the batch.
     *
     * @param texture  The texture to add
     * @param position The position
     * @param w        The width
     * @param h        The height
     */
    public void draw(Texture texture, Vec2 position, float w, float h) {
        draw(texture, position.x, position.y, w, h);
    }

    /**
     * Adds a texture to the batch.
     *
     * @param texture The texture to add
     * @param x       The x position
     * @param y       The y position
     * @param w       The width
     * @param h       The height
     */
    public void draw(Texture texture, float x, float y, float w, float h) {
        if (!isDrawing)
            throw new IllegalStateException("TextureBatch.begin() must be called first!");

        // If we switched textures, or the batch is full, we MUST flush to the GPU
        // before we can start adding vertices for the new texture
        if (texture != lastTex || texCount >= MAX_TEXTURES) {
            flush();
            lastTex = texture;
        }

        // Offset x and y by origin to center the texture
        float ox = texture.getOriginX() * (w / texture.getWidth());
        float oy = texture.getOriginY() * (h / texture.getHeight());
        float dx = x - ox;
        float dy = y - oy;

        // Calculate where we are in our giant float array
        int idx = texCount * FLOATS_PER_TEXTURE;
        // Triangle 1 (Top-Left, Bottom-Left, Bottom-Right)
        // Top-Left Vertex
        vertices[idx++] = dx;
        vertices[idx++] = dy + h; // X, Y
        vertices[idx++] = 0.0f;
        vertices[idx++] = 0.0f; // U, V
        // Bottom-Left Vertex
        vertices[idx++] = dx;
        vertices[idx++] = dy;
        vertices[idx++] = 0.0f;
        vertices[idx++] = 1.0f;
        // Bottom-Right Vertex
        vertices[idx++] = dx + w;
        vertices[idx++] = dy;
        vertices[idx++] = 1.0f;
        vertices[idx++] = 1.0f;

        // Triangle 2 (Top-Left, Bottom-Right, Top-Right)
        // Top-Left Vertex
        vertices[idx++] = dx;
        vertices[idx++] = dy + h;
        vertices[idx++] = 0.0f;
        vertices[idx++] = 0.0f;
        // Bottom-Right Vertex
        vertices[idx++] = dx + w;
        vertices[idx++] = dy;
        vertices[idx++] = 1.0f;
        vertices[idx++] = 1.0f;
        // Top-Right Vertex
        vertices[idx++] = dx + w;
        vertices[idx++] = dy + h;
        vertices[idx++] = 1.0f;
        vertices[idx++] = 0.0f;

        texCount++;
    }

    /**
     * Draws a region of the texture (e.g. a sprite from a sprite sheet, font
     * atlas).
     *
     * @param texture The texture atlas to draw from
     * @param x       The x position
     * @param y       The y position
     * @param w       The width
     * @param h       The height
     * @param u1      The left U coordinate (0.0 - 1.0)
     * @param v1      The top V coordinate (0.0 - 1.0)
     * @param u2      The right U coordinate (0.0 - 1.0)
     * @param v2      The bottom V coordinate (0.0 - 1.0)
     */
    public void drawRegion(Texture texture, float x, float y, float w, float h,
            float u1, float v1, float u2, float v2) {
        if (!isDrawing)
            throw new IllegalStateException("TextureBatch.begin() must be called first!");

        if (texture != lastTex || texCount >= MAX_TEXTURES) {
            flush();
            lastTex = texture;
        }

        float ox = texture.getOriginX() * (w / texture.getWidth());
        float oy = texture.getOriginY() * (h / texture.getHeight());
        float dx = x - ox;
        float dy = y - oy;

        int idx = texCount * FLOATS_PER_TEXTURE;

        vertices[idx++] = dx;
        vertices[idx++] = dy + h;
        vertices[idx++] = u1;
        vertices[idx++] = v1;

        vertices[idx++] = dx;
        vertices[idx++] = dy;
        vertices[idx++] = u1;
        vertices[idx++] = v2;

        vertices[idx++] = dx + w;
        vertices[idx++] = dy;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = dx;
        vertices[idx++] = dy + h;
        vertices[idx++] = u1;
        vertices[idx++] = v1;

        vertices[idx++] = dx + w;
        vertices[idx++] = dy;
        vertices[idx++] = u2;
        vertices[idx++] = v2;

        vertices[idx++] = dx + w;
        vertices[idx++] = dy + h;
        vertices[idx++] = u2;
        vertices[idx++] = v1;

        texCount++;
    }

    /**
     * Draws a solid rectangle with the given color.
     *
     * @param position
     * @param size
     * @param color
     */
    public void drawRect(Vec2 position, Vec2 size, Color color) {
        Texture texture = new Texture("textures/solid.png");
        setColor(color);
        draw(texture, position.x, position.y, size.x, size.y);
    }

    /**
     * Sends the current batch to the GPU and draws it.
     */
    public void flush() {
        if (texCount == 0)
            return; // Nothing to draw
        lastTex.bind();
        glBindVertexArray(vaoId);

        // Upload only the portion of the array we actually used this batch
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Draw the vertices for all textures in this batch (6 vertices per texture)
        glDrawArrays(GL_TRIANGLES, 0, texCount * 6);

        glBindVertexArray(0);

        // Reset our CPU array counter for the next batch
        texCount = 0;
    }

    public void setColor(Color color) {
        // If color changed, flush the current batch before applying new color
        if (!color.equals(currentColor)) {
            flush();
            currentColor = color;
            shader.setUniform4f("u_color", color.r, color.g, color.b, color.a);
        }
    }

    public void end() {
        if (!isDrawing)
            throw new IllegalStateException("SpriteBatch is not drawing!");
        flush();
        isDrawing = false;
        lastTex = null;
        currentColor = Color.WHITE;
    }

    public void setProjection(Matrix4f projection) {
        if (isDrawing)
            flush(); // Flush before changing projection
        this.projection.set(projection);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
        shader.dispose();
    }
}
