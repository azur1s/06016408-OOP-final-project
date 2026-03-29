package game.menu.selectCharacter;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.glClearColor;

import engine.Engine;
import engine.Scene;
import engine.graphics.AnimationClip;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.PlayerCharacterAssets;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.menu.components.UIButton;

public class selectCharacter extends Scene {
    private static final int[] DISPLAY_ORDER = { 1, 0, 2 };

    private FontAtlas font;
    private Texture solidTexture;
    private Texture buttonTexture;
    private Texture backgroundTexture;
    private Texture selectedCharacterTexture;
    private Texture backTexture;
    private AnimationClip[] characterAnimations;

    private UIButton[] characterButtons;
    private UIButton selectButton;
    private UIButton backButton;

    private final String[] characterNames = { "Ruby", "Sapphire", "Emerald" };
    private final Color[] accentColors = {
            new Color(0.96f, 0.55f, 0.26f, 1.0f),
            new Color(0.27f, 0.70f, 0.95f, 1.0f),
            new Color(0.36f, 0.82f, 0.46f, 1.0f)
    };
    private final Color[] panelColors = {
            new Color(1.00f, 0.95f, 0.90f, 1.0f),
            new Color(0.91f, 0.96f, 1.00f, 1.0f),
            new Color(0.91f, 0.99f, 0.92f, 1.0f)
    };
    private float[] cardScaleAnimations;
    private float[] cardLiftAnimations;
    private float[] clickPulseAnimations;
    private int hoveredCharacter = -1;
    private int pendingSelection;

    @Override
    public void preloadAssets() {
        super.preloadAssets();
        Texture.preloadAsync(
                "textures/solid.png",
                "textures/bg.png",
                "textures/selectCharacter/btn_select.png",
                "textures/btn_back.png");
        Texture.preloadAsync(PlayerCharacterAssets.getAllAnimationFramePaths());
    }

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        solidTexture = new Texture("textures/solid.png");
        backgroundTexture = new Texture("textures/bg.png");
        selectedCharacterTexture = new Texture("textures/selectCharacter/btn_select.png");
        backTexture = new Texture("textures/btn_back.png");
        characterAnimations = new AnimationClip[PlayerCharacterAssets.CHARACTER_COUNT];
        for (int i = 0; i < characterAnimations.length; i++) {
            characterAnimations[i] = PlayerCharacterAssets.createAnimationClip(i);
        }

        pendingSelection = PlayerData.selectedCharacter == -1
                ? DISPLAY_ORDER[1]
                : PlayerCharacterAssets.sanitizeCharacterIndex(PlayerData.selectedCharacter);
        cardScaleAnimations = new float[3];
        cardLiftAnimations = new float[3];
        clickPulseAnimations = new float[3];

        characterButtons = new UIButton[DISPLAY_ORDER.length];
        float[] xOffsets = { 280f, 0f, -280f };
        for (int i = 0; i < characterButtons.length; i++) {
            final int slotIndex = i;
            final int characterIndex = DISPLAY_ORDER[i];
            UIButton button = new UIButton(
                    super.layout.center(xOffsets[i], -10f),
                    new Vec2(256, 300),
                    "",
                    new Color(1f, 1f, 1f, 0.02f),
                    new Color(1f, 1f, 1f, 0.06f),
                    solidTexture);

            button.setOnClick(() -> {
                pendingSelection = characterIndex;
                clickPulseAnimations[slotIndex] = 1.0f;
            });
            button.setOnEnter(() -> hoveredCharacter = slotIndex);
            button.setOnLeave(() -> {
                if (hoveredCharacter == slotIndex) {
                    hoveredCharacter = -1;
                }
            });

            characterButtons[i] = button;
            super.uiManager.add(button);
        }

        selectButton = new UIButton(
                super.layout.bottomCenter(0, 90),
                new Vec2(256 * 0.8f, 92 * 0.8f),
                "",
                selectedCharacterTexture);
        selectButton.setOnClick(() -> {
            PlayerData.selectedCharacter = pendingSelection;
            PlayerDataSaver.save();
            Engine.setScene(new game.menu.tutorial.tutorial());
        });
        super.uiManager.add(selectButton);

