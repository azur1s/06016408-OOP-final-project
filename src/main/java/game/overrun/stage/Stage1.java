package game.overrun.stage;

public class Stage1 extends Stage {
	private float timer = 0f;

	private class SpawnPhase {
		int count;
		int timerAt;
		boolean spawned;

		public SpawnPhase(int count, int timerAt) {
			this.count = count;
			this.timerAt = timerAt;
			this.spawned = false;
		}
	}

	SpawnPhase[] spawnPhases = new SpawnPhase[] {
			new SpawnPhase(1, 1),
			new SpawnPhase(2, 10),
			new SpawnPhase(3, 20)
	};

	public Stage1() {
		super(StageConfigs.STAGE_1);
	}

	public void update(float delta) {
		super.update(delta);
		timer += delta;

		for (SpawnPhase phase : spawnPhases) {
			if (!phase.spawned && timer >= phase.timerAt) {
				words.addNewEntites(phase.count);
				phase.spawned = true;
			}
		}
	}
}
