package com.project.scenes.menu.shop;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.common.UIButton;

public class UpgradeMenu extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture solidTexture;

    // Shared UI
    private UIButton backBtn;
    private UIButton tabTitleBtn;

    // --- ITEM LIST UI ---
    private List<UIButton> itemSelectBtns = new ArrayList<>();
    private List<UIButton> itemUpgradeBtns = new ArrayList<>();

    // Status tracker
    private int selectedItemIndex = 0; // The item currently shown on the right panel

    // --- INFO PANEL UI ---
    private UIButton infoItemBtn;
    private UIButton mainUpgradeBtn;

    private UIButton stat0Btn;
    private UIButton stat1Btn;
    private UIButton stat2Btn;
    private int selectedStatIndex = 0;

    // Removed constant UPGRADE_COST to use dynamic formula

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        solidTexture = new Texture("textures/solid.png");

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        tabTitleBtn = new UIButton(
                super.layout.topCenter(0, 50),
                new Vec2(300, 40),
                "Upgrade",
                new Color(0.2f, 0.2f, 0.2f, 1.0f),
                new Color(0.2f, 0.2f, 0.2f, 1.0f),
                Color.WHITE,
                Color.WHITE,
                solidTexture);

        super.uiManager.add(backBtn);

        Color tColor = new Color(0, 0, 0, 0);
        Color hColor = new Color(1, 1, 1, 0.3f);
        Vec2 statBtnSize = new Vec2(380, 70);
        stat0Btn = new UIButton(super.layout.centerRight(super.layout.res.x / 4f + 30, -80), statBtnSize, "", tColor,
                hColor, solidTexture);
        stat1Btn = new UIButton(super.layout.centerRight(super.layout.res.x / 4f + 30, 0), statBtnSize, "", tColor,
                hColor, solidTexture);
        stat2Btn = new UIButton(super.layout.centerRight(super.layout.res.x / 4f + 30, 80), statBtnSize, "", tColor,
                hColor, solidTexture);

        stat0Btn.setOnClick(() -> {
            selectedStatIndex = 0;
            updateUpgradeButtonText();
        });
        stat1Btn.setOnClick(() -> {
            selectedStatIndex = 1;
            updateUpgradeButtonText();
        });
        stat2Btn.setOnClick(() -> {
            selectedStatIndex = 2;
            updateUpgradeButtonText();
        });

        // ปุ่มอัพเกรดหลักในแผง Info Panel (Initialized here to avoid NPE in
        // updateUpgradeButtonText)
        mainUpgradeBtn = new UIButton(
                super.layout.centerRight(super.layout.res.x / 4f + 30, 200),
                new Vec2(180, 60),
                "UPGRADE",
                btnTexture);
        super.uiManager.add(mainUpgradeBtn);

        updateUpgradeButtonText();

        // ======= ITEM LIST SETUP =======
        for (int i = 0; i < 5; i++) {
            final int index = i;
            float yOffset = -150 + (i * 80);

            // ปุ่มชื่อไอเทมทางซ้าย
            UIButton itemBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f - 60, yOffset),
                    new Vec2(220, 60),
                    "ITEM " + (i + 1),
                    btnTexture);

            boolean isUnlocked = com.project.scenes.menu.PlayerData.unlockedItems[i];

            // ปุ่มจำลองสถานะการอัพเกรดข้างๆ
            UIButton upgradeStatusBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f + 160, yOffset),
                    new Vec2(120, 60),
                    isUnlocked ? "UPGRADE" : "LOCKED",
                    btnTexture);

            // เมื่อกดที่ชื่อไอเทม ให้สลับหน้าต่าง Info Panel ทางขวาไปแสดงไอเทมนั้น
            itemBtn.setOnClick(() -> {
                selectedItemIndex = index;
                updateUpgradeButtonText();
            });

            // เมื่อกด UPGRADE ทางซ้าย
            upgradeStatusBtn.setOnClick(() -> {
                selectedItemIndex = index;
                updateUpgradeButtonText();
            });

            itemSelectBtns.add(itemBtn);
            itemUpgradeBtns.add(upgradeStatusBtn);
            super.uiManager.add(itemBtn);
            super.uiManager.add(upgradeStatusBtn);
        }

        infoItemBtn = new UIButton(
                super.layout.centerRight(super.layout.res.x / 4f - 100, -220),
                new Vec2(120, 30),
                "INFO ITEM",
                btnTexture);
        super.uiManager.add(infoItemBtn);

        // ======= EVENT LISTENERS =======
        backBtn.setOnClick(() -> Engine.setScene(new com.project.scenes.menu.mode.Mode()));

        mainUpgradeBtn.setOnClick(() -> {
            if (!com.project.scenes.menu.PlayerData.unlockedItems[selectedItemIndex])
                return;

            int cost = getUpgradeCost(selectedItemIndex, selectedStatIndex);

            if (com.project.scenes.menu.PlayerData.hasEnoughCoins(cost)) {
                com.project.scenes.menu.PlayerData.deductCoins(cost);
                com.project.scenes.menu.PlayerData.itemStatsLevels[selectedItemIndex][selectedStatIndex]++;
                com.project.scenes.menu.PlayerDataSaver.save();
                String[] statNames = { "Cooldown", "Damage", "Duration" };
                System.out.println(
                        "Upgraded " + statNames[selectedStatIndex] + " of Item " + (selectedItemIndex + 1) + "!");
                updateUpgradeButtonText();
            } else {
                System.out.println("Not enough coins to upgrade.");
            }
        });
    }

    private int getUpgradeCost(int itemIdx, int statIdx) {
        int currentLevel = com.project.scenes.menu.PlayerData.itemStatsLevels[itemIdx][statIdx];
        // Base cost is 500, increases by 250 for each level
        return 500 + (currentLevel * 250);
    }

    private void updateUpgradeButtonText() {
        if (!com.project.scenes.menu.PlayerData.unlockedItems[selectedItemIndex]) {
            mainUpgradeBtn.setText("UPGRADE");
            return;
        }
        int cost = getUpgradeCost(selectedItemIndex, selectedStatIndex);
        mainUpgradeBtn.setText("UPGRADE (" + cost + " C)");
    }

    @Override
    public void update(float delta) {
        for (UIButton btn : itemSelectBtns)
            btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        for (UIButton btn : itemUpgradeBtns)
            btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        infoItemBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        mainUpgradeBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        backBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        stat0Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        stat1Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        stat2Btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // Coins Display
        Vec2 coinPos = super.layout.topRight(150, 50);
        font.drawTextAligned(super.batch, "Coins: " + com.project.scenes.menu.PlayerData.coins, coinPos.x, coinPos.y,
                Color.WHITE, 24);

        // Render Title
        tabTitleBtn.render(super.batch, font, mouseScreen);

        // Render item list
        for (UIButton btn : itemSelectBtns)
            btn.render(super.batch, font, mouseScreen);
        for (UIButton btn : itemUpgradeBtns)
            btn.render(super.batch, font, mouseScreen);

        // Draw Info panel background
        Vec2 infoPanelPos = super.layout.centerRight(super.layout.res.x / 4f + 30, 0);
        super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
        super.batch.draw(solidTexture, infoPanelPos.x, infoPanelPos.y, 400, 480);
        super.batch.setColor(Color.WHITE);

        boolean isUnlocked = com.project.scenes.menu.PlayerData.unlockedItems[selectedItemIndex];

        // Render Info panel details
        font.drawTextAligned(super.batch, "ITEM " + (selectedItemIndex + 1), infoPanelPos.x, infoPanelPos.y + 180,
                Color.BLACK, 48);

        if (isUnlocked) {
            // Render highlight for selected stat
            Vec2 selectedPos = super.layout.centerRight(super.layout.res.x / 4f + 30, -80 + (selectedStatIndex * 80));
            super.batch.setColor(new Color(1.0f, 1.0f, 0.0f, 0.4f)); // Yellow highlight
            super.batch.draw(solidTexture, selectedPos.x, selectedPos.y, 380, 70);
            super.batch.setColor(Color.WHITE);

            // Draw Stat Buttons
            stat0Btn.render(super.batch, font, mouseScreen);
            stat1Btn.render(super.batch, font, mouseScreen);
            stat2Btn.render(super.batch, font, mouseScreen);

            // --- STATS BARS CALCULATION ---
            float barStartX = infoPanelPos.x - 170;

            float currentCooldownLevel = com.project.scenes.menu.PlayerData.itemStatsLevels[selectedItemIndex][0];
            float currentDamageLevel = com.project.scenes.menu.PlayerData.itemStatsLevels[selectedItemIndex][1];
            float currentDurationLevel = com.project.scenes.menu.PlayerData.itemStatsLevels[selectedItemIndex][2];

            float baseCooldown = 10f; // placeholder base values
            float baseDamage = 10f;
            float baseDuration = 5f;

            drawStatBar("Cooldown", barStartX, super.layout.centerRight(0, -80).y,
                    baseCooldown + (currentCooldownLevel * 1f), 30.0f);
            drawStatBar("Damage", barStartX, super.layout.centerRight(0, 0).y, baseDamage + (currentDamageLevel * 5f),
                    100.0f);
            drawStatBar("Duration", barStartX, super.layout.centerRight(0, 80).y,
                    baseDuration + (currentDurationLevel * 2f), 30.0f);

            infoItemBtn.render(super.batch, font, mouseScreen);
            mainUpgradeBtn.render(super.batch, font, mouseScreen);
        } else {
            font.drawTextAligned(super.batch, "LOCKED", infoPanelPos.x, infoPanelPos.y, Color.BLACK, 64);
        }

        backBtn.render(super.batch, font, mouseScreen);
    }

    private void drawStatBar(String label, float x, float y, float currentVal, float maxVal) {
        font.drawTextAligned(super.batch, label, x + 60, y, Color.BLACK, 24);

        // Background Bar
        super.batch.setColor(Color.WHITE);
        super.batch.draw(solidTexture, x + 230, y, 200, 20);

        // Foreground (Fill) Bar
        float fillPercentage = Math.min(1.0f, Math.max(0.0f, currentVal / maxVal));
        float fillWidth = 200 * fillPercentage;
        float fillCenterX = x + 130 + (fillWidth / 2f);
        super.batch.setColor(new Color(0.4f, 0.4f, 0.4f, 1.0f));
        super.batch.draw(solidTexture, fillCenterX, y, fillWidth, 20);
        super.batch.setColor(Color.WHITE);
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
