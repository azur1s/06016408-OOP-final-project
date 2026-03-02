package com.project.scenes.menu;

import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Button;

public class Mode extends Scene {
    FontAtlas font;
    Texture btnTexture;
    Texture cardTexture;

    Button stageBtn;
    Button overrunBtn;
    Button upgradeBtn;
    Button shopBtn;
    Button backBtn;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        cardTexture = new Texture("textures/solid.png"); // placeholder for card

        Vec2 btnSize = new Vec2(200, 50);

        stageBtn = new Button(
                super.layout.center(-150, 100),
                btnSize,
                "Stage",
                btnTexture);

        overrunBtn = new Button(
                super.layout.center(150, 100),
                btnSize,
                "Overrun",
                btnTexture);

        upgradeBtn = new Button(
                super.layout.bottomRight(400, 50),
                new Vec2(150, 50),
                "Upgrade",
                btnTexture);

        shopBtn = new Button(
                super.layout.bottomRight(200, 50),
                new Vec2(150, 50),
                "Shop",
                btnTexture);

        backBtn = new Button(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        super.uiManager.add(stageBtn);
        super.uiManager.add(overrunBtn);
        super.uiManager.add(upgradeBtn);
        super.uiManager.add(shopBtn);
        super.uiManager.add(backBtn);

        stageBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.StageMenu());
        });

        overrunBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.OverrunMenu());
        });

        backBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Main());
        });

        shopBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.ShopMenu());
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // draw dummy cards
        super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
        Vec2 stageCardPos = super.layout.center(-150, -100);
        super.batch.draw(cardTexture, stageCardPos.x, stageCardPos.y, 200, 250);

        Vec2 overrunCardPos = super.layout.center(150, -100);
        super.batch.draw(cardTexture, overrunCardPos.x, overrunCardPos.y, 200, 250);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (cardTexture != null)
            cardTexture.cleanup();
    }
}
