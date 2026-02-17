package com.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.project.words.WordEntitiesListener;
import com.project.words.WordEntity;

public class ParticlesManager implements WordEntitiesListener, Disposable {
    private final ParticleEffect template;
    private final ParticleEffectPool pool;
    private final Array<PooledEffect> activeEffects;

    public ParticlesManager(String effectPath, String imagesDir) {
        template = new ParticleEffect();
        template.load(
                Gdx.files.internal(effectPath),
                Gdx.files.internal(imagesDir));
        for (ParticleEmitter emitter : template.getEmitters()) {
            emitter.setContinuous(false);
        }
        pool = new ParticleEffectPool(template, 8, 64);
        activeEffects = new Array<>();
    }

    @Override
    public void onWordCompleted(WordEntity wordEntity) {
        spawnParticle(wordEntity);
    }

    @Override
    public void onWordMissed(WordEntity wordEntity) {
        // spawnParticle(wordEntity);
    }

    public void updateAndRender(SpriteBatch batch, float delta) {
        if (activeEffects.isEmpty()) {
            return;
        }

        for (int i = activeEffects.size - 1; i >= 0; i--) {
            PooledEffect effect = activeEffects.get(i);
            effect.update(delta);
            effect.draw(batch);
            if (effect.isComplete()) {
                effect.free();
                activeEffects.removeIndex(i);
            }
        }
    }

    private void spawnParticle(WordEntity wordEntity) {
        if (wordEntity == null) {
            return;
        }

        PooledEffect effect = pool.obtain();
        effect.setPosition(wordEntity.position.x, wordEntity.position.y);
        effect.reset();
        effect.start();
        activeEffects.add(effect);
    }

    @Override
    public void dispose() {
        for (PooledEffect effect : activeEffects) {
            effect.free();
        }
        activeEffects.clear();
        template.dispose();
    }
}
