package game.overrun.projectiles;

import java.util.ArrayList;

import engine.entities.Collidable;
import engine.graphics.AnimationClip;
import engine.graphics.TextureBatch;
import engine.math.Vec2;
import game.overrun.PlayerManager;
import game.overrun.words.WordEntitiesListener;
import game.overrun.words.WordEntity;

public class ProjectileManager implements WordEntitiesListener {
    private static final Vec2 PLAYER_PROJECTILE_SIZE = new Vec2(68f, 68f);
    private static final float PLAYER_PROJECTILE_SPEED = 1000f;

    // Reference to Player for getting player position to spawn projectile
    public PlayerManager playerManager;
    public AnimationClip playerShotAnimation;
    // List of active projectiles
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public void spawnProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    public void update(float delta) {
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile p = projectiles.get(i);
            p.update(delta);
        }
    }

    public void render(TextureBatch batch) {
        for (Projectile p : projectiles) {
            p.render(batch);
        }
    }

    public void removeInactive() {
        projectiles.removeIf(p -> !p.active);
    }

    public ArrayList<Collidable> getCollidables() {
        ArrayList<Collidable> collidables = new ArrayList<>();
        for (Projectile p : projectiles) {
            collidables.add(p);
        }
        return collidables;
    }

    public void cleanup() {
        projectiles.clear();
    }

    @Override
    public void onWordCompleted(WordEntity wordEntity) {
        Vec2 spawnPositon = playerManager.getPosition().copy();
        Vec2 direction = wordEntity.position.sub(spawnPositon).normalize();

        Projectile p = new CharacterShotProjectile(
                spawnPositon,
                PLAYER_PROJECTILE_SIZE.copy(),
                direction,
                PLAYER_PROJECTILE_SPEED,
                playerShotAnimation);
        spawnProjectile(p);
    };

    @Override
    public void onWordMissed(WordEntity wordEntity) {
    };

    @Override
    public void onWordProgress(WordEntity wordEntity) {
    };
}
