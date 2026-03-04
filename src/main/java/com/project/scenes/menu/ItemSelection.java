package com.project.scenes.menu;

import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.UIButton;

public class ItemSelection extends Scene {
    FontAtlas font;
    Texture btnTexture;
    Texture itemSlotTexture;

    UIButton playBtn;
    UIButton backBtn;
    UIButton item1Btn;
    UIButton item2Btn;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        itemSlotTexture = new Texture("textures/solid.png"); // placeholder

        Vec2 btnSize = new Vec2(200, 50);
        Vec2 itemSlotSize = new Vec2(100, 100);

        playBtn = new UIButton(
                super.layout.center(0, 150),
                btnSize,
                "Play",
                btnTexture);

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        item1Btn = new UIButton(
                super.layout.center(-80, 0),
                itemSlotSize,
                "",
                itemSlotTexture);

        item2Btn = new UIButton(
                super.layout.center(80, 0),
                itemSlotSize,
                "",
                itemSlotTexture);

        super.uiManager.add(playBtn);
        super.uiManager.add(backBtn);
        super.uiManager.add(item1Btn);
        super.uiManager.add(item2Btn);

        // TODO: สำหรับคนทำระบบเปิดหน้าต่าง shop/inventory ตรงนี้เพื่อเลือกรูปไอเทมมาใส่
        // เมื่อเลือกไอเทมเสร็จ ให้สลับ Texture ของปุ่มนี้เป็นรูปไอเทมนั้น
        item1Btn.setOnClick(() -> System.out.println("Open Select Item 1 UI"));
        item2Btn.setOnClick(() -> System.out.println("Open Select Item 2 UI"));

        playBtn.setOnClick(() -> {
            // TODO: ส่งคำสั่งเริ่มเกม พร้อมกับด่านที่บันทึกไว้ และไอเทมที่เลือกใช้งานใน UI
            // นี้
            Engine.setScene(new com.project.scenes.game.Main());
        });

        backBtn.setOnClick(() -> {
            // ถ้าย้อนกลับคือกลับไปหน้าเลือกด่าน
            Engine.setScene(new com.project.scenes.menu.StageMenu());
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // Texts
        Vec2 titlePos = super.layout.center(0, -150);
        // TODO: คนทำระบบ ให้แสดงชื่อด่านที่ผู้เล่นเพิ่งเลือกมาจากหน้าก่อนหน้าแทน
        // Hardcoded "Stage 1"
        font.drawTextAligned(super.batch, "Stage 1", titlePos.x, titlePos.y, Color.WHITE, 64);

        Vec2 selectItemPos = super.layout.center(0, -70);
        font.drawTextAligned(super.batch, "Select Item", selectItemPos.x, selectItemPos.y, Color.WHITE, 24);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (itemSlotTexture != null)
            itemSlotTexture.cleanup();
    }
}
