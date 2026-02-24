package com.project.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Color;
import com.project.engine.Engine;
import com.project.engine.Fonts;
import com.project.engine.GameState;
import com.project.engine.OrthoCamera;
import com.project.engine.Texture;
import com.project.engine.TextureBatch;
import com.project.game.words.WordEntitiesManager;
import com.project.game.words.WordEntity;
import com.project.math.Vec2;

public class Game implements GameState {
    private TextureBatch batch;
    private Texture solidTexture;
    private Fonts fonts;

    private OrthoCamera camera;

    private WordEntitiesManager words;
    private InputHandler inputHandler;

    private Vec2 mouseWorld = new Vec2(0, 0);

    private boolean debug = false;

    @Override
    public void init(int width, int height) {
        batch = new TextureBatch();
        solidTexture = new Texture("textures/solid.png");

        fonts = new Fonts();
        fonts.loadFont("default", "GeistMono-Regular.otf");

        camera = new OrthoCamera(width, height, true);

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(1);
        inputHandler = new InputHandler(words);
    }

    @Override
    public void render(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            debug = !debug;
        }

        inputHandler.update();
        words.update(delta);

        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        camera.update();
        batch.setProjection(camera.combined);

        mouseWorld.set(camera.screenToWorld(Engine.input.getMousePosition()));

        drawWorld(batch);

        fonts.begin(camera.viewportWidth, camera.viewportHeight);
        words.renderText(camera, fonts);
        fonts.end();

        drawUI(batch);
        if (debug)
            drawUITextDebug(fonts);
    }

    @Override
    public void resize(int width, int height) {
        camera.setOrtho(width, height, true);
    }

    @Override
    public void cleanup() {
        batch.cleanup();
        solidTexture.cleanup();
    }

    private void drawWorld(TextureBatch batch) {
        batch.begin();

        // draw 5 lanes for the words to move in
        for (int i = 0; i < 5; i++) {
            float y = (i - 2) * WordEntity.LANE_SPACING;
            batch.setColor(i % 2 == 0
                    ? new Color(0.6f, 0.6f, 0.6f, 1f)
                    : new Color(0.5f, 0.5f, 0.5f, 1f));
            batch.draw(solidTexture, 0, y, 2000, WordEntity.LANE_HEIGHT);
        }

        batch.setColor(Color.WHITE);
        words.renderTexture(camera, batch);

        batch.end();
    }

    private void drawUI(TextureBatch batch) {
        batch.begin();
        batch.end();
    }

    private void drawUITextDebug(Fonts fonts) {
        fonts.begin(camera.viewportWidth, camera.viewportHeight);

        float fps = Engine.graphics.getFramesPerSecond();
        fonts.draw("default", String.format("FPS: %.2f", fps), 10, 30, 16);

        // draw mouse position in world space & screen space for debugging
        fonts.draw("default", String.format("Mouse World: (%.1f, %.1f)", mouseWorld.x, mouseWorld.y), 10, 50, 16);
        Vec2 mouseScreen = Engine.input.getMousePosition();
        fonts.draw("default", String.format("Mouse Screen: (%.1f, %.1f)", mouseScreen.x, mouseScreen.y), 10, 70, 16);

        fonts.draw("default", String.format("Spawn Cooldown: %.2f", words.spawnCooldown), 10, 90, 16);
        for (int i = 0; i < WordEntitiesManager.MAX_LANES; i++) {
            fonts.draw("default", String.format("Lane %d Cooldown: %.2f", i, words.laneCooldowns[i]), 10, 110 + i * 20,
                    16);
        }
        fonts.draw("default", String.format("Difficulty Ramp: %.2f", words.difficultyRamp), 10,
                110 + WordEntitiesManager.MAX_LANES * 20,
                16);

        fonts.end();
    }
}
