package game.overrun.stage;

public class Stage1 extends Stage {
    private float timer = 0f;

    public Stage1() {
        super(StageConfigs.STAGE_1);
    }

    public void update(float delta) {
        super.update(delta);
        timer += delta;

        for (SpawnPhase phase : super.config.spawnPhases()) {
            if (!phase.spawned && timer >= phase.timerAt) {
                words.addNewEntites(phase.count);
                phase.spawned = true;
            }
        }

        // Kill the player if they exceed the max time for the stage.
        if (timer >= super.config.maxTime() && !deathRewardsGranted) {
            playerManager.hurt(99999);
        }
    }
}
