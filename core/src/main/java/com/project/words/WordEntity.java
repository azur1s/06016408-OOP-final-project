package com.project.words;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class WordEntity {
    public String word;
    public int progress = 0; // how many characters have been correctly typed

    public Vector2 position;
    public float speed;

    public WordEntity(String word, Vector2 position, float speed) {
        this.word = word;
        this.position = position;
        this.speed = speed;
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        font.setColor(1f, 1f, 1f, 0.5f);
        font.draw(batch, word, position.x, position.y);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, word.substring(0, progress), position.x, position.y);
    }
}
