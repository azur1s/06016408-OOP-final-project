package com.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.project.words.WordEntitiesManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private FontsManager fonts;
    private BitmapFont font;
    private GlyphLayout gl;

    private WordEntitiesManager words;
    private InputHandler inputHandler;
    private StatsManager stats;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new FontsManager();
        gl = new GlyphLayout();

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

        // render word
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);
        batch.begin();

        // draw word entites
        words.renderAll(batch, font);

        // render UI
        renderUI();

        batch.end();
    }

    private void renderUI() {
        String wpmText = stats.lastWpm + " WPM";
        gl.setText(font, wpmText);
        font.draw(batch, wpmText, Gdx.graphics.getWidth() - gl.width - 24, 24);

        String fpsText = Gdx.graphics.getFramesPerSecond() + " FPS";
        gl.setText(font, fpsText);
        font.draw(batch, fpsText, Gdx.graphics.getWidth() - gl.width - 24, Gdx.graphics.getHeight() - 24);

        font.draw(batch, words.inputBuffer, 24, 24);
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
    }
}
