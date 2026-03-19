package com.project.scenes.game.projectiles;

import java.util.ArrayList;

import com.project.engine.entities.Collidable;
import com.project.engine.graphics.TextureBatch;
import com.project.engine.math.Vec2;
import com.project.scenes.game.words.WordEntitiesListener;
import com.project.scenes.game.words.WordEntity;

public class ProjectileManager implements WordEntitiesListener {
    ArrayList<Projectile> projectiles = new ArrayList<>();

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

    @Override
    public void onWordCompleted(WordEntity wordEntity) {
        Vec2 spawnPositon = new Vec2(-600, 0);
        Vec2 direction = wordEntity.position.sub(spawnPositon).normalize();

        Projectile p = new Projectile(
                spawnPositon,
                new Vec2(10, 10), // TODO projectile size
                direction,
                1000f);
        spawnProjectile(p);
    };

    @Override
    public void onWordMissed(WordEntity wordEntity) {
    };

    @Override
    public void onWordProgress(WordEntity wordEntity) {
    };
}
