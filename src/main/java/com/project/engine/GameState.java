package com.project.engine;

public interface GameState {
    void init(int width, int height);

    void render(float delta);

    void resize(int width, int height);

    void cleanup();
}
