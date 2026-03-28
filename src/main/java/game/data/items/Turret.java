package game.data.items;

import engine.Engine;
import engine.graphics.AnimationClip;
import engine.graphics.Texture;
import engine.graphics.TextureBatch;
import engine.math.Vec2;
import game.data.Item;
import game.overrun.projectiles.TurretProjectile;
import game.overrun.stage.Stage;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

public class Turret extends Item {
    private static final Texture TURRET_TEXTURE = new Texture("textures/items/turret.png");
    private static final AnimationClip TURRET_PROJECTILES = new AnimationClip(new Texture[] {
            new Texture("textures/items/turret_projectile_1.png"),
            new Texture("textures/items/turret_projectile_2.png"),
    }, 0.1f);
    private static final Texture ICON_TEXTURE = new Texture("textures/items/icon_turret.png");

    private static final Vec2 PROJECTILE_SIZE = new Vec2(24f, 24f);
    private static final float TURRET_SIZE = 84f;
    private static final float TURRET_OFFSET_X = 140f;
    private static final float PROJECTILE_SPEED = 900f;

    private Vec2 turretPosition;
    private int turretLane = 0;

    public Turret() {
        this.name = "Turret";
        this.description = "Summon a turret that automatically shoots at enemies.";
        this.icon = ICON_TEXTURE;
    }

    @Override
    public void activate(Stage stage) {
        this.setTimer();
        this.active = true;

        turretLane = findBestLane(stage);
        turretPosition = new Vec2(
                stage.playerManager.getPosition().x + TURRET_OFFSET_X,
                (turretLane - 2) * WordEntity.LANE_SPACING + WordEntity.LANE_Y_OFFSET);

        float duration = super.activeTime;
        float fireInterval = 1f;
        int shotCount = Math.max(1, (int) Math.ceil(duration / fireInterval));

        for (int i = 0; i < shotCount; i++) {
            float delay = i * fireInterval;
            Engine.runAfter(() -> {
                if (!this.active || turretPosition == null) {
                    return;
                }

                Vec2 direction = findLaneTargetDirection(stage);
                Vec2 spawnPosition = turretPosition.add(new Vec2(TURRET_SIZE * 0.45f, 0f));

                stage.projectiles.spawnProjectile(new TurretProjectile(
                        spawnPosition,
                        PROJECTILE_SIZE,
                        direction,
                        PROJECTILE_SPEED,
                        TURRET_PROJECTILES));
            }, delay);
        }

        Engine.runAfter(() -> {
            this.active = false;
            this.turretPosition = null;
        }, duration);
    }

    @Override
    public void render(Stage stage, TextureBatch batch) {
        if (!active || turretPosition == null) {
            return;
        }

        batch.draw(TURRET_TEXTURE, turretPosition.x, turretPosition.y, TURRET_SIZE, TURRET_SIZE);
    }

    private int findBestLane(Stage stage) {
        int[] counts = new int[WordEntitiesManager.MAX_LANES];
        for (WordEntity word : stage.words.getWordEntities()) {
            if (!word.active) {
                continue;
            }
            if (word.lane >= 0 && word.lane < counts.length) {
                counts[word.lane]++;
            }
        }

        int fallbackLane = stage.playerManager.currentLane;
        if (fallbackLane < 0 || fallbackLane >= counts.length) {
            fallbackLane = 0;
        }

        int bestLane = fallbackLane;
        int bestCount = counts[bestLane];
        for (int lane = 0; lane < counts.length; lane++) {
            if (counts[lane] > bestCount) {
                bestCount = counts[lane];
                bestLane = lane;
            }
        }

        return bestLane;
    }

    private Vec2 findLaneTargetDirection(Stage stage) {
        WordEntity nearest = null;
        float closestDistance = Float.MAX_VALUE;

        for (WordEntity word : stage.words.getWordEntities()) {
            if (!word.active || word.lane != turretLane) {
                continue;
            }

            float distance = word.position.sub(turretPosition).length();
            if (distance < closestDistance) {
                closestDistance = distance;
                nearest = word;
            }
        }

        if (nearest == null) {
            return new Vec2(1f, 0f);
        }

        Vec2 direction = nearest.position.sub(turretPosition);
        if (direction.length() == 0f) {
            return new Vec2(1f, 0f);
        }
        return direction.normalize();
    }
}
