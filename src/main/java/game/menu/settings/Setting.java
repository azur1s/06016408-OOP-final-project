package game.menu.settings;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.components.UIButton;
import game.menu.components.UISlider;

public class Setting extends Scene {
    private FontAtlas font;
    private UIButton backButton;
    private Texture backgroundTexture;
    private Texture backBtnTexture;
    private Texture solidTexture;
    private UISlider bgmVolumeSlider;
    private UISlider sfxVolumeSlider;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        backBtnTexture = new Texture("textures/btn_back.png");
        backgroundTexture = new Texture("textures/bg.png");
        solidTexture = new Texture("textures/solid.png");

        Texture btnTexture = new Texture("textures/button_test.png");
        Vec2 btnSize = new Vec2(400, 100);

        bgmVolumeSlider = new UISlider(
                super.layout.center(0, -50),
                btnSize,
                "BGM Volume",
                Engine.audio.getBgmVolume(),
                btnTexture);

        sfxVolumeSlider = new UISlider(
                super.layout.center(0, 100),
                btnSize,
                "SFX Volume",
                Engine.audio.getSfxVolume(),
                btnTexture);

        backButton = new UIButton(
                super.layout.center(0, 250),
                new Vec2(256, 92),
                "",
                backBtnTexture);

        bgmVolumeSlider.setOnValueChanged(value -> {
            Engine.audio.setBgmVolume(value);
        });

        sfxVolumeSlider.setOnValueChanged(value -> {
            Engine.audio.setSfxVolume(value);
        });

        super.uiManager.add(bgmVolumeSlider);
        super.uiManager.add(sfxVolumeSlider);
        super.uiManager.add(backButton);

        backButton.setOnClick(() -> {
            Engine.setScene(new game.menu.Main());
        });
    }

    @Override
    public void update(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Engine.setScene(new game.menu.Main());
        }
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.2f, 0.2f, 0.3f, 1.0f);

        Vec2 backgoundPos = super.layout.center(0, 0);
        Vec2 bacgroundSize = new Vec2(1280, 720);
        super.batch.draw(backgroundTexture, backgoundPos, bacgroundSize.x, bacgroundSize.y);

        Vec2 contentPos = super.layout.center(0, 0);
        Vec2 ContentSize = new Vec2(800, 400);
        super.batch.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
        super.batch.draw(solidTexture, contentPos, ContentSize.x, ContentSize.y);

        Vec2 titlePos = super.layout.center(0, -200);
        font.drawTextAligned(super.batch, "Settings", titlePos.x, titlePos.y, Color.WHITE, 64);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
    }
}
