package game.overrun;

import engine.math.Vec2;
import game.overrun.words.WordEffect;
import game.overrun.words.WordEntitiesListener;
import game.overrun.words.WordEntitiesManager;
import game.overrun.words.WordEntity;

public class PlayerManager implements WordEntitiesListener {
    // Reference to Words for average word length
    private WordEntitiesManager words;
    private Vec2 playerPosition = new Vec2(-500f, -5f);

    public int maxHealth = 100;
    public int health = maxHealth;
    public int score = 0;

    public int streaks = 0;
    public float streaksTimer = 0f;

    public float charTimer = 0f;
    public int lastWpm = 0;

    public int currentLane = 1;

    public PlayerManager(WordEntitiesManager words) {
        this.words = words;
    }

    public void update(float delta, int charCount) {
        streaksTimer += delta;

        if (streaksTimer >= 3.0f) {
            streaks = 0;
            streaksTimer = 0f;
        }

        charTimer += delta;

        if (charTimer >= 1.0f) {
            lastWpm = (int) ((charCount / words.averageWordLength) * (60.0f / charTimer));
            charTimer = 0f;
        }
    }

    public void hurt(int damage) {
        health -= damage;
        if (health < 0)
            health = 0;
    }

    public void addScore(int points) {
        score += points;
    }

    public Vec2 getPosition() {
        return playerPosition;
    }

    @Override
    public void onWordCompleted(WordEntity wordEntity) {
        currentLane = wordEntity.lane;
        // TODO lerp
        playerPosition.set(playerPosition.x, wordEntity.position.y);

        // float streakMultiplier = 1 + (streaks * 0.1f) + (-(streaksTimer - 3) *
        // 0.05f);

        // addScore((int) (10 * wordEntity.word.length()
        // * WordEffect.getScoreMultipler(wordEntity.effect)
        // * streakMultiplier));
        // streaks++;
        // streaksTimer = 0f;
    }

    @Override
    public void onWordMissed(WordEntity wordEntity) {
        hurt((int) (wordEntity.word.length()
                * WordEffect.getDamageMultipler(wordEntity.effect)));
        streaks = 0;
        streaksTimer = 0f;
    }

    @Override
    public void onWordProgress(WordEntity wordEntity) {
        // currentLane = wordEntity.lane;
    }
}