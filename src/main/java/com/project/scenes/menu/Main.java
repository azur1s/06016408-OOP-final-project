package com.project.scenes.menu;

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
    Texture logo;
    Button startButton;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);

        logo = new Texture("textures/logo_test.png");

        startButton = new Button(
                new Vec2(super.uiCamera.viewportWidth / 2f, super.uiCamera.viewportHeight / 2f - 200),
                new Vec2(200, 50),
                "Start Game",
                new Color(0.0f, 0.5f, 1.0f, 1.0f),
                new Color(0.0f, 0.4f, 0.8f, 1.0f),
                new Texture("textures/button_test.png"));
        startButton.setOnClick(() -> {
            System.out.println("Start button clicked!");
            Engine.setScene(new com.project.scenes.game.Main());
        });
    }

    @Override
    public void update(float delta) {
        startButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Override
    public void renderWorld(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        super.batch.setColor(Color.WHITE);
        super.batch.draw(logo,
                super.uiCamera.viewportWidth / 2f,
                super.uiCamera.viewportHeight / 2f + 100,
                1000f,
                500f);

        startButton.render(batch, font, super.mouseScreen);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void cleanup() {
    }
}