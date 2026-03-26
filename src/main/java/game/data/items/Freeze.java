package game.data.items;

import engine.Engine;
import engine.graphics.Color;
import game.data.Item;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

public class Freeze extends Item {
    public Freeze() {
        this.name = "Freeze";
        this.description = "Freezes all enemies on screen for a short duration.";
    }

    @Override
    public void activate(WordEntitiesManager words, game.overrun.stage.Stage stage) {
        for (WordEntity word : words.getWordEntities()) {
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
