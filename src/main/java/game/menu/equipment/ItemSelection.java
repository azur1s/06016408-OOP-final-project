package game.menu.equipment;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.components.UIButton;

public class ItemSelection extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture itemSlotTexture;

    private UIButton playBtn;
    private UIButton backBtn;
    private UIButton item1Btn;
    private UIButton item2Btn;

    // Overrun will have OverrunStage as the next Scene, and normal stage will have
    // Stage 1-4 as next.
    private Scene nextScene;

    public ItemSelection(Scene nextScene) {
        this.nextScene = nextScene;
    }

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

        int item1Index = game.data.PlayerData.getEquippedItemIndex(0);
        int item2Index = game.data.PlayerData.getEquippedItemIndex(1);

        String item1Text = item1Index == -1 ? "" : "ITEM " + (item1Index + 1);
        String item2Text = item2Index == -1 ? "" : "ITEM " + (item2Index + 1);

        item1Btn = new UIButton(
                super.layout.center(60, 0),
                itemSlotSize,
                item1Text,
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                Color.BLACK,
                Color.BLACK,
                itemSlotTexture);

        item2Btn = new UIButton(
                super.layout.center(-60, 0),
                itemSlotSize,
                item2Text,
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                Color.BLACK,
                Color.BLACK,
                itemSlotTexture);

        super.uiManager.add(playBtn);
        super.uiManager.add(backBtn);
        super.uiManager.add(item1Btn);
        super.uiManager.add(item2Btn);

        // TODO: สำหรับคนทำระบบเปิดหน้าต่าง shop/inventory ตรงนี้เพื่อเลือกรูปไอเทมมาใส่
        // เมื่อเลือกไอเทมเสร็จ ให้สลับ Texture ของปุ่มนี้เป็นรูปไอเทมนั้น
        item1Btn.setOnClick(() -> {
            Engine.setScene(new game.menu.equipment.ItemEquipMenu(0, "Stage"));
        });

        item2Btn.setOnClick(() -> {
            Engine.setScene(new game.menu.equipment.ItemEquipMenu(1, "Stage"));
        });

        playBtn.setOnClick(() -> {
            // TODO: ส่งคำสั่งเริ่มเกม พร้อมกับด่านที่บันทึกไว้ และไอเทมที่เลือกใช้งานใน UI
            // นี้
            Engine.setScene(nextScene);
        });

        backBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.mode.Mode());
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
        font.drawTextAligned(super.batch, "Stage", titlePos.x, titlePos.y, Color.WHITE, 64);

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
