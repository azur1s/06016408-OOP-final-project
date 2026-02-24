package com.project.engine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import com.project.math.Vec2;
import com.project.utils.Resources;

import org.lwjgl.nanovg.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class Fonts {
    // Pointer to the NanoVG context.
    private final long vg;
    // Keep font buffers alive for NanoVG
    private final Map<String, ByteBuffer> fontBuffers = new HashMap<>();

    private NVGColor textColor = NVGColor.malloc();

    public Fonts() {
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == 0) {
            throw new RuntimeException("Could not create NanoVG context");
        }
    }

    public void loadFont(String name, String resourcePath) {
        ByteBuffer data = Resources.loadResourceToByteBuffer(resourcePath);
        int font = nvgCreateFontMem(vg, name, data, false);
        if (font == -1) {
            throw new RuntimeException("Could not load font: " + resourcePath);
        }
        fontBuffers.put(name, data);
    }

    /**
     * Starts the NanoVG rendering frame.
     * The screen dimensions are needed so NanoVG knows how to scale the vector
     * math.
     */
    public void begin(float windowWidth, float windowHeight) {
        // The last parameter is the pixel ratio (1.0f for normal displays, 2.0f for
        // retina displays)
        nvgBeginFrame(vg, windowWidth, windowHeight, 1.0f);
    }

    public void setColor(Color color) {
        nvgRGBAf(color.r, color.g, color.b, color.a, textColor);
    }

    /**
     * Draws text to the screen.
     */
    public void draw(String fontName, String text, float x, float y, float fontSize) {
        nvgFontSize(vg, fontSize);
        nvgFontFace(vg, fontName);

        nvgFillColor(vg, textColor);

        nvgText(vg, x, y, text);
    }

    public void drawCenter(String fontName, String text, float x, float y, float fontSize) {
        Vec2 textSize = measure(fontName, text, fontSize);
        draw(fontName, text, x - textSize.x / 2f, y + textSize.y / 2f, fontSize);
    }

    /**
     * Draws text in world-space using the provided camera.
     */
    public void drawWorld(OrthoCamera camera, String fontName, String text, float worldX, float worldY,
            float fontSize) {
        Vec2 screen = camera.worldToScreen(worldX, worldY);
        draw(fontName, text, screen.x, screen.y, fontSize);
    }

    public void drawWorldCenter(OrthoCamera camera, String fontName, String text, float worldX, float worldY,
            float fontSize) {
        Vec2 textSize = measure(fontName, text, fontSize);
        Vec2 screen = camera.worldToScreen(worldX, worldY);
        draw(fontName, text, screen.x - textSize.x / 2f, screen.y + textSize.y / 2f, fontSize);
    }

    public Vec2 measure(String fontName, String text, float fontSize) {
        nvgFontSize(vg, fontSize);
        nvgFontFace(vg, fontName);

        // Allocate a temporary float buffer from C stack
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer bounds = stack.mallocFloat(4);

            nvgTextBounds(vg, 0, 0, text, bounds);

            float width = bounds.get(2) - bounds.get(0);
            float height = bounds.get(3) - bounds.get(1);

            return new Vec2(width, height);
        }
    }

    /**
     * Flushes all vector instructions to the GPU.
     */
    public void end() {
        nvgEndFrame(vg);
    }

    public void cleanup() {
        nvgDelete(vg);
        fontBuffers.clear();
    }
}
