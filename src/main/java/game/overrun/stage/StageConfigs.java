package game.overrun.stage;

public final class StageConfigs {
    private StageConfigs() {
    }

    public static final StageConfig STAGE_1 = new StageConfig(
            "bgm_main",
            "textures/background/stage1.png",
            "GeistMono-Regular.otf",
            32);

    public static final StageConfig STAGE_2 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s2.png",
            "GeistMono-Regular.otf",
            32);

    public static final StageConfig STAGE_3 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s3.png",
            "GeistMono-Regular.otf",
            32);

    public static final StageConfig STAGE_4 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s4.png",
            "GeistMono-Regular.otf",
            32);

    public static String[] getPlayerTexturePaths() {
        return new String[] {
                "textures/player_1.png",
                "textures/player_0.png",
                "textures/player_3.png"
        };
    }

    public static String getSolidTexturePath() {
        return "textures/solid.png";
    }

    public static String getButtonTexturePath() {
        return "textures/button_test.png";
    }

    public static StageConfig getStageConfig(int stageIndex) {
        return switch (stageIndex) {
            case 0 -> STAGE_1;
            case 1 -> STAGE_2;
            case 2 -> STAGE_3;
            case 3 -> STAGE_4;
            default -> STAGE_1;
        };
    }

    public static String getStageName(int stageIndex) {
        return switch (stageIndex) {
            case 0 -> "Stage 1";
            case 1 -> "Stage 2";
            case 2 -> "Stage 3";
            case 3 -> "Stage 4";
            default -> "Unknown";
        };
    }
}