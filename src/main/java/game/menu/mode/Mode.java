package game.menu.mode;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.components.UIButton;

public class Mode extends Scene {
    private FontAtlas font;
    private Texture btnTexture;
    private Texture backgroundTexture;
    private Texture backBtnTexture;
    private Texture stageButtonTexture;
    private Texture overrunButtonTexture;
    private Texture upgradeButtonTexture;
    private Texture shopButtonTexture;
    private Texture stageCardTexture;
    private Texture overrunCardTexture;

    private UIButton stageBtn;
    private UIButton overrunBtn;
    private UIButton upgradeBtn;
    private UIButton shopBtn;
    private UIButton backBtn;

    @Override
    public void init(int width, int height) {
        backgroundTexture = new Texture("textures/bg.png");
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        backBtnTexture = new Texture("textures/btn_back.png");
        stageButtonTexture = new Texture("textures/mode/btn_stage.png");
        overrunButtonTexture = new Texture("textures/mode/btn_overrun.png");
        upgradeButtonTexture = new Texture("textures/mode/btn_upgrade.png");
        shopButtonTexture = new Texture("textures/mode/btn_shop.png");
        stageCardTexture = new Texture("textures/mode/stage.png");
        overrunCardTexture = new Texture("textures/mode/overrun.png");

        Vec2 btnSize = new Vec2(256, 92);

        stageBtn = new UIButton(
                super.layout.center(-200, 150),
                btnSize,
                "",
                stageButtonTexture);

        overrunBtn = new UIButton(
                super.layout.center(200, 150),
                btnSize,
                "",
                overrunButtonTexture);

        upgradeBtn = new UIButton(
                super.layout.bottomRight(400, 50),
                new Vec2(150, 50),
                "",
                upgradeButtonTexture);

        shopBtn = new UIButton(
                super.layout.bottomRight(200, 50),
                new Vec2(150, 50),
                "",
                shopButtonTexture);

        backBtn = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "",
                backBtnTexture);

        super.uiManager.add(stageBtn);
        super.uiManager.add(overrunBtn);
        super.uiManager.add(upgradeBtn);
        super.uiManager.add(shopBtn);
        super.uiManager.add(backBtn);

        stageBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.equipment.ItemSelection(new game.menu.mode.StageMenu()));
        });

        overrunBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.mode.OverrunMenu());
        });

        backBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.Main());
        });

        upgradeBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.shop.UpgradeMenu());
        });

        shopBtn.setOnClick(() -> {
            Engine.setScene(new game.menu.shop.ShopMenu());
        });
    }

    @Override
    public void update(float delta) {
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

        // draw dummy cards
        super.batch.setColor(new Color(0.8f, 0.8f, 0.8f, 1.0f));
        Vec2 stageCardPos = super.layout.center(-200, -50);
        super.batch.draw(stageCardTexture, stageCardPos.x, stageCardPos.y, 300 * 1.25f, 300 * 1.25f);

        Vec2 overrunCardPos = super.layout.center(200, -50);
        super.batch.draw(overrunCardTexture, overrunCardPos.x, overrunCardPos.y, 300 * 1.25f, 300 * 1.25f);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (backgroundTexture != null)
            backgroundTexture.cleanup();
        if (btnTexture != null)
            btnTexture.cleanup();
        if (stageButtonTexture != null)
            stageButtonTexture.cleanup();
        if (overrunButtonTexture != null)
            overrunButtonTexture.cleanup();
        if (upgradeButtonTexture != null)
            upgradeButtonTexture.cleanup();
        if (shopButtonTexture != null)
            shopButtonTexture.cleanup();
        if (stageCardTexture != null)
            stageCardTexture.cleanup();
        if (overrunCardTexture != null)
            overrunCardTexture.cleanup();
    }
}
