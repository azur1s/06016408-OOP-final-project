package com.project.scenes.menu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Button;

public class ShopMenu extends Scene {
    FontAtlas font;
    Texture btnTexture;
    Texture solidTexture;

    // Navigation and Shared UI
    Button backBtn;
    Button tabShopBtnDisplay;

    Button toggleLeftBtn;
    Button toggleRightBtn;

    // Status tracker
    boolean isSkinTabSelected = false; // false = Item Tab, true = Skin Tab

    // --- ITEM TAB UI ---
    ArrayList<Button> itemBoxBtns = new ArrayList<>();
    Button buyItemBtn;
    Button infoItemBtn;

    // --- SKIN TAB UI ---
    ArrayList<Button> skinBoxBtns = new ArrayList<>();
    Button buySkin1Btn;
    Button buySkin2Btn;
    Button buySkin3Btn;

    // TODO: สำหรับคนทำระบบ ให้บันทึกค่าและดึงค่าเหรียญ/เงิน ที่ผู้เล่นมีตรงนี้
    int playerCoins = 1000;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        solidTexture = new Texture("textures/solid.png");

        backBtn = new Button(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        tabShopBtnDisplay = new Button(
                super.layout.topCenter(0, 50),
                new Vec2(300, 40),
                isSkinTabSelected ? "Shop Skin" : "Shop ITEM",
                solidTexture);

        toggleLeftBtn = new Button(
                super.layout.bottomCenter(-30, 20),
                new Vec2(40, 40),
                "<",
                btnTexture);

        toggleRightBtn = new Button(
                super.layout.bottomCenter(30, 20),
                new Vec2(40, 40),
                ">",
                btnTexture);

        super.uiManager.add(backBtn);
        super.uiManager.add(toggleLeftBtn);
        super.uiManager.add(toggleRightBtn);

        // ======= ITEM TAB SETUP =======
        for (int i = 0; i < 5; i++) {
            // สร้างปุ่มชิ้นส่วนไอเทมทางซ้าย
            float yOffset = -150 + (i * 80);
            Button itemBtn = new Button(
                    super.layout.centerLeft(super.layout.res.x / 4f - 60, yOffset),
                    new Vec2(220, 60),
                    "ITEM " + (i + 1),
                    btnTexture);

            // TODO: สร้างปุ่มบอกสถานะการปลดล็อคข้างๆ (แทนปุ่มเฉยๆ ด้วยระบบตรวจสอบการซื้อ)
            Button unlockStatusBtn = new Button(
                    super.layout.centerLeft(super.layout.res.x / 4f + 160, yOffset),
                    new Vec2(120, 60),
                    i == 0 ? "UNLOCKED" : "LOCKED",
                    btnTexture);

            itemBoxBtns.add(itemBtn);
            itemBoxBtns.add(unlockStatusBtn);
            super.uiManager.add(itemBtn);
            super.uiManager.add(unlockStatusBtn);
        }

        infoItemBtn = new Button(
                super.layout.centerRight(super.layout.res.x / 4f - 100, -180),
                new Vec2(120, 30),
                "INFO ITEM",
                btnTexture);
        super.uiManager.add(infoItemBtn);

        buyItemBtn = new Button(
                super.layout.centerRight(super.layout.res.x / 4f + 50, 150),
                new Vec2(150, 50),
                "Buy (100C)",
                btnTexture);
        super.uiManager.add(buyItemBtn);

        // ======= SKIN TAB SETUP =======
        // 3 กล่องสกิน
        for (int i = 0; i < 3; i++) {
            float xOffset = (i - 1) * 220;
            Button skinBoxBtn = new Button(
                    super.layout.center(xOffset, 0),
                    new Vec2(200, 350),
                    "",
                    solidTexture);

            skinBoxBtns.add(skinBoxBtn);
            super.uiManager.add(skinBoxBtn);
        }

        buySkin1Btn = new Button(super.layout.center(-220, 120), new Vec2(150, 50), "Buy", btnTexture);
        buySkin2Btn = new Button(super.layout.center(0, 120), new Vec2(150, 50), "Buy", btnTexture);
        buySkin3Btn = new Button(super.layout.center(220, 120), new Vec2(150, 50), "Buy", btnTexture);

        super.uiManager.add(buySkin1Btn);
        super.uiManager.add(buySkin2Btn);
        super.uiManager.add(buySkin3Btn);

        // ======= EVENT LISTENERS =======
        backBtn.setOnClick(() -> Engine.setScene(new com.project.scenes.menu.Mode()));

        toggleLeftBtn.setOnClick(() -> {
            isSkinTabSelected = false;
            tabShopBtnDisplay.setText("Shop ITEM");
        });

        toggleRightBtn.setOnClick(() -> {
            isSkinTabSelected = true;
            tabShopBtnDisplay.setText("Shop Skin");
        });

        // TODO: ใส่ระบบหักเงินเมื่อซื้อไอเทม/สกิน และอัปเดตสถานะเป็น UNLOCKED
        buyItemBtn.setOnClick(() -> System.out.println("Bought Item!"));
        buySkin1Btn.setOnClick(() -> System.out.println("Bought Skin 1!"));
        buySkin2Btn.setOnClick(() -> System.out.println("Bought Skin 2!"));
        buySkin3Btn.setOnClick(() -> System.out.println("Bought Skin 3!"));
    }

