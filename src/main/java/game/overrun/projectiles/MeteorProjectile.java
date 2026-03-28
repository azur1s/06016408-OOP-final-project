package game.overrun.projectiles;

import engine.entities.Collidable;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

public class MeteorProjectile extends Projectile {
    private static final float FALL_SPEED = 500f; // pixels per second
    private static final float METEOR_SIZE = 60f; // width and height
    private static final float METEOR_LIFETIME = 15f; // max time before auto-destroy

    private Texture texture;

    public MeteorProjectile(Texture texture) {
        super(new Vec2((float) (Math.random() * 600) - 200f, 300f),
                new Vec2(METEOR_SIZE, METEOR_SIZE),
                new Vec2(1f, -1f),
                FALL_SPEED);

        this.texture = texture;
        this.lifetime = METEOR_LIFETIME;
    }

    @Override
    public void render(TextureBatch batch) {
        batch.draw(texture, position.x, position.y, size.x * 2f, size.y * 2f);
    }

    @Override
    public void onCollision(Collidable other) {
        // Destroy meteor on collision with enemy
        active = false;
    }
}
