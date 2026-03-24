package game.menu.selectcharacter;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.glClearColor;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.menu.PlayerData;
import game.menu.PlayerDataSaver;
import game.menu.components.UIButton;

public class selectcharacter extends Scene {
    private FontAtlas font;
    private Texture solidTexture;
    private Texture buttonTexture;
    private Texture playerTexture;

    private UIButton[] characterButtons;
    private UIButton selectButton;
    private UIButton backButton;

    private final String[] characterNames = { "Character 1", "Character 2", "Character 3" };
    private final Color[] accentColors = {
            new Color(0.93f, 0.57f, 0.41f, 1.0f),
            new Color(0.41f, 0.69f, 0.92f, 1.0f),
            new Color(0.53f, 0.77f, 0.49f, 1.0f)
    };
    private int hoveredCharacter = -1;
    private int pendingSelection;

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        solidTexture = new Texture("textures/solid.png");
        buttonTexture = new Texture("textures/button_test.png");
        playerTexture = new Texture("textures/player.png");

        pendingSelection = PlayerData.selectedCharacter;

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

            button.setOnClick(() -> pendingSelection = index);
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
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.96f, 0.96f, 0.96f, 1.0f);

        Vec2 titlePos = super.layout.topCenter(0, 100);
        font.drawTextAligned(super.batch, "Your Character", titlePos.x, titlePos.y, Color.BLACK, 72);

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
        if (playerTexture != null)
            playerTexture.cleanup();
    }

    private void drawCharacterCard(int index, float cardX, float cardY) {
        boolean isSelected = pendingSelection == index;
        boolean isHovered = hoveredCharacter == index;

        Color panelColor = isSelected
                ? new Color(0.91f, 0.93f, 0.98f, 1.0f)
                : new Color(1f, 1f, 1f, isHovered ? 0.95f : 0.90f);
        Color outlineColor = isSelected
                ? accentColors[index]
                : new Color(0.86f, 0.86f, 0.86f, 1.0f);
        Color shadowColor = new Color(0f, 0f, 0f, 0.08f);
        Color textColor = isSelected ? accentColors[index] : new Color(0.75f, 0.75f, 0.75f, 1.0f);

        super.batch.setColor(shadowColor);
        super.batch.draw(solidTexture, cardX, cardY - 8f, 228f, 322f);

        super.batch.setColor(panelColor);
        super.batch.draw(solidTexture, cardX, cardY, 220f, 310f);

        super.batch.setColor(outlineColor);
        super.batch.draw(solidTexture, cardX, cardY + 155f, 220f, 6f);
        super.batch.draw(solidTexture, cardX, cardY - 155f, 220f, 6f);
        super.batch.draw(solidTexture, cardX - 107f, cardY, 6f, 310f);
        super.batch.draw(solidTexture, cardX + 107f, cardY, 6f, 310f);

        font.drawTextHorizontalAligned(super.batch, "(pic character)", cardX, cardY + 65f, textColor, 30);

        super.batch.setColor(new Color(0.85f, 0.85f, 0.85f, 1.0f));
        super.batch.draw(solidTexture, cardX, cardY - 10f, 130f, 44f);

        super.batch.setColor(accentColors[index]);
        super.batch.draw(playerTexture, cardX, cardY + 18f, 105f, 130f);

        font.drawTextHorizontalAligned(super.batch, characterNames[index], cardX, cardY - 115f, Color.BLACK, 24);

        if (isSelected) {
            font.drawTextHorizontalAligned(super.batch, "Selected", cardX, cardY - 145f, accentColors[index], 22);
        }

        super.batch.setColor(Color.WHITE);
    }
}
