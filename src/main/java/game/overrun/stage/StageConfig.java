package game.overrun.stage;

public class StageConfig {
    private final String soundToStopOnInit;
    private final String backgroundTexturePath;
    private final String fontPath;
    private final int fontSize;

    public StageConfig(
            String soundToStopOnInit,
            String backgroundTexturePath,
            String fontPath,
            int fontSize) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.backgroundTexturePath = backgroundTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;
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
}