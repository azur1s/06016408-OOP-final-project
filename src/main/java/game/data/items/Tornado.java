package game.data.items;

import engine.Engine;
import engine.graphics.AnimationClip;
import engine.graphics.Texture;
import engine.math.Vec2;
import game.data.Item;
import game.overrun.stage.Stage;
import game.overrun.words.WordEntity;

public class Tornado extends Item {
    private static final AnimationClip TORNADO_ACTIVE_TEXTURE = new AnimationClip(new Texture[] {
            new Texture("textures/items/tornado_active_1.png"),
            new Texture("textures/items/tornado_active_2.png"),
    }, 0.1f);
    private static final Texture ICON_TEXTURE = new Texture("textures/items/icon_tornado.png");

    public Tornado() {
        this.name = "Tornado";
        this.description = "Summon a tornado that pushes back enemies in its path.";
        this.icon = ICON_TEXTURE;
    }

    @Override
    public void activate(Stage stage) {
        this.setTimer();

        this.active = true;
        Engine.runAfter(() -> {
            this.active = false;
        }, super.activeTime);

        // for each 0.25 seconds while active, push back words by 50 pixels
        for (float t = 0f; t < super.activeTime; t += 0.25f) {
            Engine.runAfter(() -> {
                for (WordEntity word : stage.words.getWordEntities()) {
                    word.position = word.position.add(new Vec2(10f, 0f));
                }
            }, t);
        }
    }

    @Override
    public void render(Stage stage, engine.graphics.TextureBatch batch) {
        if (active) {
            batch.drawAnimation(TORNADO_ACTIVE_TEXTURE,
                    new Vec2(
                            400f - (super.activeTime / super.getDuration()) * 600f,
                            0f),
                    512f, 512f);
        }
    }
}
