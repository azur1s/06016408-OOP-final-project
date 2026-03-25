package engine.ui;

import java.util.ArrayList;

import engine.Engine;
import engine.graphics.FontAtlas;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

/**
 * Manages UI controls, forwarding input updates and rendering calls.
 */
public class UIManager {
    ArrayList<Button> buttons;
    ArrayList<Slider> sliders;

    /**
     * Creates an empty UI manager.
     */
    public UIManager() {
        buttons = new ArrayList<>();
        sliders = new ArrayList<>();
    }

    /**
     * Adds a button to the managed UI list.
     *
     * @param button button to add
     */
    public void add(Button button) {
        buttons.add(button);
    }

    /**
     * Adds a slider to the managed UI list.
     *
     * @param slider slider to add
     */
    public void add(Slider slider) {
        sliders.add(slider);
    }

    /**
     * Updates all managed controls from current input state.
     *
     * @param mousePos      current mouse position in UI coordinates
     * @param mouseReleased mouse button action for button update flow
     */
    public void update(Vec2 mousePos, boolean mouseReleased) {
        for (Button button : buttons) {
            button.update(mousePos, mouseReleased);
        }
        for (Slider slider : sliders) {
            slider.update(mousePos, Engine.input.isMouseButtonPressed(0));
        }
    }

    /**
     * Renders all managed controls.
     *
     * @param batch       texture batch used for drawing
     * @param font        font atlas used for text rendering
     * @param mouseScreen current mouse position for hover-aware rendering
     */
    public void render(TextureBatch batch, FontAtlas font, Vec2 mouseScreen) {
        for (Button button : buttons) {
            button.render(batch, font, mouseScreen);
        }
        for (Slider slider : sliders) {
            slider.render(batch, font, mouseScreen);
        }
    }

    /**
     * Cleans up resources owned by managed controls.
     */
    public void cleanup() {
        for (Button button : buttons) {
            button.cleanup();
        }
    }

    /**
     * Returns currently managed buttons.
     *
     * @return button list
     */
    public ArrayList<Button> getButtons() {
        return buttons;
    }
}
