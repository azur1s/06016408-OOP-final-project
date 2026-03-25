package engine.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import engine.math.Vec2;
import engine.utils.Resources;

/**
 * Font atlas built from a TrueType font for batched text rendering.
 */
public class FontAtlas {
    /** Texture containing packed glyph bitmap data. */
    private final Texture texture;
    /** Packed glyph metrics for printable ASCII characters (32..126). */
    private final STBTTPackedchar.Buffer charData;
    /** Base font size used while packing glyphs. */
    private final int fontSize;

    /**
     * Creates a font atlas from the specified TTF file and font size/resolution.
     *
     * @param ttfFilePath The path to the TTF file.
     * @param fontSize    The size of the font.
     */
    public FontAtlas(String ttfFilePath, int fontSize) {
        this.fontSize = fontSize;
        int atlasWidth = 1024;
        int atlasHeight = 1024;

        ByteBuffer bitmap = MemoryUtil.memAlloc(atlasWidth * atlasHeight);
        charData = STBTTPackedchar.malloc(95); // ASCII 32..126

        // Allocate a temporary buffer to load the TTF file into memory
        try (MemoryStack stack = MemoryStack.stackPush()) {
            byte[] ttfBytes = Resources.loadResourceAsBytes(ttfFilePath);
            ByteBuffer ttfBuffer = stack.malloc(ttfBytes.length);
            ttfBuffer.put(ttfBytes).flip(); // Flip for reading

            STBTTPackContext packContext = STBTTPackContext.malloc(stack);

            STBTruetype.stbtt_PackBegin(packContext, bitmap, atlasWidth, atlasHeight, 0, 1, MemoryUtil.NULL);
            // Use 2x oversampling for better quality at small sizes
            STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2);
            STBTruetype.stbtt_PackFontRange(packContext, ttfBuffer, 0, fontSize, 32, charData);
            STBTruetype.stbtt_PackEnd(packContext);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load font: " + ttfFilePath, e);
        }

        // Expand the 1-byte grayscale atlas into a 4-byte RGBA buffer
        ByteBuffer rgbaBitmap = MemoryUtil.memAlloc(atlasWidth * atlasHeight * 4);

        for (int i = 0; i < bitmap.capacity(); i++) {
            byte grayscale = bitmap.get(i);
            rgbaBitmap.put((byte) 255); // R
            rgbaBitmap.put((byte) 255); // G
            rgbaBitmap.put((byte) 255); // B
            rgbaBitmap.put(grayscale); // A = grayscale value
        }
        rgbaBitmap.flip(); // Flip for reading

        // Origin at the top-left corner
        this.texture = new Texture(rgbaBitmap, atlasWidth, atlasHeight, 0, 0);

        System.out.println("Font atlas created: " + ttfFilePath + " (size: " + fontSize + "px)");

