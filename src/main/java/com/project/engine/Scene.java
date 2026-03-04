package com.project.engine;

import static org.lwjgl.glfw.GLFW.*;

import com.project.engine.graphics.OrthoCamera;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Layout;
import com.project.engine.ui.UIManager;

/**
 * Scene class acts as a container for all game objects, UI elements, and logic
 * for a particular scene or level in the game (main menu/gameplay/etc.).
 *
 * A scene will have these components pre-initialized by the engine:
 * - `batch` TextureBatch for rendering sprites
 * - `camera` OrthoCamera for world-space rendering (0, 0 is center of screen)
 * - `uiCamera` OrthoCamera for UI rendering (0, 0 is bottom-left corner of
 * screen)
 * - `uiManager` UIManager for managing UI elements
 * - `mouseScreen` Vec2 for tracking mouse position in screen space
 * - `mouseWorld` Vec2 for tracking mouse position in world space
 * - `layout` Layout for managing UI layout and resizing
 */
public abstract class Scene {
    protected TextureBatch batch;
    /// World-space camera. (0, 0) is the center of the screen.
    protected OrthoCamera camera;
    /// UI-space camera. (0, 0) is the bottom-left corner of the screen.
    protected OrthoCamera uiCamera;
    protected UIManager uiManager = new UIManager();

    /// Mouse position in screen space, (0, 0) is the bottom-left corner of the
    /// screen.
    protected Vec2 mouseScreen = new Vec2(0, 0);
    /// Mouse position in world space, (0, 0) is the center of the screen.
    protected Vec2 mouseWorld = new Vec2(0, 0);

    protected Layout layout;

    // Internal methods called by the engine/LWJGL main loop

    public void internalInit(int width, int height) {
        this.batch = new TextureBatch();
        this.camera = new OrthoCamera(width, height, true);
        this.uiCamera = new OrthoCamera(width, height, false);
        this.layout = new Layout(width, height);
        init(width, height);
    }

    public void internalResize(int width, int height) {
        this.camera.setOrtho(width, height, true);
        this.uiCamera.setOrtho(width, height, false);
        this.layout.resize(width, height);
        resize(width, height);
    }

    public void internalTick(float delta) {
        this.camera.update();
        this.uiCamera.update();

        Vec2 rawMousePos = Engine.input.getMousePosition();
        this.mouseScreen.set(new Vec2(rawMousePos.x, this.camera.viewportHeight - rawMousePos.y));
        this.mouseWorld.set(this.camera.screenToWorld(rawMousePos));

        this.uiManager.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        update(delta);

        this.batch.setProjection(this.camera.combined);
        this.batch.begin();
        renderWorld(delta);
        this.batch.end();

        this.batch.setProjection(this.uiCamera.combined);
        this.batch.begin();
        renderUI(delta);
        this.batch.end();
    }

    public void internalCleanup() {
        if (batch != null)
            batch.cleanup();
        cleanup();
    }

    // User-level methods

    /**
     * Initializes the scene. Called once when the scene is first loaded.
     */
    public void init(int width, int height) {
    };

    /**
     * Updates the game state.
     */
    public void update(float delta) {
    };

    /**
     * Renders the world (game objects, background, etc.) in world space using the
     * world camera.
     */
    public void renderWorld(float delta) {
    };

    /**
     * Renders the UI (menus, buttons, text, etc.) in screen space using the UI
     * camera.
     */
    public void renderUI(float delta) {
    };

    /**
     * Called when the window is resized.
     */
    public void resize(int width, int height) {
    };

    /**
     * Called when the scene is being destroyed.
     */
    public void cleanup() {
    };
}
