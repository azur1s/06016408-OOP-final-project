package engine.ui;

import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

/**
 * Clickable UI button with hover/click callbacks and text rendering.
 */
public class Button {
    private Vec2 position, size;
    private String text;
    private Color color;
    private Color hoverColor;
    private Color textColor;
    private Color textHoverColor;
    private Texture bgTexture;

    private Runnable onClick;
    private Runnable onHover;
    private Runnable onEnter;
    private Runnable onLeave;

    // Whether the button was hovered in the last frame, used for onEnter and
    // onLeave callbacks
    boolean lastHovered = false;

    /**
     * Creates a button with full visual customization.
     *
     * @param position       button center position
     * @param size           button dimensions
     * @param text           label text
     * @param color          background color in normal state
     * @param hoverColor     background color when hovered
     * @param textColor      text color in normal state
     * @param textHoverColor text color when hovered
     * @param bgTexture      texture used to draw the button body
     */
    public Button(Vec2 position, Vec2 size, String text,
            Color color, Color hoverColor,
            Color textColor, Color textHoverColor,
            Texture bgTexture) {
        this.position = position;
        this.size = size;
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.textColor = textColor;
        this.textHoverColor = textHoverColor;
        this.bgTexture = bgTexture;
    }

    /**
     * Creates a button with default background and text hover colors.
     *
     * @param position  button center position
     * @param size      button dimensions
     * @param text      label text
     * @param bgTexture texture used to draw the button body
     */
    public Button(Vec2 position, Vec2 size, String text, Texture bgTexture) {
        this(position, size, text,
                // Texture colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                // Text colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                bgTexture);
    }

    /**
     * Creates a button with custom background colors and default text colors.
     *
     * @param position   button center position
     * @param size       button dimensions
     * @param text       label text
     * @param color      background color in normal state
     * @param hoverColor background color when hovered
     * @param bgTexture  texture used to draw the button body
     */
    public Button(Vec2 position, Vec2 size, String text, Color color, Color hoverColor, Texture bgTexture) {
        this(position, size, text, color, hoverColor, Color.WHITE, new Color(0.8f, 0.8f, 0.8f, 1.0f), bgTexture);
    }

    /**
     * Set the text displayed on the button.
     *
     * @param text text to display
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Add a callback called when the button is clicked.
     *
     * @param onClick callback invoked on click
     */
    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    /**
     * Add a callback called when the button is hovered.
     *
     * @param onHover callback invoked while hovered
     */
    public void setOnHover(Runnable onHover) {
        this.onHover = onHover;
    }

    /**
     * Add a callback called when the mouse enters the button area.
     *
     * @param onEnter callback invoked on hover enter
     */
    public void setOnEnter(Runnable onEnter) {
        this.onEnter = onEnter;
    }

    /**
     * Add a callback called when the mouse leaves the button area.
     *
     * @param onLeave callback invoked on hover leave
     */
    public void setOnLeave(Runnable onLeave) {
        this.onLeave = onLeave;
    }

    public void update(Vec2 mousePos, boolean mousePressed) {

        /**
         * Updates hover and click state and dispatches callbacks.
         *
         * @param mousePos     current mouse position in UI coordinates
         * @param mousePressed whether the click action is pressed this frame
         */
        if (isHovered(mousePos)) {
            if (onHover != null) {
                onHover.run();
            }
        }

        if (isHovered(mousePos) && !lastHovered && onEnter != null) {
            onEnter.run();
            lastHovered = true;
        }
        if (!isHovered(mousePos) && lastHovered && onLeave != null) {
            onLeave.run();
            lastHovered = false;
        }

        if (isHovered(mousePos) && mousePressed && onClick != null) {
            onClick.run();
        }
    }

    /**
     * Renders this button and its text label.
     *
     * @param batch    texture batch used for drawing
     * @param font     font atlas used for text rendering
     * @param mousePos current mouse position used for hover coloring
     */
    public void render(TextureBatch batch, FontAtlas font, Vec2 mousePos) {
        boolean hovered = isHovered(mousePos);
        Color color = hovered ? hoverColor : this.color;
        Color textColor = hovered ? textHoverColor : this.textColor;

        batch.setColor(color);
        batch.draw(bgTexture, position.x, position.y, size.x, size.y);
        batch.setColor(Color.WHITE);

        font.drawTextAligned(batch, text, position.x, position.y, textColor, 16);
    }

    /**
     * Checks whether a point lies inside the button bounds.
     *
     * @param mousePos point to test
     * @return {@code true} when inside the button area
     */
    public boolean isHovered(Vec2 mousePos) {
        // Since the origin of the button is at the center, we need to shift the mouse
        // position by half the size of the button
        Vec2 shiftedMousePos = new Vec2(mousePos.x - position.x, mousePos.y - position.y);
        Vec2 topLeft = new Vec2(-size.x / 2f, -size.y / 2f);
        return Vec2.isPointInRect(shiftedMousePos, topLeft, size);
    }

    /**
     * Releases button-owned resources.
     */
    public void cleanup() {
        // bgTexture is managed and cleaned up by the Scene
    }
}
