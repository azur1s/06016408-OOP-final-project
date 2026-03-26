package game.overrun.stage;

public final class StageConfigs {
    private StageConfigs() {
    }

    public static StageConfig stage1() {
        return new StageConfig(
                "bgm_main",
                "textures/solid.png",
                "textures/background/stage1.png",
                "textures/button_test.png",
                "GeistMono-Regular.otf",
                32,
                new String[] {
                        "textures/player_1.png",
                        "textures/player_0.png",
                        "textures/player_3.png"
                });
    }
}