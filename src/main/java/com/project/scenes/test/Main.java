package com.project.scenes.test;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Button;

public class Main extends Scene {
    FontAtlas font;

    Button[] buttons = new Button[8];
    Button fwButton;
    Button fhButton;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);

        for (int i = 0; i < buttons.length; i++) {
            final int idx = i;
            Vec2 position;

            switch (i) {
                case 0 -> position = super.layout.bottomLeft(0, 0);
                case 1 -> position = super.layout.bottomCenter(0, 0);
                case 2 -> position = super.layout.bottomRight(0, 0);
                case 3 -> position = super.layout.centerLeft(0, 0);
                case 4 -> position = super.layout.center(0, 0);
                case 5 -> position = super.layout.centerRight(0, 0);
                case 6 -> position = super.layout.topLeft(0, 0);
                case 7 -> position = super.layout.topCenter(0, 0);
                case 8 -> position = super.layout.topRight(0, 0);
                default -> position = new Vec2(0, 0);
            }

            buttons[i] = new Button(
                    position,
                    new Vec2(200, 50),
                    "Test Button " + (idx + 1),
                    new Texture("textures/button_test.png"));
            buttons[i].setOnClick(() -> {
                System.out.println("Test Button " + (idx + 1) + " clicked!");
            });
        }

        fwButton = new Button(
                super.layout.center(0, 100),
                super.layout.fullWidth(50),
                "Full Width",
                new Texture("textures/button_test.png"));
        fwButton.setOnClick(() -> {
            System.out.println("Full Width button clicked!");
        });

        fhButton = new Button(
                super.layout.center(100, 0),
                super.layout.fullHeight(50),
                "Full Height",
                new Texture("textures/button_test.png"));
        fhButton.setOnClick(() -> {
            System.out.println("Full Height button clicked!");
        });
    }

    @Override
    public void update(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            Engine.setScene(new com.project.scenes.menu.Main());
        }
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        super.batch.setColor(Color.WHITE);

        for (Button button : buttons) {
            button.render(super.batch, font, super.mouseScreen);
        }

        fwButton.render(super.batch, font, super.mouseScreen);
        fhButton.render(super.batch, font, super.mouseScreen);
    }
}