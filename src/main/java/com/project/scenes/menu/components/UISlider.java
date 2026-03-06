package com.project.scenes.menu.components;

import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;

public class UISlider {
    private Vec2 position, size;
    private String label;
    private float value; // 0.0 to 1.0
    private Texture bgTexture;
    
    private Color bgColor = new Color(0.2f, 0.2f, 0.2f, 1.0f);
    private Color fillNormalColor = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    private Color fillHoverColor = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    private Color textColor = Color.BLACK;

    private boolean isDragging = false;
    
    public interface OnValueChangedCallback {
        void onValueChanged(float newValue);
    }
    
    private OnValueChangedCallback onValueChanged;

    public UISlider(Vec2 position, Vec2 size, String label, float initialValue, Texture bgTexture) {
        this.position = position;
        this.size = size;
        this.label = label;
        this.value = Math.max(0.0f, Math.min(1.0f, initialValue));
        this.bgTexture = bgTexture;
        
        // Use a lighter gray for the track background to match the reference image
        this.bgColor = new Color(0.7f, 0.7f, 0.7f, 1.0f);
        // Use a dark gray for the filled part
        this.fillNormalColor = new Color(0.4f, 0.4f, 0.4f, 1.0f);
        this.fillHoverColor = new Color(0.3f, 0.3f, 0.3f, 1.0f);
    }

    public void setOnValueChanged(OnValueChangedCallback callback) {
        this.onValueChanged = callback;
    }

    public void setValue(float value) {
        this.value = Math.max(0.0f, Math.min(1.0f, value));
    }

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
            // Calculate new value based on mouse X relative to slider bounds
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
        
        // Define new slim size for the bar part
        float barHeight = 20.0f; // Thin bar
        float labelWidth = 200.0f; // Space reserved for the label on the left
        float barWidth = size.x - labelWidth; // Remaining space for the bar
        
        // The start position of the entire component (left edge)
        float componentStartX = position.x - size.x / 2.0f;
        
        // Draw label on the left
        // Align text left, vertically centered
        font.drawTextUnaligned(batch, label, componentStartX, position.y + 8, textColor, 24);
        
        // The start position of the bar part
        float barStartX = componentStartX + labelWidth;
        float barCenterY = position.y;
        
        // Draw background (dark gray base)
        batch.setColor(bgColor);
        batch.draw(bgTexture, barStartX + barWidth / 2.0f, barCenterY, barWidth, barHeight);
        
        // Draw fill (progress bar from left)
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
