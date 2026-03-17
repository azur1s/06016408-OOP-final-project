package com.project.scenes.menu.mode;

import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.common.UIButton;

public class Mode extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture cardTexture;

    private UIButton stageBtn;
    private UIButton overrunBtn;
    private UIButton upgradeBtn;
    private UIButton shopBtn;
    private UIButton backBtn;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        cardTexture = new Texture("textures/solid.png"); // placeholder for card

        Vec2 btnSize = new Vec2(300, 100);

        stageBtn = new UIButton(
                super.layout.center(-200, 150),
                btnSize,
                "Stage",
                btnTexture);

        overrunBtn = new UIButton(
                super.layout.center(200, 150),
                btnSize,
                "Overrun",
                btnTexture);

        upgradeBtn = new UIButton(
                super.layout.bottomRight(400, 50),
                new Vec2(150, 50),
                "Upgrade",
                btnTexture);

        shopBtn = new UIButton(
                super.layout.bottomRight(200, 50),
                new Vec2(150, 50),
                "Shop",
                btnTexture);

        backBtn = new UIButton(
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
            Engine.setScene(new com.project.scenes.menu.mode.StageMenu());
        });

        overrunBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.mode.OverrunMenu());
        });

        backBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Main());
        });

        upgradeBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.shop.UpgradeMenu());
        });

        shopBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.shop.ShopMenu());
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // Coins Display
        Vec2 coinPos = super.layout.topRight(150, 50);
        font.drawTextAligned(super.batch, "Coins: " + com.project.scenes.menu.PlayerData.coins, coinPos.x, coinPos.y,
                Color.WHITE, 24);

        // draw dummy cards
        super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
        Vec2 stageCardPos = super.layout.center(-200, -100);
        super.batch.draw(cardTexture, stageCardPos.x, stageCardPos.y, 300, 300);

        Vec2 overrunCardPos = super.layout.center(200, -100);
        super.batch.draw(cardTexture, overrunCardPos.x, overrunCardPos.y, 300, 300);

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