        // Free native memory
        MemoryUtil.memFree(bitmap);
        MemoryUtil.memFree(rgbaBitmap);
    }

    /**
     * Draws text with optional horizontal and vertical center alignment around (x,
     * y).
     *
     * @param batch
     * @param text
     * @param x               Anchor x position
     * @param y               Anchor y position
     * @param color
     * @param scale           Scale multiplier for text size (1.0f = normal size)
     * @param alignHorizontal If true, the text is centered horizontally around x
     * @param alignVertical   If true, the text is centered vertically around y
     */
    public void drawText(TextureBatch batch, String text, float x, float y, Color color, float scale,
            boolean alignHorizontal, boolean alignVertical) {
        if (text == null || text.isEmpty()) {
            return;
        }

        batch.setColor(color);

        float scaleRatio = scale / fontSize;
        float drawX = x;
        float drawY = y;

        if (alignHorizontal) {
            drawX -= getTextWidth(text, scale) * 0.5f;
        }

        if (alignVertical) {
            float minY = getTextMinY(text);
            float maxY = getTextMaxY(text);
            float centerY = (minY + maxY) * 0.5f;
            drawY = y - centerY * scaleRatio;
        }

        // Draw scaled text
        float currentX = drawX;
        float currentY = drawY;

        // Atlas dimensions to convert pixel coordinates into 0.0 - 1.0 UV coordinates
        float texWidth = texture.getWidth();
        float texHeight = texture.getHeight();

        for (char c : text.toCharArray()) {
            // Skip unsupported characters
            if (c < 32 || c > 126)
                continue;

            STBTTPackedchar charInfo = charData.get(c - 32);

            // Calculate UV / texture coordinates
            float u1 = charInfo.x0() / texWidth;
            float v1 = charInfo.y0() / texHeight;
            float u2 = charInfo.x1() / texWidth;
            float v2 = charInfo.y1() / texHeight;

            // Calculate the quad dimensions
            float width = (charInfo.xoff2() - charInfo.xoff()) * scaleRatio;
            float height = (charInfo.yoff2() - charInfo.yoff()) * scaleRatio;

            // Calculate render position
            float posX = currentX + charInfo.xoff() * scaleRatio;
            // charInfo.yoff2() represents the bottom of the glyph relative to the baseline
            float posY = currentY - charInfo.yoff2() * scaleRatio;

            batch.drawRegion(texture, posX, posY, width, height, u1, v1, u2, v2);

            // Advance the X cursor for the next character
            currentX += charInfo.xadvance() * scaleRatio;
        }
    }

    /**
     * Draws text with the top-left corner of the text at (x, y) without any
     * alignment.
     *
     * @param batch texture batch used for rendering
     * @param text  text to render
     * @param x     anchor x position
     * @param y     anchor y position
     * @param color text color
     * @param scale size scale multiplier
     */
    public void drawTextUnaligned(TextureBatch batch, String text, float x, float y, Color color, float scale) {
        drawText(batch, text, x, y, color, scale, false, false);
    }

    /**
     * Draws text centered both horizontally and vertically around (x, y).
     *
     * @param batch texture batch used for rendering
     * @param text  text to render
     * @param x     anchor x position
     * @param y     anchor y position
     * @param color text color
     * @param scale size scale multiplier
     */
    public void drawTextAligned(TextureBatch batch, String text, float x, float y, Color color, float scale) {
        drawText(batch, text, x, y, color, scale, true, true);
    }

    /**
     * Draws text centered horizontally around x, but with the top of the text at y.
     *
     * @param batch texture batch used for rendering
     * @param text  text to render
     * @param x     anchor x position
     * @param y     anchor y position
     * @param color text color
     * @param scale size scale multiplier
     */
    public void drawTextHorizontalAligned(TextureBatch batch, String text, float x, float y, Color color, float scale) {
        drawText(batch, text, x, y, color, scale, true, false);
    }

    /**
     * Releases native font data and texture resources.
     */
    public void cleanup() {
        texture.cleanup();
        charData.free();
    }

    // Helper methods

    /**
     * Calculates the minimum Y offset of the characters in the text.
     */
    private float getTextMinY(String text) {
        float minY = Float.POSITIVE_INFINITY;

        for (char c : text.toCharArray()) {
            if (c < 32 || c > 126)
                continue;

            STBTTPackedchar charInfo = charData.get(c - 32);
            float glyphTop = -charInfo.yoff2();

            if (glyphTop < minY)
                minY = glyphTop;
        }

        return minY == Float.POSITIVE_INFINITY ? 0 : minY;
    }

    /**
     * Calculates the maximum Y offset of the characters in the text.
     */
    private float getTextMaxY(String text) {
        float maxY = Float.NEGATIVE_INFINITY;

        for (char c : text.toCharArray()) {
            if (c < 32 || c > 126)
                continue;

            STBTTPackedchar charInfo = charData.get(c - 32);
            float glyphBottom = -charInfo.yoff();

            if (glyphBottom > maxY)
                maxY = glyphBottom;
        }

        return maxY == Float.NEGATIVE_INFINITY ? 0 : maxY;
    }

    /**
     * Calculates the width of the given text string when rendered with this font
     * atlas at the specified font size.
     *
     * @param text  The text to measure
     * @param scale Font size
     * @return measured width in world units
     */
    public float getTextWidth(String text, float scale) {
        float scaleRatio = scale / fontSize;
        float width = 0;
        for (char c : text.toCharArray()) {
            if (c < 32 || c > 126)
                continue; // Skip unsupported characters

            STBTTPackedchar charInfo = charData.get(c - 32);
            width += charInfo.xadvance() * scaleRatio;
        }
        return width;
    }

    /**
     * Calculates the height of the given text string when rendered with this font
     * atlas at the specified font size.
     *
     * @param text  The text to measure
     * @param scale Font size
     * @return measured height in world units
     */
    public float getTextHeight(String text, float scale) {
        float scaleRatio = scale / fontSize;
        float minY = getTextMinY(text);
        float maxY = getTextMaxY(text);
        return (maxY - minY) * scaleRatio;
    }

    /**
     * Measures the dimensions of the given text string when rendered with this font
     * atlas at the specified font size.
     *
     * @param text  The text to measure
     * @param scale Font size
     * @return A Vec2 containing the width (x) and height (y) of the text
     */
    public Vec2 measure(String text, float scale) {
        return new Vec2(getTextWidth(text, scale), getTextHeight(text, scale));
    }
}
