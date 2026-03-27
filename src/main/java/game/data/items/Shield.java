package game.data.items;

import engine.Engine;
import engine.graphics.Color;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import game.data.Item;
import game.overrun.stage.Stage;

public class Shield extends Item {
    private static final Texture SHIELD_ACTIVE_TEXTURE = new Texture("textures/items/shield_active.png");
    private static final Texture ICON_TEXTURE = new Texture("textures/items/icon_shield.png");

    private boolean active = false;

    public Shield() {
        this.name = "Shield";
        this.description = "Become invincible for a short duration, preventing all damage from enemies.";
        this.icon = ICON_TEXTURE;
    }

    @Override
    public void activate(Stage stage) {
        this.setTimer();

        this.active = true;
        Color previousColor = stage.playerColor;
        stage.playerColor = new Color(255 / 255f, 211 / 255f, 54 / 255f, 255 / 255f);
        Engine.runAfter(() -> {
            this.active = false;
            stage.playerColor = previousColor;
        }, super.activeTime);
    }

    @Override
    public void render(Stage stage, TextureBatch batch) {
        if (active) {
            float yScale = 1f + (float) Math.sin(Engine.graphics.getTime() * 4f) * 0.1f;
            batch.draw(SHIELD_ACTIVE_TEXTURE,
                    stage.playerManager.getPosition().x,
                    (stage.playerManager.getPosition().y + 80f) * yScale,
                    64, 64);
        }
    }
}