        backButton = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(256 * 0.6f, 92 * 0.6f),
                "",
                backTexture);
        backButton.setOnClick(() -> Engine.setScene(new game.menu.Main())); // has a bug if you click back when it's
                                                                            // back to main menu, it has 2 time sound
                                                                            // effect but it must be 1 time
        super.uiManager.add(backButton);
    }

    @Override
    public void update(float delta) {
        for (UIButton button : characterButtons) {
            button.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        }
        selectButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        backButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        for (int i = 0; i < characterButtons.length; i++) {
            boolean isHovered = characterButtons[i].isHovered(mouseScreen);
            boolean isSelected = pendingSelection == DISPLAY_ORDER[i];
            float targetScale = isSelected ? 1.06f : isHovered ? 1.03f : 1.0f;
            float targetLift = isSelected ? 18f : isHovered ? 10f : 0f;

            cardScaleAnimations[i] = approach(cardScaleAnimations[i], targetScale, delta * 8f);
            cardLiftAnimations[i] = approach(cardLiftAnimations[i], targetLift, delta * 180f);
            clickPulseAnimations[i] = Math.max(0f, clickPulseAnimations[i] - delta * 4.5f);
        }
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.96f, 0.96f, 0.96f, 1.0f);

        Vec2 backgroundPos = super.layout.center(0, 0);
        Vec2 backgroundSize = new Vec2(super.layout.res.x, super.layout.res.y);
        super.batch.draw(backgroundTexture, backgroundPos.x, backgroundPos.y, backgroundSize.x, backgroundSize.y);

        Vec2 titlePos = super.layout.topCenter(0, 100);
        font.drawTextAligned(super.batch, "Select Your Character", titlePos.x, titlePos.y, Color.WHITE, 72);

        float[] xPositions = {
                super.layout.center(280f, -10f).x,
                super.layout.center(0f, -10f).x,
                super.layout.center(-280f, -10f).x
        };
        float cardY = super.layout.center(0f, -10f).y;

        for (int i = 0; i < characterButtons.length; i++) {
            drawCharacterCard(i, xPositions[i], cardY);
        }

        selectButton.render(super.batch, font, mouseScreen);
        backButton.render(super.batch, font, mouseScreen);
    }

    @Override
    public void cleanup() {
        if (font != null)
            font.cleanup();
        if (solidTexture != null)
            solidTexture.cleanup();
        if (buttonTexture != null)
            buttonTexture.cleanup();
        if (backgroundTexture != null)
            backgroundTexture.cleanup();
        if (selectedCharacterTexture != null)
            selectedCharacterTexture.cleanup();
        if (backTexture != null)
            backTexture.cleanup();
        if (characterAnimations != null) {
            for (AnimationClip characterAnimation : characterAnimations) {
                PlayerCharacterAssets.cleanup(characterAnimation);
            }
        }
    }

    private void drawCharacterCard(int index, float cardX, float cardY) {
        int characterIndex = DISPLAY_ORDER[index];
        int frameStyleIndex = index;
        boolean isSelected = pendingSelection == characterIndex;
        boolean isHovered = hoveredCharacter == index;
        float pulse = clickPulseAnimations[index];
        float pulseBoost = (float) Math.sin(pulse * Math.PI) * 0.035f;
        float scale = cardScaleAnimations[index] + pulseBoost;
        float lift = cardLiftAnimations[index] + pulse * 12f;

        float panelWidth = 220f * scale;
        float panelHeight = 310f * scale;
        float shadowWidth = 228f * scale;
        float shadowHeight = 322f * scale;
        float borderThickness = Math.max(6f, 6f * scale);
        float imageWidth = 105f * scale;
        float imageHeight = 130f * scale;
        float podiumWidth = 130f * scale;
        float podiumHeight = 44f * scale;
        float drawY = cardY + lift;

        Color panelColor = isSelected
                ? panelColors[frameStyleIndex]
                : new Color(1f, 1f, 1f, isHovered ? 0.96f : 0.91f);
        Color outlineColor = isSelected
                ? accentColors[frameStyleIndex]
                : new Color(0.86f, 0.86f, 0.86f, 1.0f);
        Color shadowColor = new Color(0f, 0f, 0f, 0.10f + pulse * 0.04f);

        super.batch.setColor(shadowColor);
        super.batch.draw(solidTexture, cardX, drawY - 12f, shadowWidth, shadowHeight);

        super.batch.setColor(panelColor);
        super.batch.draw(solidTexture, cardX, drawY, panelWidth, panelHeight);

        super.batch.setColor(outlineColor);
        super.batch.draw(solidTexture, cardX, drawY + panelHeight * 0.5f, panelWidth, borderThickness);
        super.batch.draw(solidTexture, cardX, drawY - panelHeight * 0.5f, panelWidth, borderThickness);
        super.batch.draw(solidTexture, cardX - panelWidth * 0.5f + borderThickness * 0.5f, drawY, borderThickness,
                panelHeight);
        super.batch.draw(solidTexture, cardX + panelWidth * 0.5f - borderThickness * 0.5f, drawY, borderThickness,
                panelHeight);

        super.batch.setColor(new Color(
                Math.max(0f, accentColors[frameStyleIndex].r - 0.10f),
                Math.max(0f, accentColors[frameStyleIndex].g - 0.10f),
                Math.max(0f, accentColors[frameStyleIndex].b - 0.10f),
                0.28f + pulse * 0.10f));
        super.batch.draw(solidTexture, cardX, drawY - 10f * scale, podiumWidth, podiumHeight);

        super.batch.setColor(Color.WHITE);
        super.batch.setFlipped(true);
        super.batch.drawAnimation(characterAnimations[characterIndex],
                new Vec2(cardX, drawY + 18f * scale),
                imageWidth,
                imageHeight);
        super.batch.setFlipped(false);

        font.drawTextHorizontalAligned(super.batch, characterNames[characterIndex], cardX, drawY - 115f * scale, Color.BLACK,
                24f * scale);

        if (isSelected) {
            font.drawTextHorizontalAligned(super.batch, "Selected", cardX, drawY - 145f * scale, accentColors[frameStyleIndex],
                    22f * scale);
        }

        super.batch.setColor(Color.WHITE);
    }

    private float approach(float current, float target, float amount) {
        if (current < target) {
            return Math.min(current + amount, target);
        }
        return Math.max(current - amount, target);
    }
}
