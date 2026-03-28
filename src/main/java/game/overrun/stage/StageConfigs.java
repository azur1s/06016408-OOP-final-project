package game.overrun.stage;

import engine.graphics.AnimationClip;
import engine.graphics.Texture;
import game.overrun.words.WordEffect;
import game.overrun.words.WordEntity;

public final class StageConfigs {
    //
    private StageConfigs() {
    }

    private static final String[][] ENEMY_STAGE_1_TEXTURE_PATHS = {
            {
                    "textures/entities/stage1/Peter/Peter_1.png",
                    "textures/entities/stage1/Peter/Peter_2.png",
                    "textures/entities/stage1/Peter/Peter_3.png",
                    "textures/entities/stage1/Peter/Peter_4.png",
            },
            {
                    "textures/entities/stage1/John/John_1.png",
                    "textures/entities/stage1/John/John_2.png",
                    "textures/entities/stage1/John/John_3.png",
                    "textures/entities/stage1/John/John_4.png",
            },
            {
                    "textures/entities/stage1/King/King_1.png",
                    "textures/entities/stage1/King/King_2.png",
                    "textures/entities/stage1/King/King_3.png",
                    "textures/entities/stage1/King/King_4.png",
            }
    };

    private static final String[][] ENEMY_STAGE_2_TEXTURE_PATHS = {
            {
                    "textures/entities/stage2/Dark_Red/Dark_Red_0.png",
                    "textures/entities/stage2/Dark_Red/Dark_Red_1.png",
                    "textures/entities/stage2/Dark_Red/Dark_Red_2.png",
            },
            {
                    "textures/entities/stage2/Blue/Blue_0.png",
                    "textures/entities/stage2/Blue/Blue_1.png",
                    "textures/entities/stage2/Blue/Blue_2.png",
            },
            {
                    "textures/entities/stage2/Black/Black_0.png",
                    "textures/entities/stage2/Black/Black_1.png",
                    "textures/entities/stage2/Black/Black_2.png",
            },
            {
                    "textures/entities/stage2/Red/Red_0.png",
                    "textures/entities/stage2/Red/Red_1.png",
                    "textures/entities/stage2/Red/Red_2.png",
            },
    };

    private static final String[][] ENEMY_STAGE_3_TEXTURE_PATHS = {
            {
                    "textures/entities/stage3/bukdog/bukdog_0.png",
                    "textures/entities/stage3/bukdog/bukdog_1.png",
                    "textures/entities/stage3/bukdog/bukdog_2.png",
            },
            {
                    "textures/entities/stage3/bukyern/bukyern_0.png",
                    "textures/entities/stage3/bukyern/bukyern_1.png",
                    "textures/entities/stage3/bukyern/bukyern_2.png",
            },
            {
                    "textures/entities/stage3/buksas/buksas_0.png",
                    "textures/entities/stage3/buksas/buksas_1.png",
                    "textures/entities/stage3/buksas/buksas_2.png",
            }
    };

    private static final String[][] ENEMY_STAGE_4_TEXTURE_PATHS = {
            {
                    "textures/entities/stage4/medcrazy/medcrazy_0.png",
                    "textures/entities/stage4/medcrazy/medcrazy_1.png",
                    "textures/entities/stage4/medcrazy/medcrazy_2.png",
            },
            {
                    "textures/entities/stage4/medgreen/medgreen_0.png",
                    "textures/entities/stage4/medgreen/medgreen_1.png",
                    "textures/entities/stage4/medgreen/medgreen_2.png",
            },
            {
                    "textures/entities/stage4/medpurple/medpurple_0.png",
                    "textures/entities/stage4/medpurple/medpurple_1.png",
                    "textures/entities/stage4/medpurple/medpurple_2.png",
            },
            {
                    "textures/entities/stage4/medice/medice_0.png",
                    "textures/entities/stage4/medice/medice_1.png",
                    "textures/entities/stage4/medice/medice_2.png",
            }
    };

