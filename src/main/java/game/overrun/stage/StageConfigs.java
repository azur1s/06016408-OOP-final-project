package game.overrun.stage;

import engine.graphics.AnimationClip;
import engine.graphics.Texture;

public final class StageConfigs {
    private StageConfigs() {
    }

    public static final StageConfig OVERRUN = new StageConfig(
            "bgm_main",
            "textures/background/stage1.png",
            "GeistMono-Regular.otf",
            32,
            new AnimationClip(new Texture[] {
                    new Texture("textures/entities/stage1/e1_1.png"),
                    new Texture("textures/entities/stage1/e1_2.png"),
                    new Texture("textures/entities/stage1/e1_3.png"),
                    new Texture("textures/entities/stage1/e1_4.png"),
            }, 0.2f),
            false);

    public static final StageConfig STAGE_1 = new StageConfig(
            "bgm_main",
            "textures/background/stage1.png",
            "GeistMono-Regular.otf",
            32,
            new AnimationClip(new Texture[] {
                    new Texture("textures/entities/stage1/e1_1.png"),
                    new Texture("textures/entities/stage1/e1_2.png"),
                    new Texture("textures/entities/stage1/e1_3.png"),
                    new Texture("textures/entities/stage1/e1_4.png"),
            }, 0.2f),
            true);

    public static final StageConfig STAGE_2 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s2.png",
            "GeistMono-Regular.otf",
            32,
            new AnimationClip(new Texture[] {
                    new Texture("textures/entities/stage1/e1_1.png"),
                    new Texture("textures/entities/stage1/e1_2.png"),
                    new Texture("textures/entities/stage1/e1_3.png"),
                    new Texture("textures/entities/stage1/e1_4.png"),
            }, 0.2f),
            true);

    public static final StageConfig STAGE_3 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s3.png",
            "GeistMono-Regular.otf",
            32,
            new AnimationClip(new Texture[] {
                    new Texture("textures/entities/stage1/e1_1.png"),
                    new Texture("textures/entities/stage1/e1_2.png"),
                    new Texture("textures/entities/stage1/e1_3.png"),
                    new Texture("textures/entities/stage1/e1_4.png"),
            }, 0.2f),
            true);

    public static final StageConfig STAGE_4 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s4.png",
            "GeistMono-Regular.otf",
            32,
            new AnimationClip(new Texture[] {
                    new Texture("textures/entities/stage1/e1_1.png"),
                    new Texture("textures/entities/stage1/e1_2.png"),
                    new Texture("textures/entities/stage1/e1_3.png"),
                    new Texture("textures/entities/stage1/e1_4.png"),
            }, 0.2f),
            true);

    public static String[] getPlayerTexturePaths() {
        return new String[] {
                "textures/player_1.png",
                "textures/player_0.png",
                "textures/player_3.png"
        };
    }

    public static String getStageBackgroundPaths() {
        return "textures/background/stagelocked/allLocked.jpg";
    }

    public static String[] getStageButtonPaths() {
        return new String[] {
                "textures/stage1.png",
                "textures/stage2.png",
                "textures/stage3.png",
                "textures/stage4.png",
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