package game.menu;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.menu.components.UIButton;

public class Main extends Scene {
    private FontAtlas font;
    private Texture logo;
    private UIButton playButton;
    private UIButton settingButton;
    private UIButton quitButton;
    private Texture backgroundTexture;
    private Texture startTexture, settingTexture, quitTexture;
    @Override
    public void init(int width, int height) {
        PlayerDataSaver.load();

        font = new FontAtlas("GeistMono-Regular.otf", 32);

        logo = new Texture("textures/logo.png");

        backgroundTexture = new Texture("textures/bg.png");

        startTexture = new Texture("textures/main/btn_start.png");
        settingTexture = new Texture("textures/main/btn_setting.png");
        quitTexture = new Texture("textures/main/btn_quit.png");

        Vec2 btnSize = new Vec2(256, 92);

        playButton = new UIButton(
                super.layout.centerRight(300, -110),
                btnSize,
                "",
                startTexture);

        settingButton = new UIButton(
                super.layout.centerRight(300, 20),
                btnSize,
                "",
                settingTexture);

        quitButton = new UIButton(
                super.layout.centerRight(300, 150),
                btnSize,
                "",
                quitTexture);

        super.uiManager.add(playButton);
        super.uiManager.add(settingButton);
        super.uiManager.add(quitButton);

        playButton.setOnClick(() -> {
            if (PlayerData.selectedCharacter != -1 && PlayerData.hasCompletedTutorial)
                Engine.setScene(new game.menu.mode.Mode());
            else if (PlayerData.selectedCharacter == -1)
                Engine.setScene(new game.menu.selectCharacter.selectCharacter());
            else
                Engine.setScene(new game.menu.tutorial.tutorial());
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

        float logoScale = 1.3f + 0.05f * (float) Math.sin(Engine.graphics.getTime() * 0.5f);

        Vec2 backgroundPos = super.layout.center(0, 0);
        Vec2 backgroundSize = new Vec2(super.layout.res.x,super.layout.res.y);

        super.batch.draw(backgroundTexture, backgroundPos, backgroundSize.x,backgroundSize.y);

        Vec2 logoPos = super.layout.centerLeft(super.layout.res.x * 1.27f / 4f + 50f, 0f);
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