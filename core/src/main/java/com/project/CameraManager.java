package com.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class CameraManager {

    /** Camera for rendering game world (word entities, particles, lanes) */
    private final OrthographicCamera worldCamera;

    /** Viewport for the game world with virtual coordinates */
    private final ExtendViewport worldViewport;

    /** Camera for rendering UI elements (HUD, health bar, score) */
    private final OrthographicCamera uiCamera;

    /** Viewport for UI that matches screen pixels */
    private final ExtendViewport uiViewport;

    /**
     * Initializes the camera system with two separate camera/viewport pairs:
     * one for the game world and one for the UI overlay.
     */
    public CameraManager() {
        // World camera uses virtual coordinates
        worldCamera = new OrthographicCamera();
        worldViewport = new ExtendViewport(
                GameConfig.WORLD_WIDTH,
                GameConfig.WORLD_HEIGHT,
                worldCamera);

        // UI camera matches screen dimensions for pixel-perfect UI
        uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                uiCamera);
    }

    /**
     * Updates both viewports when the window is resized.
     */
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }

    public ExtendViewport getWorldViewport() {
        return worldViewport;
    }

    public OrthographicCamera getUICamera() {
        return uiCamera;
    }

    public ExtendViewport getUIViewport() {
        return uiViewport;
    }
}