    @Override
    public void update(float delta) {
        if (!isSkinTabSelected) {
            for (Button btn : itemBoxBtns) {
                btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            }
            buyItemBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            infoItemBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        } else {
            for (Button btn : skinBoxBtns) {
                btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            }
            buySkin1Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            buySkin2Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            buySkin3Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        }

        toggleLeftBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        toggleRightBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        backBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // Coins Display
        Vec2 coinPos = super.layout.topRight(150, 50);
        font.drawTextAligned(super.batch, "Coins: " + playerCoins, coinPos.x, coinPos.y, Color.WHITE, 24);

        // Render static display label
        tabShopBtnDisplay.render(super.batch, font, mouseScreen);

        if (!isSkinTabSelected) {
            for (Button btn : itemBoxBtns)
                btn.render(super.batch, font, mouseScreen);

            // Draw item info panel box
            Vec2 infoPanelPos = super.layout.centerRight(super.layout.res.x / 4f + 50, 0);
            super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
            super.batch.draw(solidTexture, infoPanelPos.x, infoPanelPos.y, 350, 400);

            font.drawTextAligned(super.batch, "Ability of Item", infoPanelPos.x, infoPanelPos.y - 50, Color.BLACK, 32);

            infoItemBtn.render(super.batch, font, mouseScreen);
            buyItemBtn.render(super.batch, font, mouseScreen);

        } else {
            for (Button btn : skinBoxBtns)
                btn.render(super.batch, font, mouseScreen);

            // Draw Skin Texts inside borders
            font.drawTextAligned(super.batch, "SKIN 1", super.layout.center(-220, -100).x,
                    super.layout.center(-220, -100).y, Color.BLACK, 24);
            font.drawTextAligned(super.batch, "SKIN 2", super.layout.center(0, -100).x, super.layout.center(0, -100).y,
                    Color.BLACK, 24);
            font.drawTextAligned(super.batch, "SKIN 3", super.layout.center(220, -100).x,
                    super.layout.center(220, -100).y, Color.BLACK, 24);

            buySkin1Btn.render(super.batch, font, mouseScreen);
            buySkin2Btn.render(super.batch, font, mouseScreen);
            buySkin3Btn.render(super.batch, font, mouseScreen);
        }

        toggleLeftBtn.render(super.batch, font, mouseScreen);
        toggleRightBtn.render(super.batch, font, mouseScreen);
        backBtn.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (solidTexture != null)
            solidTexture.cleanup();
    }
}
