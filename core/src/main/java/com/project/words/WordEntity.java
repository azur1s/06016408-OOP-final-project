package com.project.words;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class WordEntity {
    public static final float LANE_HEIGHT = 80f;
    public static final float LANE_GAP = 20f;
    public static final float LANE_SPACING = LANE_HEIGHT + LANE_GAP;

    public String word;
    public int progress = 0; // how many characters have been correctly typed

    public Vector2 position;
    /**
     * The lane the word entity is in [0 - 4].
     */
    public int lane;
    public float speed;

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
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        font.setColor(1f, 1f, 1f, 0.5f);
        font.draw(batch, word, position.x, position.y);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, word.substring(0, progress), position.x, position.y);
    }

}
