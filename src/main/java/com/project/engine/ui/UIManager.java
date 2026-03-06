package com.project.engine.ui;

import java.util.ArrayList;

import com.project.engine.Engine;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.UISlider;

public class UIManager {
    ArrayList<Button> buttons;
    ArrayList<UISlider> sliders;

    public UIManager() {
        buttons = new ArrayList<>();
        sliders = new ArrayList<>();
    }

    public void add(Button button) {
        buttons.add(button);
    }

    public void add(UISlider slider) {
        sliders.add(slider);
    }

    public void update(Vec2 mousePos, boolean mouseReleased) {
        for (Button button : buttons) {
            button.update(mousePos, mouseReleased);
        }
        for (UISlider slider : sliders) {
            slider.update(mousePos, Engine.input.isMouseButtonPressed(0));
        }
    }

    public void render(TextureBatch batch, FontAtlas font, Vec2 mouseScreen) {
        for (Button button : buttons) {
            button.render(batch, font, mouseScreen);
        }
        for (UISlider slider : sliders) {
            slider.render(batch, font, mouseScreen);
        }
    }

    public void cleanup() {
        for (Button button : buttons) {
            button.cleanup();
        }
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }
}
