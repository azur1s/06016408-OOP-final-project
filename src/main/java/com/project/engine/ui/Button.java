package com.project.engine.ui;

import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.math.Vec2;

public class Button {
    private Vec2 position, size;
    private String text;
    private Color color;
    private Color hoverColor;
    private Color textColor;
    private Color textHoverColor;
    private Texture bgTexture;

    private Runnable onClick;

    public Button(Vec2 position, Vec2 size, String text,
            Color color, Color hoverColor,
            Color textColor, Color textHoverColor,
            Texture bgTexture) {
        this.position = position;
        this.size = size;
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.textHoverColor = textHoverColor;
        this.bgTexture = bgTexture;
    }

    /**
     * Create a new button with default colors (white for normal, light gray for
     * hover)
     */
    public Button(Vec2 position, Vec2 size, String text, Texture bgTexture) {
        this(position, size, text,
                // Texture colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                // Text colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                bgTexture);
    }

    /**
     * Create a new button with specified colors for the button background
     */
    public Button(Vec2 position, Vec2 size, String text, Color color, Color hoverColor, Texture bgTexture) {
        this(position, size, text, color, hoverColor, Color.WHITE, new Color(0.8f, 0.8f, 0.8f, 1.0f), bgTexture);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void update(Vec2 mousePos, boolean mousePressed) {
        if (isHovered(mousePos) && mousePressed && onClick != null) {
            onClick.run();
        }
    }

    public void render(TextureBatch batch, FontAtlas font, Vec2 mousePos) {
        boolean hovered = isHovered(mousePos);
        Color color = hovered ? hoverColor : this.color;
        Color textColor = hovered ? textHoverColor : this.textColor;

        batch.setColor(color);
        batch.draw(bgTexture, position.x, position.y, size.x, size.y);
        batch.setColor(Color.WHITE);

        font.drawTextAligned(batch, text, position.x, position.y, textColor, 16);
    }

    public boolean isHovered(Vec2 mousePos) {
        // Since the origin of the button is at the center, we need to shift the mouse
        // position by half the size of the button
        Vec2 shiftedMousePos = new Vec2(mousePos.x - position.x, mousePos.y - position.y);
        Vec2 topLeft = new Vec2(-size.x / 2f, -size.y / 2f);
        return Vec2.isPointInRect(shiftedMousePos, topLeft, size);
    }
}
