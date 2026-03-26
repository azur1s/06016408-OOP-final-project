package game.data;

import java.io.Serializable;

import game.overrun.stage.Stage;
import game.overrun.words.WordEntitiesManager;

public class Item implements ItemAbility, Serializable {
    public boolean unlocked = false;

    public String name;
    public String description;

    public int cooldownLevel = 0;
    public int damageLevel = 0;
    public int durationLevel = 0;

    @Override
    public void activate(WordEntitiesManager words, Stage stage) {
    }
}
