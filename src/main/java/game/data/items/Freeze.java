package game.data.items;

import engine.Engine;
import game.data.Item;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

public class Freeze extends Item {
    public Freeze() {
        this.name = "Freeze";
        this.description = "Freezes all enemies on screen for a short duration.";
    }

    @Override
    public void activate(WordEntitiesManager words) {
        System.out.println("Activating Freeze item ability!");
        for (WordEntity word : words.getWordEntities()) {
            float previousSpeed = word.speed;
            word.speed = 0f;
            Engine.runAfter(() -> {
                if (word.speed == 0f) {
                    word.speed = previousSpeed;
                }
            }, super.durationLevel + 3f);
        }
    }
}
