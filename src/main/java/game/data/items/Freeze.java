package game.data.items;

import engine.Engine;
import engine.graphics.Color;
import engine.graphics.Texture;
import game.data.Item;
import game.overrun.stage.Stage;
import game.overrun.words.WordEntity;

public class Freeze extends Item {
    private static final Texture ICON_TEXTURE = new Texture("textures/items/icon_freeze.png");

    public Freeze() {
        this.name = "Freeze";
        this.description = "Freezes all enemies on screen for a short duration.";
        this.icon = ICON_TEXTURE;
    }

    @Override
    public void activate(Stage stage) {
        this.setTimer();

        for (WordEntity word : stage.words.getWordEntities()) {
            float previousSpeed = word.speed;
            Color previousColor = word.color;
            word.speed = 0f;
            word.color = Color.BLUE;
            Engine.runAfter(() -> {
                if (word.speed == 0f) {
                    word.speed = previousSpeed;
                    word.color = previousColor;
                }
            }, super.activeTime);
        }
    }
}
