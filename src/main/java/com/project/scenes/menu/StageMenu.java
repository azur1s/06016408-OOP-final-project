package com.project.scenes.menu;

import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.scenes.menu.components.UIButton;

public class StageMenu extends Scene {
    FontAtlas font;
    Texture btnTexture;
    UIButton backBtn;

    // โหนดสำหรับโชว์ด่านย่อย
    UIButton node1;
    UIButton node2;
    UIButton node3;
    UIButton node4;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        btnTexture = new Texture("textures/button_test.png");
        Vec2 nodeSize = new Vec2(100, 100);

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                btnTexture);

        // นำปุ่มมาเรียงไว้ตรงกลางหน้าจอ (0,0 ของ layout.center คือจุดกึ่งกลางจอพอดี)
        node1 = new UIButton(super.layout.center(-250, -50), nodeSize, "4", btnTexture);
        node2 = new UIButton(super.layout.center(-100, 30), nodeSize, "3", btnTexture);
        node3 = new UIButton(super.layout.center(100, -30), nodeSize, "2", btnTexture);
        node4 = new UIButton(super.layout.center(250, 40), nodeSize, "1", btnTexture);

        super.uiManager.add(backBtn);
        super.uiManager.add(node1);
        super.uiManager.add(node2);
        super.uiManager.add(node3);
        super.uiManager.add(node4);

        // TODO: สำหรับคนทำระบบ ให้เพิ่มโค้ดเช็คว่าด่านไหนถูกปลดล็อคแล้วบ้าง
        // หากด่านไหนยังไม่ปลดล็อค อาจจะเปลี่ยนสีปุ่มให้เป็นสีเทา หรือ set setOnClick
        // เป็น null เพื่อไม่ให้กดได้
        // TODO: เมื่อผู้เล่นกดเลือกด่าน ให้เก็บ state ไว้ว่าเล่นด่านอะไร แล้วเปิดหน้า
        // ItemSelection ต่อ
        Runnable onNodeSelected = () -> {
            Engine.setScene(new com.project.scenes.menu.ItemSelection());
        };

        node1.setOnClick(onNodeSelected);
        node2.setOnClick(onNodeSelected);
        node3.setOnClick(onNodeSelected);
        node4.setOnClick(onNodeSelected);

        backBtn.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Mode());
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // TODO: วาดเส้นประเชื่อมโหนด หรือวาดภาพพื้นหลังของกล่องตรงนี้
        // super.batch.draw( ...เส้นเชื่อมด่าน... );

        // Texts
        Vec2 mapTitlePos = super.layout.center(0, -150);
        font.drawTextAligned(super.batch, "Stage Map", mapTitlePos.x, mapTitlePos.y, Color.WHITE, 64);

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
