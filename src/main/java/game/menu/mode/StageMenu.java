package game.menu.mode;

import static org.lwjgl.opengl.GL11.*;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import engine.ui.Button;
import game.data.PlayerData;
import game.menu.components.UIButton;
import game.overrun.stage.StageConfigs;

public class StageMenu extends Scene {
    private static final int STAGE_COUNT = 4;

    private FontAtlas font;
    private Texture buttonTexture;
    private Texture[] stageButtonTextures;
    private Texture unlockedProgressBackground;
    private Button[] stageButtons;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        buttonTexture = new Texture(StageConfigs.getButtonTexturePath());
        unlockedProgressBackground = new Texture(StageConfigs.getStageBackgroundPaths());

        createStageButtons();
    }

    // TODO: fix each button position within the scene to make it fit the background
    // image better
    private void createStageButtons() {
        stageButtons = new Button[STAGE_COUNT];
        stageButtonTextures = new Texture[STAGE_COUNT];
        String[] stageButtonPaths = StageConfigs.getStageButtonPaths();
        Vec2[] stageButtonSizes = {
                new Vec2(719, 313),
                new Vec2(696, 313),
                new Vec2(685, 339),
                new Vec2(720, 339)
        };
        Vec2[] stageOffsets = {
                new Vec2(255f, -179.5f), // Stage 1 : upper right
                new Vec2(-270f, -179.5f), // Stage 2 : upper left
                new Vec2(270f, 166.5f), // Stage 3 : lower right
                new Vec2(-255f, 166.5f), // Stage 4 : lower left
        };

        for (int stageIndex = 0; stageIndex < STAGE_COUNT; stageIndex++) {
            if (stageIndex >= PlayerData.unlockedStages.length || !PlayerData.unlockedStages[stageIndex]) {
                continue;
            }

            final int launchIndex = stageIndex;
            Vec2 stageOffset = stageOffsets[stageIndex];
            Texture stageTexture = buttonTexture;
            if (stageIndex < stageButtonPaths.length) {
                stageTexture = new Texture(stageButtonPaths[stageIndex]);
                stageButtonTextures[stageIndex] = stageTexture;
            }
            Button stageButton = new UIButton(
                    super.layout.center(stageOffset.x, stageOffset.y),
                    stageIndex < stageButtonSizes.length ? stageButtonSizes[stageIndex] : new Vec2(720, 339),
                    "",
                    stageTexture);

            stageButton.setOnClick(() -> launchStage(launchIndex));
            stageButtons[stageIndex] = stageButton;
            super.uiManager.add(stageButton);
        }
    }

    private void launchStage(int stageIndex) {
        switch (stageIndex) {
            case 0:
                Engine.setScene(new game.overrun.stage.Stage1());
                break;
            case 1:
                Engine.setScene(new game.overrun.stage.Stage2());
                break;
            case 2:
                Engine.setScene(new game.overrun.stage.Stage3());
                break;
            case 3:
                Engine.setScene(new game.overrun.stage.Stage4());
                break;
            default:
                Engine.setScene(new game.overrun.stage.Stage1());
        }
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void renderWorld(float delta) {
        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        if (unlockedProgressBackground != null) {
            super.batch.setColor(Color.WHITE);
            super.batch.draw(unlockedProgressBackground, 0, 0, Engine.width, Engine.height);
        }
    }

    @Override
    public void renderUI(float delta) {
        // Render buttons
        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        font.cleanup();
        if (buttonTexture != null)
            buttonTexture.cleanup();
        if (stageButtonTextures != null) {
            for (Texture stageButtonTexture : stageButtonTextures) {
                if (stageButtonTexture != null) {
                    stageButtonTexture.cleanup();
                }
            }
        }
        if (unlockedProgressBackground != null)
            unlockedProgressBackground.cleanup();
    }
}
