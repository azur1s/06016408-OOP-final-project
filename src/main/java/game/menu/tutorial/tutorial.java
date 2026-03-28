package game.menu.tutorial;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;

import java.util.ArrayList;
import java.util.List;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.menu.components.UIButton;

public class tutorial extends Scene {

    private static class TextBlock {
        final String text;
        final float size;
        final Color color;
        final boolean centered;
        final boolean spacingBefore;

        TextBlock(String text, float size, Color color, boolean centered, boolean spacingBefore) {
            this.text = text;
            this.size = size;
            this.color = color;
            this.centered = centered;
            this.spacingBefore = spacingBefore;
        }
    }

    private FontAtlas font;
    private Texture sceneBackgroundTexture;
    private Texture backgroundTexture;
    private Texture readyButtonTexture;
    private Texture solidTexture;
    private UIButton nextButton;

    private final ArrayList<TextBlock> contentBlocks = new ArrayList<>();
    private final ArrayList<TextBlock> wrappedBlocks = new ArrayList<>();

    private float wrappedWidth = -1.0f;
    private float scrollProgress = 0.0f;
    private float contentHeight = 0.0f;
    private boolean draggingScrollbar = false;
    private float scrollbarDragOffset = 0.0f;

    @Override
    public void preloadAssets() {
        super.preloadAssets();
        Texture.preloadAsync(
                "textures/bg.png",
                "textures/tutorial/btn_ready.png",
                "textures/tutorial/tut_page.png",
                "textures/solid.png");
    }

    @Override
    public void init(int width, int height) {
        font = new FontAtlas("GeistMono-Regular.otf", 32);
        sceneBackgroundTexture = new Texture("textures/bg.png");
        readyButtonTexture = new Texture("textures/tutorial/btn_ready.png");
        backgroundTexture = new Texture("textures/tutorial/tut_page.png");
        solidTexture = new Texture("textures/solid.png");

        nextButton = new UIButton(
                super.layout.bottomCenter(0, 80),
                new Vec2(256 * 0.65f, 92 * 0.65f),
                "",
                readyButtonTexture);

        super.uiManager.add(nextButton);

        nextButton.setOnClick(() -> {
            PlayerData.hasCompletedTutorial = true;
            PlayerDataSaver.save();
            Engine.setScene(new game.menu.mode.Mode());
        });

        buildContent();
    }

    @Override
    public void update(float delta) {
        handleMouseWheelScroll();

        if (Engine.input.isKeyPressed(GLFW_KEY_DOWN)) {
            scrollProgress = clamp(scrollProgress + 0.10f, 0.0f, 1.0f);
        }
        if (Engine.input.isKeyPressed(GLFW_KEY_UP)) {
            scrollProgress = clamp(scrollProgress - 0.10f, 0.0f, 1.0f);
        }

        handleScrollbar(getContentViewportRect(), getScrollbarTrackRect());
    }

    @Override
    public void renderUI(float delta) {
        Vec2 sceneBackgroundPos = super.layout.center(0, 0);
        Vec2 sceneBackgroundSize = new Vec2(super.layout.res.x, super.layout.res.y);
        Vec2 backgroundPos = super.layout.center(0, 0);
        float notebookSize = Math.min(super.layout.res.x, super.layout.res.y) * 1.06f;
        Vec2 backgroundSize = new Vec2(notebookSize, notebookSize);

        super.batch.setColor(Color.WHITE);
        super.batch.draw(sceneBackgroundTexture, sceneBackgroundPos.x, sceneBackgroundPos.y,
                sceneBackgroundSize.x, sceneBackgroundSize.y);
        super.batch.draw(backgroundTexture, backgroundPos.x, backgroundPos.y, backgroundSize.x, backgroundSize.y);

        Vec2 titlePos = super.layout.topCenter(0, 120);
        drawOutlinedCenteredText("Welcome To Tutorial", titlePos.x, titlePos.y, 34.0f,
                Color.WHITE, new Color(0.18f, 0.10f, 0.05f, 1.0f), 2.0f);
        font.drawTextAligned(batch, "Drag the scrollbar to read the full guide", titlePos.x, titlePos.y - 42,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), 16);

