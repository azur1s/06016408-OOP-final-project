package game.overrun.projectiles;

import engine.graphics.AnimationClip;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

public class TurretProjectile extends Projectile {
    private static final float LIFETIME = 2.2f;

    private final AnimationClip animation;

    public TurretProjectile(Vec2 position, Vec2 size, Vec2 direction, float speed, AnimationClip animation) {
        super(position, size, direction, speed);
        this.animation = animation;
        this.lifetime = LIFETIME;
    }

    @Override
    public void render(TextureBatch batch) {
        batch.drawAnimation(animation, position, size.x, size.y);
    }
}
