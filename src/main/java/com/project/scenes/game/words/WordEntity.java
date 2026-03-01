package com.project.scenes.game.words;

import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;

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

    public void render(TextureBatch batch, FontAtlas font) {
        batch.setColor(Color.WHITE);
        batch.draw(texture, position.x, position.y - 8f, 64f, 64f);

        Color previewColor = new Color(0.75f, 0.75f, 0.75f, 1f);
        Color typedColor = Color.WHITE;

        // Shift the text up so it's above the texture
        Vec2 pos = position.add(new Vec2(0f, 30f));

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
                font.drawTextHorizontalAligned(batch,
                        word.substring(0, visibleCount) + hiddenPart,
                        pos.x, pos.y, previewColor, 16);
            } else {
                font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, 16);
            }

        } else if (effect instanceof WordEffect.Repeat repeatEffect) {
            // Display the word with a multiplier based on how many times it needs to be
            // repeated, e.g. if count is 3, display "example³"
            Vec2 wordSize = font.measure(word, 16);
            font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, 16);

            // Draw the multiplier in the top right corner of the word
            String multiplier = "x" + (1 + repeatEffect.count() - repeatEffect.typedCount());

            font.drawTextHorizontalAligned(batch, multiplier, pos.x + (wordSize.x / 2f) + 12f, pos.y + wordSize.y / 2f,
                    typedColor, 12);
        }

        // Normal variant
        else {
            font.drawTextHorizontalAligned(batch, word, pos.x, pos.y, previewColor, 16);
        }

        // Draw the typed part of the word on top, shifted to the left so it overlaps
        // with the preview text
        if (progress > 0) {
            float typedWidth = font.getTextWidth(word.substring(0, progress), 16);
            font.drawTextHorizontalAligned(batch, word.substring(0, progress),
                    pos.x - (font.getTextWidth(word, 16) / 2f) + (typedWidth / 2f), pos.y, typedColor, 16);
        }
    }

    public void setEffect(WordEffect effect) {
        this.effect = effect;
    }
}
