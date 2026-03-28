package engine;

import static org.lwjgl.glfw.GLFW.*;

import engine.graphics.OrthoCamera;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import engine.math.Vec2;
import engine.ui.Button;
import engine.ui.Layout;
import engine.ui.UIManager;

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

    private boolean exitPromptVisible = false;
    private FontAtlas exitPromptFont;
    private Texture exitPromptPanelTexture;
    private Texture exitPromptButtonTexture;
    private Button confirmExitButton;
    private Button cancelExitButton;

    // Internal methods called by the engine/LWJGL main loop

    public void internalInit(int width, int height) {
        this.batch = new TextureBatch();
        this.camera = new OrthoCamera(width, height, true);
        this.uiCamera = new OrthoCamera(width, height, false);
        this.layout = new Layout(width, height);
        initExitPrompt();
        init(width, height);
    }

    public void internalResize(int width, int height) {
        this.camera.setOrtho(width, height, true);
        this.uiCamera.setOrtho(width, height, false);
        this.layout.resize(width, height);
        layoutExitPromptButtons();
        resize(width, height);
    }

    public void internalTick(float delta) {
        this.camera.update();
        this.uiCamera.update();

        Vec2 rawMousePos = Engine.input.getMousePosition();
        this.mouseScreen.set(new Vec2(rawMousePos.x, this.camera.viewportHeight - rawMousePos.y));
        this.mouseWorld.set(this.camera.screenToWorld(rawMousePos));

        boolean exitPromptToggled = false;
        if (usesGlobalExitPrompt() && Engine.input.isKeyPressed(GLFW_KEY_ESCAPE)) {
            exitPromptVisible = !exitPromptVisible;
            exitPromptToggled = true;
        }

        if (exitPromptVisible) {
            updateExitPrompt();
        } else {
            this.uiManager.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
            if (!exitPromptToggled) {
                update(delta);
            }
        }

        this.batch.setProjection(this.camera.combined);
        this.batch.begin();
        renderWorld(delta);
        this.batch.end();

        this.batch.setProjection(this.uiCamera.combined);
        this.batch.begin();
        renderUI(delta);
        if (exitPromptVisible) {
            renderExitPrompt();
        }
        this.batch.end();
    }

    public void internalCleanup() {
        if (batch != null)
            batch.cleanup();
        cleanup();
        cleanupExitPrompt();
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

    /**
     * Optional hook to queue expensive asset decode work before {@link #init(int,
     * int)}.
     *
     * Keep this method lightweight and non-OpenGL. It is primarily intended for
     * background decode requests such as Texture.preloadAsync(...).
     */
    public void preloadAssets() {
        // Base prompt textures are used by the global exit dialog in every scene.
        Texture.preloadAsync("textures/solid.png", "textures/button_test.png");
    };

    protected boolean usesGlobalExitPrompt() {
        return true;
    }

    private void initExitPrompt() {
        exitPromptFont = new FontAtlas("GeistMono-Regular.otf", 28);
        exitPromptPanelTexture = new Texture("textures/solid.png");
        exitPromptButtonTexture = new Texture("textures/button_test.png");

        confirmExitButton = new Button(
                new Vec2(0, 0),
                new Vec2(180, 60),
                "Exit",
                exitPromptButtonTexture);
        confirmExitButton.setOnClick(Engine::requestExit);

        cancelExitButton = new Button(
                new Vec2(0, 0),
                new Vec2(180, 60),
                "Cancel",
                exitPromptButtonTexture);
        cancelExitButton.setOnClick(() -> exitPromptVisible = false);

        layoutExitPromptButtons();
    }

    private void layoutExitPromptButtons() {
        if (layout == null || confirmExitButton == null || cancelExitButton == null) {
            return;
        }

        confirmExitButton = new Button(
                layout.center(-110, 80),
                new Vec2(180, 60),
                "Exit",
                exitPromptButtonTexture);
        confirmExitButton.setOnClick(Engine::requestExit);

        cancelExitButton = new Button(
                layout.center(110, 80),
                new Vec2(180, 60),
                "Cancel",
                exitPromptButtonTexture);
        cancelExitButton.setOnClick(() -> exitPromptVisible = false);
    }

    private void updateExitPrompt() {
        boolean mouseReleased = Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT);
        confirmExitButton.update(mouseScreen, mouseReleased);
        cancelExitButton.update(mouseScreen, mouseReleased);
    }

    private void renderExitPrompt() {
        batch.setColor(new Color(0f, 0f, 0f, 0.55f));
        batch.draw(exitPromptPanelTexture, layout.res.x * 0.5f, layout.res.y * 0.5f, layout.res.x, layout.res.y);

        Vec2 panelPos = layout.center(0, 0);
        Vec2 panelSize = new Vec2(520, 260);
        batch.setColor(new Color(0.95f, 0.90f, 0.78f, 1.0f));
        batch.draw(exitPromptPanelTexture, panelPos.x, panelPos.y, panelSize.x, panelSize.y);

        exitPromptFont.drawTextAligned(batch, "Exit Game?", panelPos.x, panelPos.y + 40, Color.BLACK, 38);
        exitPromptFont.drawTextAligned(batch, "Press ESC again to close", panelPos.x, panelPos.y, Color.BLACK, 18);

        batch.setColor(Color.WHITE);
        confirmExitButton.render(batch, exitPromptFont, mouseScreen);
        cancelExitButton.render(batch, exitPromptFont, mouseScreen);
    }

    private void cleanupExitPrompt() {
        if (exitPromptFont != null) {
            exitPromptFont.cleanup();
        }
        if (exitPromptPanelTexture != null) {
            exitPromptPanelTexture.cleanup();
        }
        if (exitPromptButtonTexture != null) {
            exitPromptButtonTexture.cleanup();
        }
    }
}
