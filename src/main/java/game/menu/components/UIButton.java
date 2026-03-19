package game.menu.components;

import engine.Engine;
import engine.graphics.Color;
import engine.graphics.Texture;
import engine.math.Vec2;
import engine.ui.Button;

public class UIButton extends Button {
    public UIButton(Vec2 position, Vec2 size, String text, Texture bgTexture) {
        super(position, size, text,
                // Texture colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                // Text colors
                Color.WHITE,
                new Color(0.8f, 0.8f, 0.8f, 1.0f),
                bgTexture);

        Engine.audio.loadSound("click", "audio/click.ogg");
    }

    public UIButton(Vec2 position, Vec2 size, String text,
            Color color, Color hoverColor,
            Color textColor, Color textHoverColor,
            Texture bgTexture) {
        super(position, size, text, color, hoverColor, textColor, textHoverColor, bgTexture);

        Engine.audio.loadSound("click", "audio/click.ogg");
    }

    public UIButton(Vec2 position, Vec2 size, String text,
            Color color, Color hoverColor,
            Texture solidTexture) {
        super(position, size, text, color, hoverColor, solidTexture);

        Engine.audio.loadSound("click", "audio/click.ogg");
    }

    /**
     * Add a callback called when the button is clicked.
     */
    public void setOnClick(Runnable onClick) {
        super.setOnClick(() -> {
            Engine.audio.playSound("click");
            onClick.run();
        });
    }
}