        Vec2[] viewportRect = getContentViewportRect();
        Vec2 viewportPos = viewportRect[0];
        Vec2 viewportSize = viewportRect[1];
        Vec2[] trackRect = getScrollbarTrackRect();
        Vec2 trackPos = trackRect[0];
        Vec2 trackSize = trackRect[1];

        rebuildWrappedContent(viewportSize.x - 12.0f);

        super.batch.setColor(new Color(0.20f, 0.15f, 0.10f, 0.16f));
        super.batch.draw(solidTexture, viewportPos.x, viewportPos.y, viewportSize.x, viewportSize.y);

        super.batch.setColor(new Color(0.35f, 0.26f, 0.18f, 0.30f));
        super.batch.draw(solidTexture, trackPos.x, trackPos.y, trackSize.x, trackSize.y);

        renderScrollableText(viewportPos, viewportSize);
        renderScrollbarThumb(trackPos, trackSize, viewportSize.y);

        super.batch.setColor(Color.WHITE);
        super.uiManager.render(super.batch, font, mouseScreen);
    }

    @Override
    public void resize(int width, int height) {
        wrappedWidth = -1.0f;
    }

    @Override
    public void cleanup() {
        font.cleanup();
        sceneBackgroundTexture.cleanup();
        backgroundTexture.cleanup();
        readyButtonTexture.cleanup();
        solidTexture.cleanup();
    }

    private void buildContent() {
        contentBlocks.clear();

        contentBlocks.add(new TextBlock(
                "Typing Java is a typing-based defense game where players must type words or Java code correctly and quickly to defeat monsters approaching their base.",
                21.0f, Color.WHITE, false, false));
        contentBlocks.add(new TextBlock(
                "The main objective is to stop monsters from reaching the player or the base by combining fast typing with smart item usage.",
                21.0f, Color.WHITE, false, false));

        contentBlocks.add(new TextBlock("Step-by-Step Gameplay Tutorial", 25.0f, Color.WHITE, true, true));

        contentBlocks.add(new TextBlock("1. Select Game Mode", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "At the start of the game, players must choose a game mode. There are two main modes available.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Stage Mode", 20.0f, Color.WHITE, false, false));
        contentBlocks.add(new TextBlock(
                "Progress through levels one by one. Each stage becomes more challenging, with stronger and more numerous monsters appearing over time.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Overrun Mode", 20.0f, Color.WHITE, false, false));
        contentBlocks.add(new TextBlock(
                "This is an endless mode where monsters continuously spawn and the difficulty increases gradually. The run ends immediately if a monster reaches the player or the base.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));

        contentBlocks.add(new TextBlock("2. Select Items Before Starting", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "Before entering the game, players can select items to assist them during gameplay.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Players start with 1 item slot available.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Additional item slots can be unlocked, up to a maximum of 2 slots, using in-game coins.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Choosing the right items is important for surviving more difficult stages.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));

        contentBlocks.add(new TextBlock("3. Start the Game", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "Once the game begins, monsters will appear and move toward the player through different lanes.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Each monster displays a word or Java code on the screen.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Players must type the displayed text accurately and quickly to eliminate the monster before it reaches the player or base.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Faster and more accurate typing improves survival and scoring performance.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));

        contentBlocks.add(new TextBlock("4. Use Items Strategically", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "When facing difficult situations, such as large numbers of monsters or high difficulty levels, players can use items to gain an advantage.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Fireball: Instantly eliminates one or more monsters.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Freeze: Temporarily stops monsters from moving.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Wind: Pushes monsters backward, increasing the distance between them and the player.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Turret: Automatically attacks monsters for a short period of time.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Shield: Restores health and grants temporary invincibility to the player.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Strategic timing and proper item usage are essential for surviving challenging situations.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));

        contentBlocks.add(new TextBlock("5. Earn Rewards", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "Players receive rewards after defeating monsters or completing stages.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Score: Measures overall performance.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Coins: Used for upgrades and unlocks.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Combo Bonus: Earned by typing correctly multiple times in succession.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Better performance results in higher rewards.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));

        contentBlocks.add(new TextBlock("6. Upgrade and Progress", 23.0f, Color.WHITE, false, true));
        contentBlocks.add(new TextBlock(
                "Coins earned during gameplay can be used to improve the player's abilities and equipment.",
                20.0f, new Color(0.97f, 0.94f, 0.86f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Upgrade the base's health (HP).", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Improve item effectiveness.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Unlock additional item slots.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock("Purchase character skins.", 19.0f,
                new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
        contentBlocks.add(new TextBlock(
                "Upgrading systems allows players to handle stronger enemies and progress further in the game.",
                19.0f, new Color(0.92f, 0.88f, 0.77f, 1.0f), false, false));
    }

    private void rebuildWrappedContent(float maxWidth) {
        if (Math.abs(maxWidth - wrappedWidth) < 1.0f) {
            return;
        }

        wrappedWidth = maxWidth;
        wrappedBlocks.clear();
        contentHeight = 0.0f;

        for (TextBlock block : contentBlocks) {
            if (block.spacingBefore) {
                contentHeight += 16.0f;
            }

            List<String> wrappedLines = wrapText(block.text, maxWidth, block.size);
            boolean firstLine = true;
            for (String line : wrappedLines) {
                wrappedBlocks.add(new TextBlock(line, block.size, block.color, block.centered, block.spacingBefore && firstLine));
                contentHeight += getLineHeight(block.size);
                firstLine = false;
            }
        }
    }

    private void renderScrollableText(Vec2 viewportPos, Vec2 viewportSize) {
        float hiddenHeight = Math.max(0.0f, contentHeight - viewportSize.y);
        float scrollOffset = hiddenHeight * scrollProgress;
        float currentY = viewportPos.y + viewportSize.y / 2.0f - 6.0f + scrollOffset;

        super.batch.flush();
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) (viewportPos.x - viewportSize.x / 2.0f), (int) (viewportPos.y - viewportSize.y / 2.0f),
                (int) viewportSize.x, (int) viewportSize.y);

        for (TextBlock block : wrappedBlocks) {
            if (block.spacingBefore) {
                currentY -= 16.0f;
            }
            currentY -= getLineHeight(block.size);

            if (currentY < viewportPos.y - viewportSize.y / 2.0f - 30.0f) {
                continue;
            }
            if (currentY > viewportPos.y + viewportSize.y / 2.0f + 40.0f) {
                continue;
            }

            if (block.centered) {
                font.drawTextHorizontalAligned(super.batch, block.text, viewportPos.x, currentY, block.color, block.size);
            } else {
                float leftX = viewportPos.x - viewportSize.x / 2.0f + 10.0f;
                font.drawTextUnaligned(super.batch, block.text, leftX, currentY, block.color, block.size);
            }
        }

        super.batch.flush();
        glDisable(GL_SCISSOR_TEST);
    }

    private void renderScrollbarThumb(Vec2 trackPos, Vec2 trackSize, float viewportHeight) {
        float thumbHeight = getScrollbarThumbHeight(trackSize.y, viewportHeight);
        float thumbTravel = Math.max(0.0f, trackSize.y - thumbHeight);
        float thumbCenterY = trackPos.y + thumbTravel / 2.0f - (thumbTravel * scrollProgress);

        super.batch.setColor(new Color(0.86f, 0.72f, 0.48f, 0.95f));
        super.batch.draw(solidTexture, trackPos.x, thumbCenterY, trackSize.x - 6.0f, thumbHeight);
    }

    private void handleMouseWheelScroll() {
        float scrollY = Engine.input.getMouseScrollY();
        if (scrollY == 0.0f) {
            return;
        }

        Vec2[] viewportRect = getContentViewportRect();
        Vec2[] trackRect = getScrollbarTrackRect();
        if (!isInsideRect(mouseScreen, viewportRect[0], viewportRect[1])
                && !isInsideRect(mouseScreen, trackRect[0], trackRect[1])) {
            return;
        }

        scrollProgress = clamp(scrollProgress - (scrollY * 0.08f), 0.0f, 1.0f);
    }

    private void drawOutlinedCenteredText(String text, float x, float y, float size,
            Color fillColor, Color outlineColor, float offset) {
        font.drawTextAligned(batch, text, x - offset, y, outlineColor, size);
        font.drawTextAligned(batch, text, x + offset, y, outlineColor, size);
        font.drawTextAligned(batch, text, x, y - offset, outlineColor, size);
        font.drawTextAligned(batch, text, x, y + offset, outlineColor, size);
        font.drawTextAligned(batch, text, x, y, fillColor, size);
    }

    private void handleScrollbar(Vec2[] viewportRect, Vec2[] trackRect) {
        Vec2 viewportPos = viewportRect[0];
        Vec2 viewportSize = viewportRect[1];
        rebuildWrappedContent(viewportSize.x - 12.0f);

        Vec2 trackPos = trackRect[0];
        Vec2 trackSize = trackRect[1];
        float thumbHeight = getScrollbarThumbHeight(trackSize.y, viewportSize.y);
        float thumbTravel = Math.max(0.0f, trackSize.y - thumbHeight);

        Vec2 thumbSize = new Vec2(trackSize.x, thumbHeight);
        float thumbCenterY = trackPos.y + thumbTravel / 2.0f - (thumbTravel * scrollProgress);
        Vec2 thumbPos = new Vec2(trackPos.x, thumbCenterY);

        boolean mousePressed = Engine.input.isMouseButtonPressed(GLFW_MOUSE_BUTTON_LEFT);
        boolean mouseReleased = Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT);

        if (mouseReleased) {
            draggingScrollbar = false;
        }

        if (contentHeight <= viewportSize.y) {
            scrollProgress = 0.0f;
            draggingScrollbar = false;
            return;
        }

        if (mousePressed && !draggingScrollbar && isInsideRect(mouseScreen, thumbPos, thumbSize)) {
            draggingScrollbar = true;
            scrollbarDragOffset = mouseScreen.y - thumbCenterY;
        } else if (mousePressed && !draggingScrollbar && isInsideRect(mouseScreen, trackPos, trackSize)) {
            updateScrollFromThumbCenter(trackPos, trackSize, thumbHeight, mouseScreen.y);
        }

        if (draggingScrollbar && mousePressed) {
            updateScrollFromThumbCenter(trackPos, trackSize, thumbHeight, mouseScreen.y - scrollbarDragOffset);
        }
    }

    private void updateScrollFromThumbCenter(Vec2 trackPos, Vec2 trackSize, float thumbHeight, float thumbCenterY) {
        float minCenter = trackPos.y - trackSize.y / 2.0f + thumbHeight / 2.0f;
        float maxCenter = trackPos.y + trackSize.y / 2.0f - thumbHeight / 2.0f;
        float clampedCenter = clamp(thumbCenterY, minCenter, maxCenter);
        float travel = Math.max(1.0f, maxCenter - minCenter);
        scrollProgress = 1.0f - ((clampedCenter - minCenter) / travel);
        scrollProgress = clamp(scrollProgress, 0.0f, 1.0f);
    }

    private float getScrollbarThumbHeight(float trackHeight, float viewportHeight) {
        if (contentHeight <= 0.0f) {
            return trackHeight;
        }
        float ratio = Math.min(1.0f, viewportHeight / contentHeight);
        return Math.max(48.0f, trackHeight * ratio);
    }

    private List<String> wrapText(String text, float maxWidth, float textSize) {
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.trim().split("\\s+");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String candidate = currentLine.length() == 0 ? word : currentLine + " " + word;
            if (currentLine.length() > 0 && font.getTextWidth(candidate, textSize) > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine = new StringBuilder(candidate);
            }
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private float getLineHeight(float textSize) {
        return textSize + 10.0f;
    }

    private Vec2[] getContentViewportRect() {
        float notebookSize = Math.min(super.layout.res.x, super.layout.res.y) * 1.06f;
        Vec2 position = super.layout.center(-22, 8);
        Vec2 size = new Vec2(notebookSize * 0.49f, notebookSize * 0.47f);
        return new Vec2[] { position, size };
    }

    private Vec2[] getScrollbarTrackRect() {
        Vec2[] viewportRect = getContentViewportRect();
        Vec2 viewportPos = viewportRect[0];
        Vec2 viewportSize = viewportRect[1];
        Vec2 position = new Vec2(viewportPos.x + viewportSize.x / 2.0f + 28.0f, viewportPos.y);
        Vec2 size = new Vec2(16.0f, viewportSize.y * 0.94f);
        return new Vec2[] { position, size };
    }

    private boolean isInsideRect(Vec2 point, Vec2 center, Vec2 size) {
        Vec2 topLeft = new Vec2(center.x - size.x / 2.0f, center.y - size.y / 2.0f);
        return Vec2.isPointInRect(point, topLeft, size);
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
