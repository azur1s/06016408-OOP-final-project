package com.project.scenes.menu.components;

import com.project.engine.graphics.Color;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Slider;

public class UISlider extends Slider {

    public UISlider(Vec2 position, Vec2 size, String label, float initialValue, Texture bgTexture) {
        super(position, size, label, initialValue,
                new Color(0.7f, 0.7f, 0.7f, 1.0f),
                new Color(0.4f, 0.4f, 0.4f, 1.0f),
                new Color(0.3f, 0.3f, 0.3f, 1.0f),
                Color.BLACK,
                bgTexture);
    }
}
