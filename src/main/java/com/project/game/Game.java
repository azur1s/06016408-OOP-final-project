package com.project.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.GameState;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.OrthoCamera;
import com.project.engine.graphics.Texture;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.ui.Button;
import com.project.game.words.WordEntitiesManager;
import com.project.game.words.WordEntity;
import com.project.math.Vec2;

public class Game implements GameState {
    private TextureBatch batch;
    private Texture solidTexture;

    private FontAtlas font;

    private OrthoCamera camera;
    private OrthoCamera uiCamera;

    private WordEntitiesManager words;
    private InputHandler inputHandler;

    private Vec2 mouseScreen = new Vec2(0, 0);
    private Vec2 mouseWorld = new Vec2(0, 0);

    private Button testButton;

    private boolean debug = false;

    @Override
    public void init(int width, int height) {
        batch = new TextureBatch();
        solidTexture = new Texture("textures/solid.png");

        font = new FontAtlas("GeistMono-Regular.otf", 32);

        camera = new OrthoCamera(width, height, true);
        uiCamera = new OrthoCamera(width, height, false);

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(1);
        inputHandler = new InputHandler(words);

        testButton = new Button(new Vec2(100, 25), new Vec2(200, 50), "Test Button",
                new Color(1.0f, 0.0f, 0.0f, 1.0f),
                new Color(0.8f, 0.0f, 0.0f, 1.0f),
                new Texture("textures/button_test.png"));
        testButton.setOnClick(() -> {
            System.out.println("Button clicked!");
            words.addNewEntites(1);
        });
    }

    @Override
    public void render(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            debug = !debug;
        }

        inputHandler.update();
        testButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        words.update(delta);

        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        camera.update();

        Vec2 rawMousePos = Engine.input.getMousePosition();
        mouseScreen.set(new Vec2(rawMousePos.x, camera.viewportHeight - rawMousePos.y));
        mouseWorld.set(camera.screenToWorld(rawMousePos));

        batch.setProjection(camera.combined);
        batch.begin();
        drawWorld(batch);
        batch.end();

        batch.setProjection(uiCamera.combined);
        batch.begin();
        drawUI(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setOrtho(width, height, true);
        uiCamera.setOrtho(width, height, false);
    }

    @Override
    public void cleanup() {
        batch.cleanup();
        solidTexture.cleanup();
    }

    private void drawWorld(TextureBatch batch) {

        // draw 5 lanes for the words to move in
        for (int i = 0; i < 5; i++) {
            float y = (i - 2) * WordEntity.LANE_SPACING;
            batch.setColor(i % 2 == 0
                    ? new Color(0.0f, 0.0f, 0.0f, 0.2f)
                    : new Color(0.0f, 0.0f, 0.0f, 0.4f));
            batch.draw(solidTexture, 0, y, 2000, WordEntity.LANE_HEIGHT);
        }

        batch.setColor(Color.WHITE);
        words.render(batch, font);
    }

    private void drawUI(TextureBatch batch) {
        testButton.render(batch, font, mouseScreen);

        if (debug) {
            font.drawTextUnaligned(batch,
                    String.format("Mouse World: (%.2f, %.2f)", mouseWorld.x, mouseWorld.y), 20, 20, Color.BLACK, 16);
            font.drawTextUnaligned(batch,
                    String.format("Mouse Screen: (%.2f, %.2f)", mouseScreen.x, mouseScreen.y), 20, 50, Color.BLACK,
                    16);
        }
    }
}
