package game.menu.tutorial;

import static org.lwjgl.opengl.GL11.glClearColor;

import engine.Engine;
import engine.Scene;
import engine.graphics.Color;
import engine.graphics.FontAtlas;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.PlayerData;
import game.data.PlayerDataSaver;
import game.menu.components.UIButton;

public class tutorial extends Scene{

    private FontAtlas font;
    private Texture backgroundTexture, nextButtonTexture;
    private UIButton nextButton;
    
    @Override
    public void init(int width, int height) {

        font = new FontAtlas("GeistMono-Regular.otf", 32);
        nextButtonTexture = new Texture("textures/button_test.png");
        backgroundTexture = new Texture("textures/solid.png");

        Vec2 btnSize = new Vec2(140, 50);
        nextButton = new UIButton(
                super.layout.bottomCenter(0, 90),
                btnSize,
                "I'm Ready",
                nextButtonTexture);

        super.uiManager.add(nextButton);

        nextButton.setOnClick(() -> {
            PlayerData.hasCompletedTutorial = true;
            PlayerDataSaver.save();
            Engine.setScene(new game.menu.mode.Mode());
        });
    }

    @Override
    public void update(float delta) {
        
    }

    @Override
    public void renderUI(float delta) {
        glClearColor(0.32f, 0.28f, 0.22f, 1.0f); // Background color

        Vec2 backgroundPos = super.layout.center(0, 0);
        Vec2 backgroundSize = new Vec2(super.layout.res.x * 0.72f, super.layout.res.y * 0.78f);

        super.batch.setColor(new Color(0.93f, 0.88f, 0.76f, 1.0f)); // Set color for the variable background
        super.batch.draw(backgroundTexture, backgroundPos.x, backgroundPos.y, backgroundSize.x, backgroundSize.y);
        super.batch.setColor(Color.WHITE);

        String tutorialText[] = {
                "Welcome to the tutorial! Here you will learn the basics of the game.",
                "Use WASD to move around and left-click to attack.",
                "Defeat enemies to earn points and upgrade your character.",
                "Good luck, and have fun playing!"
        };

        Vec2 TopicPos = super.layout.topCenter(0, 100);
        font.drawTextAligned(batch, "Welcome to the Tutorial!", TopicPos.x, TopicPos.y, Color.BLACK, 32);
        TopicPos.y -= 180;
        for (String line : tutorialText) {
            TopicPos.y -= 40; // Move down for the next line
            font.drawTextAligned(super.batch, line, TopicPos.x, TopicPos.y, Color.BLACK, 24);
        }
        super.uiManager.render(super.batch, font, mouseScreen);
    }


    @Override
    public void cleanup() {
        font.cleanup();
        backgroundTexture.cleanup();
        nextButtonTexture.cleanup();
    }
}
