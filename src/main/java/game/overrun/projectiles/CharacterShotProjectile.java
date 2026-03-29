package game.overrun.projectiles;

import engine.graphics.AnimationClip;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

public class CharacterShotProjectile extends Projectile {
    private final AnimationClip animation;

    public CharacterShotProjectile(Vec2 position, Vec2 size, Vec2 direction, float speed, AnimationClip animation) {
        super(position, size, direction, speed);
        this.animation = animation;
        this.lifetime = 1.2f;
    }

    @Override
    public void render(TextureBatch batch) {
        if (animation == null) {
            super.render(batch);
            return;
        }

        batch.drawAnimation(animation, position, size.x, size.y);
    }
}
