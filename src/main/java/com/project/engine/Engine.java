package com.project.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.project.engine.graphics.Graphics;

public class Engine {
    private static long window;

    public static int width;
    public static int height;

    public static Input input = new Input();
    public static Graphics graphics = new Graphics();
    public static AudioManager audio = new AudioManager();

    private static Scene currentScene;

    public static void setScene(Scene newScene) {
        if (currentScene != null)
            currentScene.cleanup();
        currentScene = newScene;
        currentScene.internalInit(width, height);
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    public static void requestExit() {
        glfwSetWindowShouldClose(window, true);
    }

    public static void setWindow(long win) {
        window = win;
    }
}
