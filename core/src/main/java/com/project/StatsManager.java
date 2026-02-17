package com.project;

import com.badlogic.gdx.Gdx;
import com.project.words.WordEntitiesManager;

public class StatsManager {
    // Reference to Words for average word length
    private WordEntitiesManager words;

    public float charTimer = 0f;
    public int lastWpm = 0;

    public StatsManager(WordEntitiesManager words) {
        this.words = words;
    }

    public void update(int charCount) {
        charTimer += Gdx.graphics.getDeltaTime();

        if (charTimer >= 1.0f) {
            lastWpm = (int) ((charCount / words.averageWordLength) * (60.0f / charTimer));
            charTimer = 0f;
        }
    }
}
