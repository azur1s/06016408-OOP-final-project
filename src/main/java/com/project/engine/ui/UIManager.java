package com.project.engine.ui;

import java.util.ArrayList;

import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;

public class UIManager {
    ArrayList<Button> buttons;

    public UIManager() {
        buttons = new ArrayList<>();
    }

    public void add(Button button) {
        buttons.add(button);
    }

    public void update(Vec2 mousePos, boolean mouseReleased) {
        for (Button button : buttons) {
            button.update(mousePos, mouseReleased);
        }
    }

    public void render(TextureBatch batch, FontAtlas font, Vec2 mouseScreen) {
        for (Button button : buttons) {
            button.render(batch, font, mouseScreen);
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
