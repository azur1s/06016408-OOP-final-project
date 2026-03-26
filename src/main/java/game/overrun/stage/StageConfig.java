package game.overrun.stage;

public class StageConfig {
    private final String soundToStopOnInit;
    private final String solidTexturePath;
    private final String backgroundTexturePath;
    private final String buttonTexturePath;
    private final String fontPath;
    private final int fontSize;
    private final String[] playerTexturePaths;

    public StageConfig(
            String soundToStopOnInit,
            String solidTexturePath,
            String backgroundTexturePath,
            String buttonTexturePath,
            String fontPath,
            int fontSize,
            String[] playerTexturePaths) {
        this.soundToStopOnInit = soundToStopOnInit;
        this.solidTexturePath = solidTexturePath;
        this.backgroundTexturePath = backgroundTexturePath;
        this.buttonTexturePath = buttonTexturePath;
        this.fontPath = fontPath;
        this.fontSize = fontSize;
        this.playerTexturePaths = playerTexturePaths;
    }

    public String soundToStopOnInit() {
        return soundToStopOnInit;
    }

    public String solidTexturePath() {
        return solidTexturePath;
    }

    public String backgroundTexturePath() {
        return backgroundTexturePath;
    }

    public String buttonTexturePath() {
        return buttonTexturePath;
    }

    public String fontPath() {
        return fontPath;
    }

    public int fontSize() {
        return fontSize;
    }

    public String[] playerTexturePaths() {
        return playerTexturePaths;
    }
}