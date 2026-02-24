package com.project.engine.ui;

import com.project.engine.Color;
import com.project.engine.Fonts;
import com.project.engine.Texture;
import com.project.engine.TextureBatch;
import com.project.math.Vec2;

public class Button {
    private Vec2 position, size;
    private String text;
    private Color color;
    private Color hoverColor;
    private Color textColor;
    private Color textHoverColor;
    private Texture bgTexture;

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

    public void render(TextureBatch batch, Vec2 mousePos) {
        boolean hovered = isHovered(mousePos);

        batch.setColor(hovered ? hoverColor : color);
        batch.draw(bgTexture, position.x, position.y, size.x, size.y);
        batch.setColor(Color.WHITE);
    }

    public void renderText(Fonts fonts, String fontName, Vec2 mousePos) {
        boolean hovered = isHovered(mousePos);

        fonts.setColor(hovered ? textHoverColor : textColor);
        fonts.drawCenter(fontName, text, position.x, position.y, 16);
        fonts.setColor(Color.WHITE);
    }

    public boolean isHovered(Vec2 mousePos) {
        return mousePos.x >= position.x && mousePos.x <= position.x + size.x
                && mousePos.y >= position.y && mousePos.y <= position.y + size.y;
    }

}
