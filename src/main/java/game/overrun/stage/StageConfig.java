package game.overrun.stage;

import engine.graphics.AnimationClip;
import engine.graphics.Texture;
import game.overrun.words.WordEntity;

/**
 * Immutable stage metadata plus lazily constructed runtime assets.
 *
 * Entity texture paths are stored as strings so stage configs stay cheap to
 * load.
 * The animation clip is created on first use, which avoids eager texture
 * creation
 * during class initialization.
 */
public class StageConfig {
    private final String soundToStopOnInit;
    private final String backgroundTexturePath;
    private final String fontPath;
    private final int fontSize;

    private final String[] entityTexturePaths;
    // Lazily created on first call to entityTexture().
    private AnimationClip entityTexture;
    private final boolean manualSpawn;
    private SpawnPhase[] spawnPhases;
    private float maxTime;

    private WordEntity bossWordEntity;

    // Overrun
    public StageConfig(
            String soundToStopOnInit,
            String backgroundTexturePath,
            String fontPath,
            int fontSize,
            String[] entityTexturePaths) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.backgroundTexturePath = backgroundTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;

        this.entityTexturePaths = entityTexturePaths;
        this.manualSpawn = false;
        // unused
        this.spawnPhases = new SpawnPhase[0];
        this.maxTime = 0f;
        this.bossWordEntity = null;
    }

    // Manual spawn stages
    public StageConfig(
            String soundToStopOnInit,
            String backgroundTexturePath,
            String fontPath,
            int fontSize,
            String[] entityTexturePaths,
            SpawnPhase[] spawnPhases,
            float maxTime,
            WordEntity bossWordEntity) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.backgroundTexturePath = backgroundTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;

        this.entityTexturePaths = entityTexturePaths;
        this.manualSpawn = true;
        this.spawnPhases = spawnPhases;
        this.maxTime = maxTime;
        this.bossWordEntity = bossWordEntity;
    }

    public String soundToStopOnInit() {
        return soundToStopOnInit;
    }

    public String backgroundTexturePath() {
        return backgroundTexturePath;
    }

    public String fontPath() {
        return fontPath;
    }

    public int fontSize() {
        return fontSize;
    }

    public AnimationClip entityTexture() {
        if (entityTexture == null) {
            // Delay Texture creation until the stage is actually entered.
            Texture[] frames = new Texture[entityTexturePaths.length];
            for (int i = 0; i < entityTexturePaths.length; i++) {
                frames[i] = new Texture(entityTexturePaths[i]);
            }
            entityTexture = new AnimationClip(frames, 0.2f);
        }
        return entityTexture;
    }

    public String[] entityTexturePaths() {
        return entityTexturePaths;
    }

    public boolean manualSpawn() {
        return manualSpawn;
    }

    public SpawnPhase[] spawnPhases() {
        return spawnPhases;
    }

    public float maxTime() {
        return maxTime;
    }

    public WordEntity bossWordEntity() {
        return bossWordEntity;
    }
}