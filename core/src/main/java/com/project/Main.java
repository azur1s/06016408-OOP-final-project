package com.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.project.words.WordEntitiesManager;
import com.project.words.WordEntity;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private FontsManager fonts;
    private BitmapFont font;
    private GlyphLayout gl;
    private CameraManager cameraManager;

    private WordEntitiesManager words;
    private InputHandler inputHandler;
    private PlayerManager stats;

    // Animation states
    private float displayedHealth;
    private float scoreScale = GameConfig.BASE_SCORE_SCALE;
    private float baseScoreScale = GameConfig.BASE_SCORE_SCALE;

    private ParticlesManager particlesManager;
    private Assets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new FontsManager();
        gl = new GlyphLayout();
        cameraManager = new CameraManager();

        font = fonts.get(GameConfig.MAIN_FONT_PATH, GameConfig.MAIN_FONT_SIZE);

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(GameConfig.INITIAL_WORD_COUNT);

        inputHandler = new InputHandler(words);
        stats = new PlayerManager(words);
        displayedHealth = stats.health;

        words.addListener(stats);
        particlesManager = new ParticlesManager(GameConfig.DEATH_PARTICLE_PATH, GameConfig.PARTICLE_DIRECTORY);
        words.addListener(particlesManager);

        assets = new Assets();
    }

    @Override
    public void render() {
        // --- Update logic ---

        // update word entites
        words.updateAll(Gdx.graphics.getDeltaTime());

        // input handler
        inputHandler.update();

        stats.update(inputHandler.sessionCharCount);
        if (stats.charTimer == 0f)
            inputHandler.sessionCharCount = 0;

        // --- Render logic ---
        ScreenUtils.clear(GameConfig.BACKGROUND_COLOR);
        renderWorld();

        // render UI
        cameraManager.getUIViewport().apply();
        drawHealthBar();
        drawUI();
    }

    private void renderWorld() {
        cameraManager.getWorldViewport().apply();
        batch.setProjectionMatrix(cameraManager.getWorldCamera().combined);
        batch.begin();

        batch.setColor(1f, 1f, 1f, 1f);
        // fill the full height while letting the width adjust to maintain aspect ratio,
        // so it can extend beyond the screen on wider displays without showing black
        // bars
        float aspectRatio = (float) assets.background.getWidth() / assets.background.getHeight();
        float bgWidth = GameConfig.WORLD_HEIGHT * aspectRatio;
        batch.draw(assets.background,
                0, 0,
                bgWidth,
                GameConfig.WORLD_HEIGHT);

        // draw checkerboard background for lanes
        drawLanesBackground(batch);

        // draw a shooter at the left edge of the screen, centered on the current lane
        batch.setColor(1f, 1f, 1f, 1f);
        float shooterX = 50f;
        float shooterY = GameConfig.LANE_Y_CENTER + (stats.currentLane - 2) * WordEntity.LANE_SPACING
                - 16f;
        batch.draw(assets.shooter,
                shooterX, shooterY,
                WordEntity.LANE_HEIGHT, WordEntity.LANE_HEIGHT);

        // draw word entites
        words.renderAll(batch, font, gl);

        particlesManager.updateAndRender(batch, Gdx.graphics.getDeltaTime());

        batch.end();
    }

    private void drawLanesBackground(SpriteBatch batch) {
        float laneHeight = WordEntity.LANE_HEIGHT;
        float laneSpacing = WordEntity.LANE_SPACING;
        float yCenter = GameConfig.LANE_Y_CENTER;

        for (int lane = 0; lane < GameConfig.LANE_COUNT; lane++) {
            float yLane = (lane - 2) * laneSpacing;
            batch.setColor(lane % 2 == 0
                    ? GameConfig.LANE_COLOR_EVEN
                    : GameConfig.LANE_COLOR_ODD);
            batch.draw(assets.solid, 0f, yCenter + yLane - laneHeight / 2f, 1500f, laneHeight);
        }

        // draw vertical line at x=100 to indicate fail zone
        batch.setColor(GameConfig.FAIL_ZONE_COLOR);
        batch.draw(assets.solid, GameConfig.FAIL_ZONE_X, 0f, 2f, GameConfig.WORLD_HEIGHT);
    }

    private void drawHealthBar() {
        // draw a health bar at the bottom part of the screen that span the entire width
        float health = stats.health;
        float barWidth = cameraManager.getUIViewport().getWorldWidth() - GameConfig.HEALTH_BAR_TOTAL_PADDING;
        float barHeight = GameConfig.HEALTH_BAR_HEIGHT;

        // animate health bar changes with lerp
        displayedHealth += (health - displayedHealth) * GameConfig.HEALTH_LERP_SPEED * Gdx.graphics.getDeltaTime();

        batch.setProjectionMatrix(cameraManager.getUICamera().combined);
        batch.begin();

        batch.setColor(GameConfig.HEALTH_BAR_BG_COLOR);
        batch.draw(assets.solid, GameConfig.UI_PADDING, 0f, barWidth, barHeight);
        batch.setColor(GameConfig.HEALTH_BAR_FILL_COLOR);
        batch.draw(assets.solid, GameConfig.UI_PADDING, 0f, barWidth * (displayedHealth / stats.maxHealth), barHeight);

        batch.end();
    }

    private void drawUI() {
        batch.setProjectionMatrix(cameraManager.getUICamera().combined);
        batch.begin();

        // display scores at the middle bottom of the screen, adjust size based on
        // streaks (max 100% size increase at 10+ streaks), decay with timer
        float streakBonus = Math.min(stats.streaks, GameConfig.MAX_STREAK_FOR_BONUS)
                / (float) GameConfig.MAX_STREAK_FOR_BONUS;
        float timerFactor = 1f
                - Math.min(stats.streaksTimer, GameConfig.STREAK_DECAY_TIME) / GameConfig.STREAK_DECAY_TIME;
        float targetScoreScale = baseScoreScale + streakBonus * Math.max(timerFactor, 0f);
        scoreScale += (targetScoreScale - scoreScale) * GameConfig.SCORE_SCALE_LERP_SPEED * Gdx.graphics.getDeltaTime();
        font.getData().setScale(scoreScale);

        String scoreText = String.valueOf(stats.score);
        gl.setText(font, scoreText);
        font.draw(batch, scoreText,
                cameraManager.getUIViewport().getWorldWidth() / 2f - gl.width / 2f,
                gl.height + GameConfig.UI_PADDING);

        font.getData().setScale(1f); // reset scale for other UI elements

        String wpmText = stats.lastWpm + " WPM";
        gl.setText(font, wpmText);
        font.draw(batch, wpmText,
                cameraManager.getUIViewport().getWorldWidth() - gl.width - GameConfig.UI_PADDING,
                gl.height + GameConfig.UI_PADDING);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
        if (assets != null) {
            assets.dispose();
        }
        if (particlesManager != null) {
            particlesManager.dispose();
        }
    }
}