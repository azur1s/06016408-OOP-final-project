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
    private Texture unlockedProgressBackground;
    private Button backButton;
    private Button[] stageButtons;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        buttonTexture = new Texture(StageConfigs.getButtonTexturePath());
        unlockedProgressBackground = new Texture(getUnlockedProgressBackgroundPath());

        // Create back button
        backButton = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                buttonTexture);

        backButton.setOnClick(() -> {
            Engine.setScene(new game.menu.mode.Mode());
        });

        createStageButtons();

        super.uiManager.add(backButton);
    }

    private String getUnlockedProgressBackgroundPath() {
        int unlockedCount = 0;
        int total = Math.min(STAGE_COUNT, PlayerData.unlockedStages.length);
        for (int i = 0; i < total; i++) {
            if (PlayerData.unlockedStages[i]) {
                unlockedCount++;
            }
        }

        return switch (unlockedCount) {
            case 4 -> "textures/background/stagelocked/All_unlocked.jpg";
            case 3 -> "textures/background/stagelocked/Locked1.jpg";
            case 2 -> "textures/background/stagelocked/Locked2.jpg";
            default -> "textures/background/stagelocked/Locked3.jpg";
        };
    }

    // TODO: Create custom button with texture and size of the texture background we
    // have
    private void createStageButtons() {
        stageButtons = new Button[STAGE_COUNT];
        Vec2 buttonSize = new Vec2(160, 70);
        Vec2[] stageOffsets = {
                new Vec2(220f, -80f), // Stage 1 : upper right
                new Vec2(-220f, -80f), // Stage 2 : upper left
                new Vec2(220f, 120f), // Stage 3 : lower right
                new Vec2(-220f, 120f), // Stage 4 : lower left
        };

        for (int stageIndex = 0; stageIndex < STAGE_COUNT; stageIndex++) {
            if (stageIndex >= PlayerData.unlockedStages.length || !PlayerData.unlockedStages[stageIndex]) {
                continue;
            }

            final int launchIndex = stageIndex;
            Vec2 stageOffset = stageOffsets[stageIndex];
            Button stageButton = new UIButton(
                    super.layout.center(stageOffset.x, stageOffset.y),
                    buttonSize,
                    "Stage " + (stageIndex + 1),
                    buttonTexture);

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
        if (unlockedProgressBackground != null)
            unlockedProgressBackground.cleanup();
    }
}
