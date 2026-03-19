package com.project.scenes.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.entities.Collidable;
import com.project.engine.entities.CollisionManager;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Button;
import com.project.scenes.game.projectiles.ProjectileManager;
import com.project.scenes.game.words.WordEntitiesManager;
import com.project.scenes.game.words.WordEntity;

public class Main extends Scene {
    private Texture solidTexture;

    private FontAtlas font;

    private CollisionManager collision;
    private ProjectileManager projectiles;
    private WordEntitiesManager words;
    private InputHandler inputHandler;

    private Button testButton;
    private Button pauseButton;
    private Button exitButton;

    private boolean debug = false;
    private boolean isPaused = false;

    @Override
    public void init(int width, int height) {
        Engine.audio.stopSound("bgm_main");

        solidTexture = new Texture("textures/solid.png");

        font = new FontAtlas("GeistMono-Regular.otf", 32);

        collision = new CollisionManager();
        projectiles = new ProjectileManager();

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(1);
        words.addListener(projectiles);

        inputHandler = new InputHandler(words);

        testButton = new Button(
                super.layout.bottomRight(100, 25),
                new Vec2(200, 50),
                "Add entity",
                new Color(1.0f, 0.0f, 0.0f, 1.0f),
                new Color(0.8f, 0.0f, 0.0f, 1.0f),
                new Texture("textures/button_test.png"));

        testButton.setOnClick(() -> {
            System.out.println("Button clicked!");
            words.addNewEntites(1);
        });

        pauseButton = new Button(
                super.layout.topLeft(100, 50),
                new Vec2(100, 50),
                "Pause",
                new Texture("textures/button_test.png"));

        pauseButton.setOnClick(() -> {
            isPaused = !isPaused;
            if (isPaused) {
                // Change text or texture when paused if needed
                System.out.println("Game Paused");
            } else {
                System.out.println("Game Resumed");
            }
        });

        exitButton = new Button(
                super.layout.center(0, 0),
                new Vec2(200, 50),
                "Exit Game",
                new Texture("textures/button_test.png"));

        exitButton.setOnClick(() -> {
            Engine.setScene(new com.project.scenes.menu.Main());
        });
    }

    @Override
    public void update(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            debug = !debug;
        }

        if (!isPaused) {
            inputHandler.update();
            words.update(delta);
            projectiles.update(delta);

            ArrayList<Collidable> collidables = new ArrayList<>();
            collidables.addAll(words.getCollidables());
            collidables.addAll(projectiles.getCollidables());
            collision.detectAndDispatch(collidables);

            words.removeInactive();
            projectiles.removeInactive();
        } else {
            // Update exit button only when paused
            exitButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        }

        testButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
        pauseButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));
    }

    @Override
    public void renderWorld(float delta) {
        glClearColor(0.4f, 0.4f, 0.4f, 1.0f);

        // draw 5 lanes for the words to move in
        for (int i = 0; i < 5; i++) {
            float y = (i - 2) * WordEntity.LANE_SPACING;
            super.batch.setColor(i % 2 == 0
                    ? new Color(0.0f, 0.0f, 0.0f, 0.2f)
                    : new Color(0.0f, 0.0f, 0.0f, 0.4f));
            super.batch.draw(solidTexture, 0, y, 2000, WordEntity.LANE_HEIGHT);
        }

        super.batch.setColor(Color.WHITE);
        words.render(super.batch, font);
        projectiles.render(super.batch);
    }

    @Override
    public void renderUI(float delta) {
        testButton.render(super.batch, font, mouseScreen);
        pauseButton.render(super.batch, font, mouseScreen);

        if (isPaused) {
            // Darken the screen when paused
            super.batch.setColor(new Color(0f, 0f, 0f, 0.5f));
            super.batch.draw(solidTexture, Engine.width * 0.5f, Engine.height * 0.5f, Engine.width, Engine.height);
            super.batch.setColor(Color.WHITE);

            exitButton.render(super.batch, font, mouseScreen);

            Vec2 pauseTextPos = super.layout.center(0, -100);
            font.drawTextAligned(super.batch, "PAUSED", pauseTextPos.x, pauseTextPos.y, Color.WHITE, 64);
        }

        if (debug) {
            font.drawTextUnaligned(super.batch,
                    String.format("Mouse World: (%.2f, %.2f)", mouseWorld.x, mouseWorld.y), 20, 20, Color.BLACK, 16);
            font.drawTextUnaligned(super.batch,
                    String.format("Mouse Screen: (%.2f, %.2f)", mouseScreen.x, mouseScreen.y), 20, 50, Color.BLACK,
                    16);
        }
    }

    @Override
    public void cleanup() {
        solidTexture.cleanup();
    }
}
