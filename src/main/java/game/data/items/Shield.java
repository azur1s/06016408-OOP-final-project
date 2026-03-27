package game.data.items;

import engine.Engine;
import engine.graphics.Color;
import game.data.Item;
import game.overrun.stage.Stage;
import game.overrun.words.WordEntitiesManager;

public class Shield extends Item {
    public Shield() {
        this.name = "Shield";
        this.description = "Become invincible for a short duration, preventing all damage from enemies.";
    }

    @Override
    public void activate(WordEntitiesManager words, Stage stage) {
        Color previousColor = stage.playerColor;
        stage.playerColor = new Color(255 / 255f, 211 / 255f, 54 / 255f, 255 / 255f);
        System.out.println("Player is now invincible!");
        Engine.runAfter(() -> {
            stage.playerColor = previousColor;
        }, super.durationLevel + 5f);
    }
}
