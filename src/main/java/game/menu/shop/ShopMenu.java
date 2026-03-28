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
    private static final int ITEM_BUTTON_WIDTH = 245;
    private static final int ITEM_BUTTON_HEIGHT = 72;
    private static final int STATUS_BUTTON_WIDTH = 245;
    private static final int STATUS_BUTTON_HEIGHT = 68;
    private static final int BUY_BUTTON_WIDTH = 160;
    private static final int BUY_BUTTON_HEIGHT = 72;

    private FontAtlas font;
    private Texture btnTexture;
    private Texture backBtnTexture;
    private Texture lockedTexture;
    private Texture unlockedTexture;
    private Texture upgradeTexture;
    private Texture solidTexture;
    private Texture backgroundTexture;

    // Navigation and Shared UI
    private UIButton backBtn;
    private int selectedItemIndex = 0;

    // Costs
    private final int ITEM_COST = 2000;
    private final int[] SKIN_COSTS = { 5000, 10000, 20000 };

    // --- ITEM TAB UI ---
    private List<UIButton> itemBoxBtns = new ArrayList<>();
    private UIButton buyItemBtn;

    @Override
    public void preloadAssets() {
        super.preloadAssets();
        Texture.preloadAsync(
                "textures/button_test.png",
                "textures/btn_back.png",
                "textures/shop_upgrade/btn_locked.png",
                "textures/shop_upgrade/btn_unlocked.png",
                "textures/shop_upgrade/btn_upgrade_pk.png",
                "textures/solid.png",
                "textures/bg.png");
    }

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        backBtnTexture = new Texture("textures/btn_back.png");
        lockedTexture = new Texture("textures/shop_upgrade/btn_locked.png");
        unlockedTexture = new Texture("textures/shop_upgrade/btn_unlocked.png");
        upgradeTexture = new Texture("textures/shop_upgrade/btn_upgrade_pk.png");
        solidTexture = new Texture("textures/solid.png");
        backgroundTexture = new Texture("textures/bg.png");

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "",
                backBtnTexture);

        super.uiManager.add(backBtn);

        // ======= ITEM TAB SETUP =======
        for (int i = 0; i < PlayerData.getItemCount(); i++) {
            final int index = i;
            float yOffset = -160 + (i * 96);
            UIButton itemBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f - 55, yOffset),
                    new Vec2(ITEM_BUTTON_WIDTH, ITEM_BUTTON_HEIGHT),
                    PlayerData.getItemDisplayName(i),
                    upgradeTexture);

            boolean isUnlocked = PlayerData.isItemUnlocked(i);
            UIButton unlockStatusBtn = new UIButton(
                    super.layout.centerLeft(super.layout.res.x / 4f + 220, yOffset),
                    new Vec2(STATUS_BUTTON_WIDTH, STATUS_BUTTON_HEIGHT),
                    "",
                    isUnlocked ? unlockedTexture : lockedTexture);
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
                super.layout.centerRight(super.layout.res.x / 4f + 185, 215),
                new Vec2(BUY_BUTTON_WIDTH, BUY_BUTTON_HEIGHT),
                PlayerData.isItemUnlocked(selectedItemIndex) ? "" : "BUY",
                PlayerData.isItemUnlocked(selectedItemIndex) ? unlockedTexture : upgradeTexture);
        super.uiManager.add(buyItemBtn);

        // ======= EVENT LISTENERS =======
        backBtn.setOnClick(() -> Engine.setScene(new game.menu.mode.Mode()));

        // Buy System Integration
        buyItemBtn.setOnClick(() -> {
            if (!PlayerData.isItemUnlocked(selectedItemIndex)) {
                if (game.data.PlayerData.hasEnoughCoins(ITEM_COST)) {
                    game.data.PlayerData.deductCoins(ITEM_COST);
                    unlockItem(selectedItemIndex);
                    game.data.PlayerDataSaver.save();
                    UIButton statusBtn = itemBoxBtns.get(selectedItemIndex * 2 + 1);
                    statusBtn.setText("");
                    statusBtn.setBackgroundTexture(unlockedTexture);
                    System.out.println("Purchased " + PlayerData.getItemDisplayName(selectedItemIndex));
                    updateBuyButtonText();
                } else {
                    System.out.println("Not enough coins for " + PlayerData.getItemDisplayName(selectedItemIndex));
                }
            }
        });
    }

    private void updateBuyButtonText() {
        if (PlayerData.isItemUnlocked(selectedItemIndex)) {
            buyItemBtn.setText("");
            buyItemBtn.setBackgroundTexture(unlockedTexture);
        } else {
            buyItemBtn.setText("BUY");
            buyItemBtn.setBackgroundTexture(upgradeTexture);
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
        for (UIButton btn : itemBoxBtns) {
            btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        }
        buyItemBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        backBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        Vec2 backgroundPos = super.layout.center(0, 0);
        Vec2 backgroundSize = new Vec2(super.layout.res.x, super.layout.res.y);
        super.batch.draw(backgroundTexture, backgroundPos.x, backgroundPos.y, backgroundSize.x, backgroundSize.y);

        Vec2 leftPanelPos = super.layout.centerLeft(super.layout.res.x / 4f + 40, 18);
        Vec2 rightPanelPos = super.layout.centerRight(super.layout.res.x / 4f + 35, 18);

        super.batch.setColor(new Color(0.08f, 0.08f, 0.12f, 0.22f));
        super.batch.draw(solidTexture, leftPanelPos.x + 10, leftPanelPos.y + 10, 620, 470);
        super.batch.draw(solidTexture, rightPanelPos.x + 10, rightPanelPos.y + 10, 490, 470);

        super.batch.setColor(new Color(1f, 1f, 1f, 0.68f));
        super.batch.draw(solidTexture, leftPanelPos.x, leftPanelPos.y, 620, 470);
        super.batch.draw(solidTexture, rightPanelPos.x, rightPanelPos.y, 490, 470);

        Vec2 selectedRowPos = super.layout.centerLeft(super.layout.res.x / 4f + 82, -160 + (selectedItemIndex * 96));
        super.batch.setColor(new Color(1f, 1f, 1f, 0.18f));
        super.batch.draw(solidTexture, selectedRowPos.x, selectedRowPos.y, 530, 86);
        super.batch.setColor(Color.WHITE);

        Vec2 titlePos = super.layout.topCenter(0, 78);
        font.drawTextAligned(super.batch, "SHOP", titlePos.x, titlePos.y, Color.WHITE, 64);

        Vec2 coinPos = super.layout.topRight(155, 52);
        font.drawTextAligned(super.batch, PlayerData.coins + "$", coinPos.x, coinPos.y, Color.WHITE, 28);

        for (UIButton btn : itemBoxBtns) {
            btn.render(super.batch, font, mouseScreen);
        }

        for (int i = 0; i < PlayerData.getItemCount(); i++) {
            Texture icon = PlayerData.getItemIcon(i);
            if (icon == null) {
                continue;
            }
            Vec2 rowCenter = super.layout.centerLeft(super.layout.res.x / 4f - 55, -160 + (i * 96));
            super.batch.setColor(Color.WHITE);
            super.batch.draw(icon, rowCenter.x - 90f, rowCenter.y, 44f, 44f);
        }

        Texture selectedIcon = PlayerData.getItemIcon(selectedItemIndex);
        if (selectedIcon != null) {
            super.batch.setColor(Color.WHITE);
            super.batch.draw(selectedIcon, rightPanelPos.x, rightPanelPos.y - 145f, 170f, 170f);
        }

        font.drawTextAligned(super.batch, PlayerData.getItemDisplayName(selectedItemIndex), rightPanelPos.x,
                rightPanelPos.y - 15f, new Color(0.08f, 0.18f, 0.16f, 1f), 30);

        ArrayList<String> descriptionLines = wrapText(PlayerData.getItemDescription(selectedItemIndex), 24);
        for (int i = 0; i < descriptionLines.size(); i++) {
            font.drawTextAligned(super.batch, descriptionLines.get(i), rightPanelPos.x,
                    rightPanelPos.y + 35f + (i * 36f), new Color(0.08f, 0.18f, 0.16f, 1f), 22);
        }

        font.drawTextAligned(super.batch, ITEM_COST + "$", rightPanelPos.x - 145f, rightPanelPos.y + 195f,
                Color.WHITE, 44);

        buyItemBtn.render(super.batch, font, mouseScreen);
        backBtn.render(super.batch, font, mouseScreen);
    }

    private ArrayList<String> wrapText(String text, int lineLength) {
        ArrayList<String> lines = new ArrayList<>();
        String remaining = text == null ? "" : text.trim();
        if (remaining.isEmpty()) {
            lines.add("No description available.");
            return lines;
        }

        while (remaining.length() > lineLength) {
            int splitIndex = remaining.lastIndexOf(' ', lineLength);
            if (splitIndex <= 0) {
                splitIndex = lineLength;
            }
            lines.add(remaining.substring(0, splitIndex).trim());
            remaining = remaining.substring(splitIndex).trim();
        }

        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }

        return lines;
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (backBtnTexture != null)
            backBtnTexture.cleanup();
        if (lockedTexture != null)
            lockedTexture.cleanup();
        if (unlockedTexture != null)
            unlockedTexture.cleanup();
        if (upgradeTexture != null)
            upgradeTexture.cleanup();
        if (solidTexture != null)
            solidTexture.cleanup();
        if (backgroundTexture != null)
            backgroundTexture.cleanup();
    }
}
