package com.project.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.project.engine.graphics.Graphics;

public class Engine {
    private static long window;

    public static int width;
    public static int height;

    public static Input input = new Input();
    public static Graphics graphics = new Graphics();

    private static GameState currentGameState;

    public static void setGameState(GameState newState) {
        if (currentGameState != null) {
            currentGameState.cleanup();
        }
        currentGameState = newState;
        currentGameState.internalInit(width, height);
    }

    public static void requestExit() {
        glfwSetWindowShouldClose(window, true);
    }

    public static void setWindow(long win) {
        window = win;
    }
}
