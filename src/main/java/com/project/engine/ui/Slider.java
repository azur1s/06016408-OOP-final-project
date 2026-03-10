package com.project.engine.ui;

import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;

public class Slider {
    private Vec2 position, size;
    private String label;
    private float value; // 0.0 to 1.0
    private Texture bgTexture;

    private Color bgColor;
    private Color fillNormalColor;
    private Color fillHoverColor;
    private Color textColor;

    private boolean isDragging = false;

    public interface OnValueChangedCallback {
        void onValueChanged(float newValue);
    }

    private OnValueChangedCallback onValueChanged;

    public Slider(Vec2 position, Vec2 size, String label, float initialValue,
            Color bgColor, Color fillNormalColor, Color fillHoverColor, Color textColor,
            Texture bgTexture) {
        this.position = position;
        this.size = size;
        this.label = label;
        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        this.bgColor = bgColor;
        this.fillNormalColor = fillNormalColor;
        this.fillHoverColor = fillHoverColor;
        this.textColor = textColor;
        this.bgTexture = bgTexture;
    }

    public Slider(Vec2 position, Vec2 size, String label, float initialValue, Texture bgTexture) {
        this(position, size, label, initialValue,
                new Color(0.7f, 0.7f, 0.7f, 1.0f),
                new Color(0.4f, 0.4f, 0.4f, 1.0f),
                new Color(0.3f, 0.3f, 0.3f, 1.0f),
                Color.BLACK,
                bgTexture);
    }

    /**
     * Set a callback to be called when the slider value changes. The callback will
     * receive the new slider value (0.0 to 1.0) as a parameter.
     */
    public void setOnValueChanged(OnValueChangedCallback callback) {
        this.onValueChanged = callback;
    }

    /**
     * Set the slider value (0.0 to 1.0). This will not trigger the onValueChanged
     * callback.
     */
    public void setValue(float value) {
        this.value = Math.max(0.0f, Math.min(1.0f, value));
    }

    /**
     * Get the current slider value (0.0 to 1.0).
     */
    public float getValue() {
        return value;
    }

    public void update(Vec2 mousePos, boolean mousePressed) {
        boolean hovered = isHovered(mousePos);

        if (hovered && mousePressed && !isDragging) {
            isDragging = true;
        }

        if (!mousePressed) {
            isDragging = false;
        }

        if (isDragging) {
            float startX = position.x - size.x / 2.0f;

            float newValue = (mousePos.x - startX) / size.x;
            newValue = Math.max(0.0f, Math.min(1.0f, newValue));

            if (this.value != newValue) {
                this.value = newValue;
                if (onValueChanged != null) {
                    onValueChanged.onValueChanged(this.value);
                }
            }
        }
    }

    public void render(TextureBatch batch, FontAtlas font, Vec2 mousePos) {
        boolean hovered = isHovered(mousePos) || isDragging;

        float barHeight = 20.0f;
        float labelWidth = 200.0f;
        float barWidth = size.x - labelWidth;

        float componentStartX = position.x - size.x / 2.0f;

        font.drawTextUnaligned(batch, label, componentStartX, position.y + 8, textColor, 24);

        float barStartX = componentStartX + labelWidth;
        float barCenterY = position.y;

        batch.setColor(bgColor);
        batch.draw(bgTexture, barStartX + barWidth / 2.0f, barCenterY, barWidth, barHeight);

        batch.setColor(hovered ? fillHoverColor : fillNormalColor);
        float fillWidth = barWidth * value;
        if (fillWidth > 0) {
            batch.draw(bgTexture, barStartX + fillWidth / 2.0f, barCenterY, fillWidth, barHeight);
        }

        batch.setColor(Color.WHITE);
    }

    public boolean isHovered(Vec2 mousePos) {
        Vec2 shiftedMousePos = new Vec2(mousePos.x - position.x, mousePos.y - position.y);
        Vec2 topLeft = new Vec2(-size.x / 2f, -size.y / 2f);
        return Vec2.isPointInRect(shiftedMousePos, topLeft, size);
    }

}