    public static final StageConfig OVERRUN = new StageConfig(
            "bgm_main",
            "textures/background/stage1.png",
            "GeistMono-Regular.otf",
            32,
            ENEMY_STAGE_1_TEXTURE_PATHS);

    public static final StageConfig STAGE_1 = new StageConfig(
            "bgm_main",
            "textures/background/stage1.png",
            "GeistMono-Regular.otf",
            32,
            ENEMY_STAGE_1_TEXTURE_PATHS,
            new SpawnPhase[] {
                    new SpawnPhase(1, 1),
                    new SpawnPhase(3, 10),
                    new SpawnPhase(4, 20)
            },
            60f * 2f,
            new WordEntity(
                    new AnimationClip(new Texture[] {
                            new Texture("textures/entities/stage1/Peter/Peter_1.png"),
                            new Texture("textures/entities/stage1/Peter/Peter_2.png"),
                            new Texture("textures/entities/stage1/Peter/Peter_3.png"),
                            new Texture("textures/entities/stage1/Peter/Peter_4.png"),
                    }, 0.2f),
                    "public static void main(String[] args)",
                    650f,
                    10f,
                    2,
                    new WordEffect.Boss()));

    public static final StageConfig STAGE_2 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s2.png",
            "GeistMono-Regular.otf",
            32,
            ENEMY_STAGE_2_TEXTURE_PATHS,
            new SpawnPhase[] {
                    new SpawnPhase(2, 1),
                    new SpawnPhase(4, 10),
                    new SpawnPhase(4, 20),
                    new SpawnPhase(4, 25)
            },
            60f * 3f,
            new WordEntity(
                    new AnimationClip(new Texture[] {
                            new Texture("textures/entities/stage1/Peter/Peter_1.png"),
                    }, 0.2f),
                    "public class Airplane extends Vehicle implements Flyable",
                    650f,
                    10f,
                    2,
                    new WordEffect.Boss()));

    public static final StageConfig STAGE_3 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s3.png",
            "GeistMono-Regular.otf",
            32,
            ENEMY_STAGE_3_TEXTURE_PATHS,
            new SpawnPhase[] {
                    new SpawnPhase(3, 1),
                    new SpawnPhase(4, 10),
                    new SpawnPhase(4, 20),
                    new SpawnPhase(4, 25),
                    new SpawnPhase(4, 30)
            },
            60f * 4f,
            new WordEntity(
                    new AnimationClip(new Texture[] {
                            new Texture("textures/entities/stage1/Peter/Peter_1.png"),
                    }, 0.2f),
                    "for (int i = 0; i < 10; i++) { System.out.println(i); }",
                    650f,
                    10f,
                    2,
                    new WordEffect.Boss()));

    public static final StageConfig STAGE_4 = new StageConfig(
            "bgm_main",
            "textures/background/bg_s4.png",
            "GeistMono-Regular.otf",
            32,
            ENEMY_STAGE_4_TEXTURE_PATHS,
            new SpawnPhase[] {
                    new SpawnPhase(4, 1),
                    new SpawnPhase(4, 10),
                    new SpawnPhase(4, 20),
                    new SpawnPhase(4, 25),
                    new SpawnPhase(4, 30),
                    new SpawnPhase(4, 40)
            },
            60f * 5f,
            new WordEntity(
                    new AnimationClip(new Texture[] {
                            new Texture("textures/entities/stage4/boss/boss_0.png"),
                            new Texture("textures/entities/stage4/boss/boss_1.png"),
                            new Texture("textures/entities/stage4/boss/boss_2.png"),
                            new Texture("textures/entities/stage4/boss/boss_3.png"),
                            new Texture("textures/entities/stage4/boss/boss_4.png"),
                    }, 0.2f),
                    "new BufferedReader(new InputStreamReader(System.in)).readLine()",
                    650f,
                    10f,
                    2,
                    new WordEffect.Boss()));

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