package com.project.game.words;

import com.project.engine.Color;
import com.project.engine.Fonts;
import com.project.engine.OrthoCamera;
import com.project.engine.Texture;
import com.project.engine.TextureBatch;
import com.project.math.Vec2;

public class WordEntity {
    public static final float LANE_HEIGHT = 100f;
    public static final float LANE_GAP = 20f;
    public static final float LANE_SPACING = LANE_HEIGHT + LANE_GAP;

    public Texture texture;

    public String word;
    public int progress = 0; // How many characters have been correctly typed
    public float speed;
    public WordEffect effect = new WordEffect.Normal();

    public Vec2 position;
    /**
     * The lane the word entity is in [0 - 4].
     */
    public int lane;

    public WordEntity(Texture texture, String word, float xPosition, float speed, int lane) {
        // lane 0 is top, lane 4 is bottom
        float yLane = (lane - 2) * LANE_SPACING;

        this.texture = texture;
        this.word = word;
        this.position = new Vec2(xPosition, yLane);
        this.speed = speed;
        this.lane = lane;
    }

    public void update(float delta) {
        position.x -= speed * delta;
    }

    public void renderTexture(OrthoCamera cam, TextureBatch batch) {
        batch.draw(texture, position.x, position.y - 8f, 64f, 64f);
    }

    public void renderText(OrthoCamera cam, Fonts fonts) {
        String font = "default";
        Color previewColor = new Color(0.75f, 0.75f, 0.75f, 1f);
        Color typedColor = Color.WHITE;

        // Shift the text up so it's above the texture
        Vec2 pos = position.add(new Vec2(0f, 40f));

        if (effect instanceof WordEffect.Hidden hiddenEffect) {

            // Hide last N characters based on the effect, and only show it when
            // progress reaches N
            // e.g. if word is "example" and count is 2, display "examp__"
            // and when user types "examp", display "exampl_" and so on
            int hiddenCount = hiddenEffect.count();
            int visibleCount = Math.max(0, word.length() - hiddenCount);

            String hiddenPart = "";
            // Randomly generate characters to hide the word
            for (int i = 0; i < hiddenCount; i++) {
                hiddenPart += (char) ('a' + (int) (Math.random() * 26));
            }

            if (progress < visibleCount) {
                fonts.setColor(previewColor);
                fonts.drawWorldCenter(cam, font,
                        word.substring(0, visibleCount) + hiddenPart,
                        pos.x, pos.y, 16);
            } else {
                fonts.setColor(previewColor);
                fonts.drawWorldCenter(cam, font, word, pos.x, pos.y, 16);
            }

        } else if (effect instanceof WordEffect.Repeat repeatEffect) {
            // Display the word with a multiplier based on how many times it needs to be
            // repeated, e.g. if count is 3, display "example³"
            fonts.setColor(previewColor);
            Vec2 wordSize = fonts.measure("default", word, 16);
            fonts.drawWorldCenter(cam, font, word, pos.x, pos.y, 16);

            // Draw the multiplier in the top right corner of the word
            String multiplier = "x" + (1 + repeatEffect.count() - repeatEffect.typedCount());

            fonts.setColor(typedColor);
            fonts.drawWorldCenter(cam, font, multiplier, pos.x + (wordSize.x / 2f) + 12f, pos.y + wordSize.y / 2f,
                    12);
        }

        // Normal variant
        else {
            fonts.setColor(previewColor);
            fonts.drawWorldCenter(cam, font, word, pos.x, pos.y, 16);
        }

        fonts.setColor(typedColor);
        fonts.drawWorldCenter(cam, font,
                // Since we use drawWorldCenter the text will be centered at the origin, so we
                // need to add spaces to shift the text to the right.
                word.substring(0, progress) + " ".repeat(Math.max(0, 1 + word.length() - progress)),
                pos.x, pos.y, 16);
    }

    public void setEffect(WordEffect effect) {
        this.effect = effect;
    }
}
