package game.menu.shop;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.Item;
import game.data.PlayerData;
import game.menu.components.UIButton;

public class ShopMenu extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture solidTexture;
    private Texture backgroundTexture;

    // Navigation and Shared UI
    private UIButton backBtn;
    private UIButton tabShopBtnDisplay;

    private UIButton toggleLeftBtn;
    private UIButton toggleRightBtn;

    // Status tracker
    private boolean isSkinTabSelected = false; // false = Item Tab, true = Skin Tab
    private int selectedItemIndex = 0;

    // Costs
    private final int ITEM_COST = 2000;
    private final int[] SKIN_COSTS = { 5000, 10000, 20000 };

    // --- ITEM TAB UI ---
    private List<UIButton> itemBoxBtns = new ArrayList<>();
    private UIButton buyItemBtn;

    // --- SKIN TAB UI ---
    private List<UIButton> skinBoxBtns = new ArrayList<>();
    private UIButton buySkin1Btn;
    private UIButton buySkin2Btn;
    private UIButton buySkin3Btn;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        solidTexture = new Texture("textures/solid.png");
        backgroundTexture = new Texture("textures/bg.png");

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        tabShopBtnDisplay = new UIButton(
                super.layout.topCenter(0, 50),
                new Vec2(300, 40),
                isSkinTabSelected ? "Skin Shop" : "Item Shop",
                solidTexture);

        toggleLeftBtn = new UIButton(
                super.layout.bottomCenter(30, 20),
                new Vec2(40, 40),
                "<",
                btnTexture);

        toggleRightBtn = new UIButton(
                super.layout.bottomCenter(-30, 20),
                new Vec2(40, 40),
                ">",
                btnTexture);

        super.uiManager.add(backBtn);
        super.uiManager.add(toggleLeftBtn);
        super.uiManager.add(toggleRightBtn);

        // ======= ITEM TAB SETUP =======
        for (int i = 0; i < PlayerData.getItemCount(); i++) {
            final int index = i;
            // สร้างปุ่มชิ้นส่วนไอเทมทางซ้าย
            float yOffset = -150 + (i * 80);
            UIButton itemBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f - 60, yOffset),
                    new Vec2(220, 60),
                    PlayerData.getItemDisplayName(i),
                    btnTexture);

            boolean isUnlocked = PlayerData.isItemUnlocked(i);
            UIButton unlockStatusBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f + 160, yOffset),
                    new Vec2(120, 60),
                    isUnlocked ? "UNLOCKED" : "LOCKED",
                    btnTexture);
            // ปุ่มจำลองสถานะการอัพเกรดข้างๆ ควรอัปเดตได้ด้วย
            itemBoxBtns.add(itemBtn);
            itemBoxBtns.add(unlockStatusBtn);
            super.uiManager.add(itemBtn);
            super.uiManager.add(unlockStatusBtn);

            // เมื่อกดที่ชื่อไอเทม ให้สลับหน้าต่าง Info Panel ทางขวาไปแสดงไอเทมนั้น
            itemBtn.setOnClick(() -> {
                selectedItemIndex = index;
                updateBuyButtonText();
            });

            // เมื่อกดที่ปุ่มสถานะ
            unlockStatusBtn.setOnClick(() -> {
                selectedItemIndex = index;
                updateBuyButtonText();
            });
        }

        buyItemBtn = new UIButton(
                super.layout.centerRight(super.layout.res.x / 4f + 30, 200),
                new Vec2(180, 60),
                PlayerData.isItemUnlocked(selectedItemIndex) ? "UNLOCKED"
                        : "BUY (" + ITEM_COST + " C)",
                btnTexture);
        super.uiManager.add(buyItemBtn);

        // ======= SKIN TAB SETUP =======
        // 3 กล่องสกิน
        for (int i = 0; i < 3; i++) {
            float xOffset = (i - 1) * 220;
            UIButton skinBoxBtn = new UIButton(
                    super.layout.center(xOffset, 0),
                    new Vec2(200, 350),
                    "",
                    solidTexture);

            skinBoxBtns.add(skinBoxBtn);
            super.uiManager.add(skinBoxBtn);
        }

        buySkin1Btn = new UIButton(super.layout.center(-220, 120), new Vec2(150, 50),
                game.data.PlayerData.unlockedSkins[0] ? "UNLOCKED" : "BUY (" + SKIN_COSTS[0] + " C)",
                btnTexture);
        buySkin2Btn = new UIButton(super.layout.center(0, 120), new Vec2(150, 50),
                game.data.PlayerData.unlockedSkins[1] ? "UNLOCKED" : "BUY (" + SKIN_COSTS[1] + " C)",
                btnTexture);
        buySkin3Btn = new UIButton(super.layout.center(220, 120), new Vec2(150, 50),
                game.data.PlayerData.unlockedSkins[2] ? "UNLOCKED" : "BUY (" + SKIN_COSTS[2] + " C)",
                btnTexture);

        super.uiManager.add(buySkin1Btn);
        super.uiManager.add(buySkin2Btn);
        super.uiManager.add(buySkin3Btn);

        // ======= EVENT LISTENERS =======
        backBtn.setOnClick(() -> Engine.setScene(new game.menu.mode.Mode()));

        toggleLeftBtn.setOnClick(() -> {
            isSkinTabSelected = false;
            tabShopBtnDisplay.setText("Item Shop");
            updateBuyButtonText();
        });

        toggleRightBtn.setOnClick(() -> {
            isSkinTabSelected = true;
            tabShopBtnDisplay.setText("Skin Shop");
            updateBuyButtonText();
        });

        // Buy System Integration
        buyItemBtn.setOnClick(() -> {
            if (!PlayerData.isItemUnlocked(selectedItemIndex)) {
                if (game.data.PlayerData.hasEnoughCoins(ITEM_COST)) {
                    game.data.PlayerData.deductCoins(ITEM_COST);
                    unlockItem(selectedItemIndex);
                    game.data.PlayerDataSaver.save();
                    itemBoxBtns.get(selectedItemIndex * 2 + 1).setText("UNLOCKED");
                    System.out.println("Purchased " + PlayerData.getItemDisplayName(selectedItemIndex));
                    updateBuyButtonText();
                } else {
                    System.out.println("Not enough coins for " + PlayerData.getItemDisplayName(selectedItemIndex));
                }
            }
        });

        // Loop skin buy logic
        UIButton[] skinBuyBtns = { buySkin1Btn, buySkin2Btn, buySkin3Btn };
        for (int i = 0; i < 3; i++) {
            final int skinIdx = i;
            skinBuyBtns[i].setOnClick(() -> {
                int cost = SKIN_COSTS[skinIdx];
                if (!game.data.PlayerData.unlockedSkins[skinIdx]) {
                    if (game.data.PlayerData.hasEnoughCoins(cost)) {
                        game.data.PlayerData.deductCoins(cost);
                        game.data.PlayerData.unlockedSkins[skinIdx] = true;
                        game.data.PlayerDataSaver.save();
                        System.out.println("Purchased SKIN " + (skinIdx + 1));
                        skinBuyBtns[skinIdx].setText("UNLOCKED");
                    } else {
                        System.out.println("Not enough coins for SKIN " + (skinIdx + 1));
                    }
                }
            });
        }
    }

    private void updateBuyButtonText() {
        if (!isSkinTabSelected) {
            if (PlayerData.isItemUnlocked(selectedItemIndex)) {
                buyItemBtn.setText("UNLOCKED");
            } else {
                buyItemBtn.setText("BUY (" + ITEM_COST + " C)");
            }
        }
    }

    private void unlockItem(int itemIndex) {
        Item item = game.data.PlayerData.ensureItemAtIndex(itemIndex);
        if (item == null) {
            return;
        }
        item.unlocked = true;
    }

    @Override
    public void update(float delta) {
        if (!isSkinTabSelected) {
            for (UIButton btn : itemBoxBtns) {
                btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            }
            buyItemBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        } else {
            for (UIButton btn : skinBoxBtns) {
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

        Vec2 backgroundPos = super.layout.center(0, 0);
        Vec2 backgroundSize = new Vec2(super.layout.res.x, super.layout.res.y);
        super.batch.draw(backgroundTexture, backgroundPos.x, backgroundPos.y, backgroundSize.x, backgroundSize.y);

        // Coins Display
        Vec2 coinPos = super.layout.topRight(150, 50);
        font.drawTextAligned(super.batch, "Coins: " + game.data.PlayerData.coins, coinPos.x, coinPos.y,
                Color.WHITE, 24);

        // Render static display label
        tabShopBtnDisplay.render(super.batch, font, mouseScreen);

        if (!isSkinTabSelected) {
            for (UIButton btn : itemBoxBtns)
                btn.render(super.batch, font, mouseScreen);

            for (int i = 0; i < PlayerData.getItemCount(); i++) {
                Texture icon = PlayerData.getItemIcon(i);
                if (icon == null) {
                    continue;
                }
                Vec2 rowCenter = super.layout.centerLeft(super.layout.res.x / 4f - 60, -150 + (i * 80));
                super.batch.setColor(Color.WHITE);
                super.batch.draw(icon, rowCenter.x - 84f, rowCenter.y, 42f, 42f);
            }

            // Draw item info panel box
            Vec2 infoPanelPos = super.layout.centerRight(super.layout.res.x / 4f + 50, 0);
            super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
            super.batch.draw(solidTexture, infoPanelPos.x, infoPanelPos.y, 350, 400);

            Texture selectedIcon = PlayerData.getItemIcon(selectedItemIndex);
            if (selectedIcon != null) {
                super.batch.setColor(Color.WHITE);
                super.batch.draw(selectedIcon, infoPanelPos.x, infoPanelPos.y - 130f, 72f, 72f);
            }

            font.drawTextAligned(super.batch, PlayerData.getItemDisplayName(selectedItemIndex), infoPanelPos.x,
                    infoPanelPos.y - 55, Color.BLACK, 30);
            font.drawTextAligned(super.batch, "Ability", infoPanelPos.x, infoPanelPos.y - 20, Color.BLACK, 24);

            buyItemBtn.render(super.batch, font, mouseScreen);

        } else {
            for (UIButton btn : skinBoxBtns)
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
        if (backgroundTexture != null)
            backgroundTexture.cleanup();
    }
}
