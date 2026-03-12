package com.project.engine;

import com.project.engine.math.Vec2;

public class GameConfig {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static Vec2 getResolution() {
        return new Vec2(getWidth(), getHeight());
    }
}
