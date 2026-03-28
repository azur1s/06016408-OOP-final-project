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
    private Texture backBtnTexture;
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

    // --- ITEM TAB UI ---
    private List<UIButton> itemBoxBtns = new ArrayList<>();
    private UIButton buyItemBtn;

    // --- SKIN TAB UI ---
    private List<UIButton> skinBoxBtns = new ArrayList<>();

    @Override
    public void preloadAssets() {
        super.preloadAssets();
        Texture.preloadAsync(
                "textures/button_test.png",
                "textures/btn_back.png",
                "textures/solid.png",
                "textures/bg.png");
    }

    /**
     * use fo render graphic
     */
    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        backBtnTexture = new Texture("textures/btn_back.png");
        solidTexture = new Texture("textures/solid.png");
        backgroundTexture = new Texture("textures/bg.png");

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "",
                backBtnTexture);

        tabShopBtnDisplay = new UIButton(
                super.layout.topCenter(0, 50),
                new Vec2(300, 40),
                isSkinTabSelected ? "Skin Shop" : "Item Shop",
                new Color(0.2f, 0.2f, 0.2f, 1.0f),
                new Color(0.2f, 0.2f, 0.2f, 1.0f),
                Color.WHITE,
                Color.WHITE,
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
    }

    /**
     * ใช้ทำงานสำหรับอัปเดตข้อความบนปุ่ม "ซื้อ" (Buy Button)
     * โดยเช็คข้อมูลของไอเทมปัจจุบัน (selectedItemIndex) ว่าถูกผู้เล่นปลดล็อกหรือยัง
     * (isItemUnlocked)
     */
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

    /**
     * ทำงานวนลูปซ้ำๆ ทุกๆ เฟรมเรตของเกม
     * เพื่ออัปเดตสถานะและการประมวลผลตรรกะเบื้องหลัง
     * เช่น การจับว่าผู้เล่นขยับเมาส์มาโดนเอาหรือคลิกเมาส์
     *
     * @param delta ค่าความแต่งต่างของเวลาในแต่ละเฟรม
     *              (ใช้ในกรณีคำนวณแอนิเมชันให้เคลื่อนที่เนียนเท่ากันหมด)
     *
     *              ตัวอย่าง:
     *              btn.update(mouseScreen) เป็นการบอกให้ตัวปุ่มรับข้อมูลเมาส์
     *              ถ้าเอาเมาส์ชี้ปุ่ม ปุ่มอาจจะสว่างขึ้น (Hover state)
     */
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
        }

        toggleLeftBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        toggleRightBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        backBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    /**
     * ใช้จัดเรียง วาดกราฟิก และข้อความต่างๆ ลงบนหน้าจอ
     * ฟังก์ชันนี้ถูกเรียกให้ทำงานทุกๆ เฟรมเรตต่อยอดจาก update()
     * โดยการเปลี่ยนแปลงพิกัด แกน X, Y หรือขนาดจะถูกกำหนด ณ จุดนี้
     *
     * @param delta ค่าความแต่งต่างของเวลาแต่ละเฟรม
     *
     *              ตัวอย่าง:
     *              - คำสั่ง batch.draw() ช่วยวาดรูป (Texture)
     *              - คำสั่ง font.drawTextAligned() คือเอาไว้พ่นตัวหนังสือ (Text)
     *              ใส่ฉาก
     */
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
                super.batch.draw(selectedIcon, infoPanelPos.x, infoPanelPos.y + 100f, 96f, 96f);
            }

            font.drawTextAligned(super.batch, PlayerData.getItemDisplayName(selectedItemIndex), infoPanelPos.x,
                    infoPanelPos.y + 20f, Color.BLACK, 32);

            // Description text
            List<String> descriptionLines = new ArrayList<>();
            String description = PlayerData.getItemDescription(selectedItemIndex);
            if (description == null)
                description = "";
            while (description.length() > 30) {
                int splitIndex = description.lastIndexOf(' ', 30);
                if (splitIndex == -1) {
                    splitIndex = 30; // Force split if no space found
                }
                descriptionLines.add(description.substring(0, splitIndex));
                description = description.substring(splitIndex).trim();
            }
            if (!description.isEmpty()) {
                descriptionLines.add(description);
            }
            for (int i = 0; i < descriptionLines.size(); i++) {
                font.drawTextAligned(super.batch, descriptionLines.get(i), infoPanelPos.x,
                        infoPanelPos.y - 40f - (i * 24), Color.BLACK, 20);
            }

            buyItemBtn.render(super.batch, font, mouseScreen);

        } else {
            for (UIButton btn : skinBoxBtns)
                btn.render(super.batch, font, mouseScreen);

            // Draw Skin Texts inside borders
            font.drawTextAligned(super.batch, "SKIN 1", super.layout.center(220, -100).x,
                    super.layout.center(220, -100).y, Color.BLACK, 24);
            font.drawTextAligned(super.batch, "Coming Soon", super.layout.center(220, 120).x,
                    super.layout.center(220, 120).y, Color.BLACK, 24);

            font.drawTextAligned(super.batch, "SKIN 2", super.layout.center(0, -100).x,
                    super.layout.center(0, -100).y, Color.BLACK, 24);
            font.drawTextAligned(super.batch, "Coming Soon", super.layout.center(0, 120).x,
                    super.layout.center(0, 120).y, Color.BLACK, 24);

            font.drawTextAligned(super.batch, "SKIN 3", super.layout.center(-220, -100).x,
                    super.layout.center(-220, -100).y, Color.BLACK, 24);
            font.drawTextAligned(super.batch, "Coming Soon", super.layout.center(-220, 120).x,
                    super.layout.center(-220, 120).y, Color.BLACK, 24);
        }

        toggleLeftBtn.render(super.batch, font, mouseScreen);
        toggleRightBtn.render(super.batch, font, mouseScreen);
        backBtn.render(super.batch, font, mouseScreen);
    }

    /**
     * เมธอดนี้จะถูกเรียกใช้งานเมื่อระบบปิดฉาก/สลับไปเล่นหน้าจออื่น (Scene อื่น)
     * เพื่อรับหน้าที่เคลียร์ความจำแรม (Memory Management) ลบรูปภาพทิ้ง
     * ไม่ให้เกมกินแรมไปเรื่อยๆ
     *
     * ตัวอย่าง:
     * คำสั่ง texture.cleanup() จะบอกการ์ดจอเและ OpenGL
     * ให้ทำลายภาพส่วนนั้นคืนความจำให้ระบบ
     */
    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (backBtnTexture != null)
            backBtnTexture.cleanup();
        if (solidTexture != null)
            solidTexture.cleanup();
        if (backgroundTexture != null)
            backgroundTexture.cleanup();
    }
}
