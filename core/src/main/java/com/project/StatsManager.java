package com.project;

import com.badlogic.gdx.Gdx;

public class StatsManager {
    public float charTimer = 0f;
    public int lastWpm = 0;

    public void update(int charCount) {
        charTimer += Gdx.graphics.getDeltaTime();

        if (charTimer >= 1.0f) {
            // Standard WPM formula: (chars / 5) / (seconds / 60)
            lastWpm = (int) ((charCount / 5.0f) * (60.0f / charTimer));
            charTimer = 0f;
        }
    }
}
