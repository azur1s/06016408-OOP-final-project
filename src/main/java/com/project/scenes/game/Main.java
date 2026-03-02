package com.project.scenes.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.project.engine.Engine;
import com.project.engine.Scene;
import com.project.engine.graphics.Color;
import com.project.engine.graphics.FontAtlas;
import com.project.engine.graphics.Texture;
import com.project.engine.math.Vec2;
import com.project.engine.ui.Button;
import com.project.scenes.game.words.WordEntitiesManager;
import com.project.scenes.game.words.WordEntity;

public class Main extends Scene {
    private Texture solidTexture;

    private FontAtlas font;

    private WordEntitiesManager words;
    private InputHandler inputHandler;

    private Button testButton;

    private boolean debug = false;

    @Override
    public void init(int width, int height) {
        solidTexture = new Texture("textures/solid.png");

        font = new FontAtlas("GeistMono-Regular.otf", 32);

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(1);
        inputHandler = new InputHandler(words);

        testButton = new Button(new Vec2(100, 25), new Vec2(200, 50),
                "Add entity",
                new Color(1.0f, 0.0f, 0.0f, 1.0f),
                new Color(0.8f, 0.0f, 0.0f, 1.0f),
                new Texture("textures/button_test.png"));
        testButton.setOnClick(() -> {
            System.out.println("Button clicked!");
            words.addNewEntites(1);
        });
    }

    @Override
    public void update(float delta) {
        if (Engine.input.isKeyPressed(GLFW_KEY_F3)) {
            debug = !debug;
        }

        inputHandler.update();

        testButton.update(mouseScreen, Engine.input.isMouseButtonReleased(GLFW_MOUSE_BUTTON_LEFT));

        words.update(delta);
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
    }

    @Override
    public void renderUI(float delta) {
        testButton.render(super.batch, font, mouseScreen);

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
