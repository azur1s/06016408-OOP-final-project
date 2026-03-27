package game.data;

import java.io.Serializable;

import engine.graphics.TextureBatch;
import game.overrun.stage.Stage;

public class Item implements ItemAbility, Serializable {
    public boolean unlocked = false;

    public String iconPath;

    public String name;
    public String description;

    public int cooldownLevel = 0;
    public int damageLevel = 0;
    public int durationLevel = 0;

    @Override
    public void activate(Stage stage) {
    }

    @Override
    public void render(Stage stage, TextureBatch batch) {
    }
}
