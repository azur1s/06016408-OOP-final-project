package game.overrun.stage;

public class SpawnPhase {
    int count;
    int timerAt;
    boolean spawned;

    public SpawnPhase(int count, int timerAt) {
        this.count = count;
        this.timerAt = timerAt;
        this.spawned = false;
    }
}