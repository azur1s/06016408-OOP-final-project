package com.project.scenes.menu;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.UIButton;

public class Main extends Scene {
    FontAtlas font;
    Texture logo;
    UIButton playButton;
    UIButton settingButton;
    UIButton quitButton;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);

        logo = new Texture("textures/logo_test.png");

        Texture btnTexture = new Texture("textures/button_test.png");
        Vec2 btnSize = new Vec2(400, 100);

        playButton = new UIButton(
                super.layout.centerRight(300, -150),
                btnSize,
                "Play Now",
                btnTexture);

        settingButton = new UIButton(
                super.layout.centerRight(300, 0),
                btnSize,
                "Setting",
                btnTexture);

        quitButton = new UIButton(
                super.layout.centerRight(300, 150),
                btnSize,
                "Quit",
                btnTexture);

        super.uiManager.add(playButton);
        super.uiManager.add(settingButton);
        super.uiManager.add(quitButton);

        playButton.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Mode());
        });

        settingButton.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Setting());
        });

        quitButton.setOnClick(() -> {
            Engine.requestExit();
        });

        // Load and play the global background music
        Engine.audio.loadSound("bgm_main", "audio/Song.ogg");
        Engine.audio.playSoundIfNotPlaying("bgm_main", true);
    }

    @Override
    public void update(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            Engine.setScene(new com.project.scenes.test.Main());
        }
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        Vec2 titlePos = super.layout.centerLeft(super.layout.res.x / 4f, -100);
        font.drawTextAligned(super.batch, "Java Smash", titlePos.x, titlePos.y, Color.WHITE, 64);

        Vec2 logoPos = super.layout.centerLeft(super.layout.res.x / 4f, 50);
        super.batch.setColor(Color.WHITE);
        super.batch.draw(logo, logoPos.x, logoPos.y, 400f, 200f);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        logo.cleanup();
    }
}