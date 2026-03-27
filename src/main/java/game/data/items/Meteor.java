package game.data.items;

import engine.Engine;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import game.data.Item;
import game.overrun.projectiles.MeteorProjectile;
import game.overrun.stage.Stage;

public class Meteor extends Item {
    private static final Texture[] METEOR_TEXTURES = {
            new Texture("textures/items/meteor_1.png"),
            new Texture("textures/items/meteor_2.png"),
    };

    public Meteor() {
        this.name = "Meteor";
        this.description = "Summon meteors that fall from the sky and crush enemies. More meteors fall with higher duration.";
        this.icon = new Texture("textures/items/icon_meteor.png");
    }

    @Override
    public void activate(Stage stage) {
        this.setTimer();
        this.active = true;

        // Calculate number of meteors based on duration
        // 1 meteor per 0.5 seconds, minimum 1 meteor
        float duration = this.getDuration();
        int meteorCount = Math.max(1, (int) (duration / 0.5f));

        // Spawn meteors with staggered timing
        for (int i = 0; i < meteorCount; i++) {
            float delay = (float) i * (duration / meteorCount);

            Engine.runAfter(() -> {
                // Pick a random texture variant
                Texture texture = METEOR_TEXTURES[(int) (Math.random() * METEOR_TEXTURES.length)];
                MeteorProjectile meteor = new MeteorProjectile(texture);
                stage.projectiles.spawnProjectile(meteor);
            }, delay);
        }

        Engine.runAfter(() -> {
            this.active = false;
        }, duration);
    }

    @Override
    public void render(Stage stage, TextureBatch batch) {
        // Meteors render themselves; nothing to do here
    }
}
