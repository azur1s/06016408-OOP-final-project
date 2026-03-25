package game.menu.selectCharacter;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.glClearColor;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.menu.components.UIButton;

public class selectCharacter extends Scene {
    private FontAtlas font;
    private Texture solidTexture;
    private Texture buttonTexture;
    private Texture[] characterTextures;

    private UIButton[] characterButtons;
    private UIButton selectButton;
    private UIButton backButton;

    private final String[] characterNames = { "Character 1", "Character 2", "Character 3" };
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
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        solidTexture = new Texture("textures/solid.png");
        buttonTexture = new Texture("textures/button_test.png");
        characterTextures = new Texture[] {
                new Texture("textures/player_1.png"),
                new Texture("textures/player_0.png"),
                new Texture("textures/player_3.png")
        };

        pendingSelection = PlayerData.selectedCharacter;
        cardScaleAnimations = new float[3];
        cardLiftAnimations = new float[3];
        clickPulseAnimations = new float[3];

        characterButtons = new UIButton[3];
        float[] xOffsets = { 280f, 0f, -280f };
        for (int i = 0; i < characterButtons.length; i++) {
            final int index = i;
            UIButton button = new UIButton(
                    super.layout.center(xOffsets[i], -10f),
                    new Vec2(200, 300),
                    "",
                    new Color(1f, 1f, 1f, 0.02f),
                    new Color(1f, 1f, 1f, 0.06f),
                    solidTexture);

            button.setOnClick(() -> {
                pendingSelection = index;
                clickPulseAnimations[index] = 1.0f;
            });
            button.setOnEnter(() -> hoveredCharacter = index);
            button.setOnLeave(() -> {
                if (hoveredCharacter == index) {
                    hoveredCharacter = -1;
                }
            });

            characterButtons[i] = button;
            super.uiManager.add(button);
        }

        selectButton = new UIButton(
                super.layout.bottomCenter(0, 90),
                new Vec2(260, 70),
                "Select",
                buttonTexture);
        selectButton.setOnClick(() -> {
            PlayerData.selectedCharacter = pendingSelection;
            PlayerDataSaver.save();
            Engine.setScene(new game.menu.mode.Mode());
        });
        super.uiManager.add(selectButton);

        backButton = new UIButton(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Back",
                buttonTexture);
        backButton.setOnClick(() -> Engine.setScene(new game.menu.mode.Mode()));
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
            boolean isSelected = pendingSelection == i;
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

        Vec2 titlePos = super.layout.topCenter(0, 100);
        font.drawTextAligned(super.batch, "Select Your Character", titlePos.x, titlePos.y, Color.BLACK, 72);

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
        if (characterTextures != null) {
            for (Texture characterTexture : characterTextures) {
                if (characterTexture != null) {
                    characterTexture.cleanup();
                }
            }
        }
    }

    private void drawCharacterCard(int index, float cardX, float cardY) {
        boolean isSelected = pendingSelection == index;
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
                ? panelColors[index]
                : new Color(1f, 1f, 1f, isHovered ? 0.96f : 0.91f);
        Color outlineColor = isSelected
                ? accentColors[index]
                : new Color(0.86f, 0.86f, 0.86f, 1.0f);
        Color shadowColor = new Color(0f, 0f, 0f, 0.10f + pulse * 0.04f);
        Color textColor = isSelected ? accentColors[index] : new Color(0.75f, 0.75f, 0.75f, 1.0f);

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

        font.drawTextHorizontalAligned(super.batch, "(pic character)", cardX, drawY + 65f * scale, textColor,
                30f * scale);

        super.batch.setColor(new Color(
                Math.max(0f, accentColors[index].r - 0.10f),
                Math.max(0f, accentColors[index].g - 0.10f),
                Math.max(0f, accentColors[index].b - 0.10f),
                0.28f + pulse * 0.10f));
        super.batch.draw(solidTexture, cardX, drawY - 10f * scale, podiumWidth, podiumHeight);

        super.batch.setColor(Color.WHITE);
        super.batch.draw(characterTextures[index], cardX, drawY + 18f * scale, imageWidth, imageHeight);

        font.drawTextHorizontalAligned(super.batch, characterNames[index], cardX, drawY - 115f * scale, Color.BLACK,
                24f * scale);

        if (isSelected) {
            font.drawTextHorizontalAligned(super.batch, "Selected", cardX, drawY - 145f * scale, accentColors[index],
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
