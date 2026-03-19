package game.overrun.projectiles;

import engine.entities.Collidable;
import engine.entities.CollisionLayer;
import engine.entities.Entity;
import engine.graphics.Color;
import engine.graphics.TextureBatch;
import engine.math.Vec2;

public class Projectile extends Entity {
    Vec2 direction;
    float speed;
    float lifetime = 10f; // Lifetime in seconds
    float damage = 10f; // Damage dealt to enemies

    private float age = 0f; // Age of the projectile in seconds

    public Projectile(Vec2 position, Vec2 size, Vec2 direction, float speed) {
        super(position, size);
        this.direction = direction;
        this.speed = speed;
    }

    public void update(float delta) {
        // Move the projectile in the direction it's facing
        move(direction.mul(speed), delta);

        // Update age and check if it should be destroyed
        age += delta;
        if (age >= lifetime) {
            active = false; // Mark for removal
        }
    }

    public void render(TextureBatch batch) {
        // TODO actual projectile texture
        batch.drawRect(position, size, new Color(1f, 0f, 0f, 1f));
    }

    @Override
    public void onCollision(Collidable other) {
        System.out.println("Projectile collided with " + other);
        active = false;
    }

    @Override
    public int getLayer() {
        return CollisionLayer.PLAYER_PROJECTILE;
    }

    @Override
    public int getMask() {
        return CollisionLayer.ENEMY;
    }
}
