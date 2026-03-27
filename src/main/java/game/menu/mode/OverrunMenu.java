package game.menu.mode;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.components.UIButton;

public class OverrunMenu extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture itemSlotTexture;

    private UIButton playBtn;
    private UIButton backBtn;
    private UIButton item1Btn;
    private UIButton item2Btn;

    @Override
    public void init(int width, int height) {
        // TODO: Change BGM to Overrun specific music here later
        // Example:
        // Engine.audio.loadSound("bgm_overrun", "audio/overrun_song.ogg");
        // Engine.audio.playSound("bgm_overrun", true);

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
                super.layout.center(-60, 0),
                itemSlotSize,
                item1Text,
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                Color.BLACK,
                Color.BLACK,
                itemSlotTexture);

        item2Btn = new UIButton(
                super.layout.center(60, 0),
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
        item1Btn.setOnClick(() -> {
            Engine.setScene(new game.menu.equipment.ItemEquipMenu(0, "Overrun"));
        });

        item2Btn.setOnClick(() -> {
            Engine.setScene(new game.menu.equipment.ItemEquipMenu(1, "Overrun"));
        });

        playBtn.setOnClick(() -> {
            // TODO: ส่งคำสั่งเริ่มเกมในโหมด Overrun (อาจจะสร้าง Scene สำหรับ Overrun
            // แทนหรือส่งพารามิเตอร์)
            Engine.setScene(new game.overrun.stage.SelectStage());
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
        font.drawTextAligned(super.batch, "Overrun", titlePos.x, titlePos.y, Color.WHITE, 64);

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
