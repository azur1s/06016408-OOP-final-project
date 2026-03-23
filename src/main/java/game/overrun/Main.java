package game.overrun;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import engine.Engine;
import engine.Scene;
import engine.entities.Collidable;
import engine.entities.CollisionManager;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import engine.ui.Button;
import game.overrun.projectiles.ProjectileManager;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

public class Main extends Scene {
    private Texture solidTexture;
    private Texture backgroundTexture;

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
        backgroundTexture = new Texture("textures/background/stage1.png");

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
            Engine.setScene(new game.menu.Main());
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

        // Draw background
        super.batch.setColor(Color.WHITE);
        super.batch.draw(backgroundTexture, 0, 0, Engine.width, Engine.height);

        if (debug) {
            // draw 4 lanes for the words to move in
            for (int i = 0; i < 4; i++) {
                float y = (i - 2) * WordEntity.LANE_SPACING + WordEntity.LANE_Y_OFFSET;
                super.batch.setColor(i % 2 == 0
                        ? new Color(1.0f, 0.0f, 0.0f, 0.2f)
                        : new Color(1.0f, 0.0f, 0.0f, 0.4f));
                super.batch.draw(solidTexture, 0, y, 2000, WordEntity.LANE_HEIGHT);
            }
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
