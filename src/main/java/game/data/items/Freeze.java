package game.data.items;

import engine.Engine;
import engine.graphics.Color;
import game.data.Item;
import game.overrun.stage.Stage;
import game.overrun.words.WordEntity;

public class Freeze extends Item {
    public Freeze() {
        this.name = "Freeze";
        this.description = "Freezes all enemies on screen for a short duration.";
        this.iconPath = "textures/items/icon_freeze.png";
    }

    @Override
    public void activate(Stage stage) {
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
            }, super.durationLevel + 3f);
        }
    }
}
