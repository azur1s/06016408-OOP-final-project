package com.project.scenes.menu.equipment;

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
import com.project.scenes.menu.PlayerData;
import com.project.scenes.menu.components.common.UIButton;

public class ItemEquipMenu extends Scene {
    private FontAtlas font;
    private Texture btnTexture;

    private UIButton backBtn;
    private List<UIButton> itemBtns = new ArrayList<>();

    // 0 or 1 for which slot we are filling
    private int slotToEquip;

    // "Overrun" or "Stage" so we know where to go back
    private String sourceMenu;

    public ItemEquipMenu(int slotToEquip, String sourceMenu) {
        this.slotToEquip = slotToEquip;
        this.sourceMenu = sourceMenu;
    }

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);
        super.uiManager.add(backBtn);

        backBtn.setOnClick(() -> {
            goBack();
        });

        // Add buttons for each item (0 to 4)
        for (int i = 0; i < 5; i++) {
            final int itemIndex = i;

            float yOffset = -150 + (i * 80);
            boolean isUnlocked = PlayerData.unlockedItems[itemIndex];

            String btnText = "ITEM " + (itemIndex + 1);
            if (!isUnlocked) {
                btnText += " (LOCKED)";
            }

            UIButton itemBtn = new UIButton(
                    super.layout.center(0, yOffset),
                    new Vec2(300, 60),
                    btnText,
                    btnTexture);

            itemBtns.add(itemBtn);
            super.uiManager.add(itemBtn);

            itemBtn.setOnClick(() -> {
                if (PlayerData.unlockedItems[itemIndex]) {
                    // TODO (For Backend Devs): Add additional logic here if equipping an item
                    // requires server validation
                    // If this item is already in another slot, swap: move the current slot's item
                    // to that slot
                    int itemCurrentSlot = -1;
                    for (int s = 0; s < PlayerData.equippedItems.length; s++) {
                        if (s != slotToEquip && PlayerData.equippedItems[s] == itemIndex) {
                            itemCurrentSlot = s;
                            break;
                        }
                    }
                    if (itemCurrentSlot != -1) {
                        // Swap: put displaced item into the slot where selected item came from
                        PlayerData.equippedItems[itemCurrentSlot] = PlayerData.equippedItems[slotToEquip];
                        System.out.println("Swapped: moved Item " + (PlayerData.equippedItems[itemCurrentSlot] + 1)
                                + " to Slot " + (itemCurrentSlot + 1));
                    }
                    // Equipping the item into the correct slot
                    PlayerData.equippedItems[slotToEquip] = itemIndex;
                    System.out.println("Equipped Item " + (itemIndex + 1) + " into Slot " + (slotToEquip + 1));
                    goBack();
                } else {
                    // TODO (For Backend Devs): Maybe trigger a sound or a Toast message that it's
                    // locked
                    System.out.println("Cannot equip locked item!");
                }
            });
        }
    }

    private void goBack() {
        if ("Overrun".equals(sourceMenu)) {
            Engine.setScene(new com.project.scenes.menu.mode.OverrunMenu());
        } else {
            Engine.setScene(new com.project.scenes.menu.equipment.ItemSelection());
        }
    }

    @Override
    public void update(float delta) {
        backBtn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        for (UIButton btn : itemBtns) {
            btn.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        }
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        Vec2 titlePos = super.layout.center(0, -250);
        font.drawTextAligned(super.batch, "Equip Item to Slot " + (slotToEquip + 1), titlePos.x, titlePos.y,
                Color.WHITE, 48);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
    }
}
