package game.menu;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.components.UIButton;

public class Main extends Scene {
    private FontAtlas font;
    private Texture logo;
    private UIButton playButton;
    private UIButton settingButton;
    private UIButton quitButton;

    @Override
    public void init(int width, int height) {
        PlayerDataSaver.load();

        font = new FontAtlas("GeistMono-Regular.otf", 32);

        logo = new Texture("textures/logo.png");

        Texture btnTexture = new Texture("textures/button_test.png");
        Vec2 btnSize = new Vec2(400, 100);

        playButton = new UIButton(
                super.layout.centerRight(300, -150),
                btnSize,
                "Play Now",
                btnTexture);

        settingButton = new UIButton(
                super.layout.centerRight(300, 0),
                btnSize,
                "Setting",
                btnTexture);

        quitButton = new UIButton(
                super.layout.centerRight(300, 150),
                btnSize,
                "Quit",
                btnTexture);

        super.uiManager.add(playButton);
        super.uiManager.add(settingButton);
        super.uiManager.add(quitButton);

        playButton.setOnClick(() -> {
            if (PlayerData.selectedCharacter == -1) Engine.setScene(new game.menu.mode.Mode());
            else Engine.setScene(new game.menu.selectcharacter.selectcharacter());
        });

        settingButton.setOnClick(() -> {
            Engine.setScene(new game.menu.settings.Setting());
        });

        quitButton.setOnClick(() -> {
            PlayerDataSaver.save();
            Engine.requestExit();
        });

        // Load and play the global background music
        Engine.audio.loadSound("bgm_main", "audio/Song.ogg");
        Engine.audio.playSoundIfNotPlaying("bgm_main", true);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        float logoScale = 1.0f + 0.05f * (float) Math.sin(Engine.graphics.getTime() * 0.5f);

        Vec2 logoPos = super.layout.centerLeft(super.layout.res.x / 4f + 50f, 0f);
        super.batch.setColor(Color.WHITE);
        super.batch.draw(logo, logoPos.x, logoPos.y, 3800f / 6f * logoScale, 3000f / 6f * logoScale);

        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        PlayerDataSaver.save();
        logo.cleanup();
    }
}