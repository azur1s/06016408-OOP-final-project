package com.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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
    private ShapeRenderer shapeRenderer;

    OrthographicCamera camera;
    ExtendViewport viewport;

    OrthographicCamera uiCamera;
    ExtendViewport uiViewport;

    private WordEntitiesManager words;
    private InputHandler inputHandler;
    private StatsManager stats;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new FontsManager();
        gl = new GlyphLayout();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1000, 600, camera);

        uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), uiCamera);

        font = fonts.get("fonts/monogram-extended.ttf", 32);

        words = new WordEntitiesManager();
        words.init();
        words.addNewEntites(10);

        inputHandler = new InputHandler(words);
        stats = new StatsManager(words);
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
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        renderWorld();

        // render UI
        renderUI();
    }

    private void renderWorld() {
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        // draw checkerboard background for lanes
        drawLanesBackground();

        batch.begin();

        // draw word entites
        words.renderAll(batch, font);

        batch.end();
    }

    private void drawLanesBackground() {
        float laneHeight = WordEntity.LANE_HEIGHT;
        float laneSpacing = WordEntity.LANE_SPACING;
        float yCenter = 300f;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int lane = 0; lane < 5; lane++) {
            float yLane = (lane - 2) * laneSpacing;
            shapeRenderer.setColor(lane % 2 == 0
                    ? new Color(0.2f, 0.2f, 0.25f, 1f)
                    : new Color(0.15f, 0.15f, 0.2f, 1f));
            shapeRenderer.rect(
                    -1000f, // start off screen to the left for smooth entry
                    yCenter + yLane - laneHeight / 2f,
                    3000f, // make wide enough to cover entire screen and allow for movement
                    laneHeight);
        }

        shapeRenderer.end();
    }

    private void renderUI() {
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        String wpmText = stats.lastWpm + " WPM";
        gl.setText(font, wpmText);
        font.draw(batch, wpmText, uiViewport.getWorldWidth() - gl.width - 24, 24);

        // debugView();

        batch.end();
    }

    private void debugView() {
        String fpsText = Gdx.graphics.getFramesPerSecond() + " FPS";
        gl.setText(font, fpsText);
        font.draw(batch, fpsText, uiViewport.getWorldWidth() - gl.width - 24, uiViewport.getWorldHeight() - 24);
        font.draw(batch, words.inputBuffer, 24, 24);

        // Draw mouse world position coordinates
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();

        // Convert screen coordinates to world coordinates
        Vector3 worldPos = new Vector3(mouseX, mouseY, 0);
        camera.unproject(worldPos);

        String coordText = String.format("%.1f, %.1f", worldPos.x, worldPos.y);
        gl.setText(font, coordText);

        font.draw(batch, coordText, 24, uiViewport.getWorldHeight() - 24);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        fonts.dispose();
    }
}