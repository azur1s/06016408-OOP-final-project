package com.project.words;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class WordEntity {
    public static final float LANE_HEIGHT = 80f;
    public static final float LANE_GAP = 20f;
    public static final float LANE_SPACING = LANE_HEIGHT + LANE_GAP;

    public String word;
    public int progress = 0; // how many characters have been correctly typed
    public float speed;
    public WordEffect effect = new WordEffect.Normal();

    public Vector2 position;
    /**
     * The lane the word entity is in [0 - 4].
     */
    public int lane;

    private float tick = 0f;

    public WordEntity(String word, float xPosition, float speed, int lane) {
        float yCenter = 300;
        // lane 0 is top, lane 4 is bottom
        float yLane = (lane - 2) * LANE_SPACING;
        // add random y variation within the lane so it doesn't overlap too much
        float yRandom = (float) Math.random() * (LANE_HEIGHT - 20f)
                - (LANE_HEIGHT - 20f) / 2f;

        this.word = word;
        this.position = new Vector2(
                xPosition,
                yCenter + yLane + yRandom + 5f); // adjust for font baseline
        this.speed = speed;
        this.lane = lane;
    }

    public void update(float delta) {
        position.x -= speed * delta;
        tick += delta;
    }

    public void render(SpriteBatch batch, BitmapFont font, GlyphLayout gl) {
        Color previewColor = new Color(0.75f, 0.75f, 0.75f, 1f);
        Color typedColor = Color.WHITE;
        // Smooth rainbow color effect based on tick
        Color rgbColor = new Color(
                0.5f + 0.5f * (float) Math.sin(tick * 2f),
                0.5f + 0.5f * (float) Math.sin(tick * 2f + 2f),
                0.5f + 0.5f * (float) Math.sin(tick * 2f + 4f),
                1f);

        if (effect instanceof WordEffect.Hidden hiddenEffect) {
            // hide last N characters based on the effect, and only show it when
            // progress reaches N
            int hiddenCount = hiddenEffect.count();
            int visibleCount = Math.max(0, word.length() - hiddenCount);

            String hiddenPart = "";
            // randomly generate characters to hide the word
            for (int i = 0; i < hiddenCount; i++) {
                hiddenPart += (char) ('a' + (int) (Math.random() * 26));
            }

            if (progress < visibleCount) {
                font.setColor(rgbColor);
                font.draw(batch,
                        word.substring(0, visibleCount) + hiddenPart,
                        position.x, position.y);
                font.setColor(previewColor);
                font.draw(batch,
                        word.substring(0, visibleCount),
                        position.x, position.y);
            } else {
                font.setColor(previewColor);
                font.draw(batch, word, position.x, position.y);
            }

        } else if (effect instanceof WordEffect.Repeat repeatEffect) {
            // display the word with a multiplier based on how many times it needs to be
            // repeated, e.g. if count is 3, display "example³"
            font.setColor(previewColor);
            gl.setText(font, word);
            float wordWidth = gl.width;
            font.draw(batch, word, position.x, position.y);

            // draw the multiplier in the top right corner of the word
            String multiplier = "x" + (1 + repeatEffect.count() - repeatEffect.typedCount());
            gl.setText(font, multiplier);

            font.setColor(rgbColor);
            font.getData().setScale(0.75f);
            font.draw(batch, multiplier, position.x + wordWidth + 5f, position.y + gl.height / 2f);
            font.getData().setScale(1f);
        }

        // normal variant
        else {
            font.setColor(previewColor);
            font.draw(batch, word, position.x, position.y);
        }

        font.setColor(typedColor);
        font.draw(batch, word.substring(0, progress), position.x, position.y);
    }

    public void setEffect(WordEffect effect) {
        this.effect = effect;
    }
}
