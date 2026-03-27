package game.overrun.stage;

import engine.graphics.AnimationClip;

public class StageConfig {
    private final String soundToStopOnInit;
    private final String backgroundTexturePath;
    private final String fontPath;
    private final int fontSize;

    private final AnimationClip entityTexture;
    private final boolean manualSpawn;
    private SpawnPhase[] spawnPhases;
    private float maxTime;

    public StageConfig(
            String soundToStopOnInit,
            String backgroundTexturePath,
            String fontPath,
            int fontSize,
            AnimationClip entityTexture) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.backgroundTexturePath = backgroundTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;

        this.entityTexture = entityTexture;
        this.manualSpawn = false;
        // unused
        this.spawnPhases = new SpawnPhase[0];
        this.maxTime = 0f;
    }

    public StageConfig(
            String soundToStopOnInit,
            String backgroundTexturePath,
            String fontPath,
            int fontSize,
            AnimationClip entityTexture,
            SpawnPhase[] spawnPhases,
            float maxTime) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.backgroundTexturePath = backgroundTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;

        this.entityTexture = entityTexture;
        this.manualSpawn = true;
        this.spawnPhases = spawnPhases;
        this.maxTime = maxTime;
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
        return entityTexture;
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
}