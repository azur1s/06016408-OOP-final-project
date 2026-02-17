package com.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Fonts fonts;
    private BitmapFont font;
    private GlyphLayout gl;

    private Words words;
    private InputHandler inputHandler;
    private StatsManager stats;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new Fonts();
        gl = new GlyphLayout();

        font = fonts.get("fonts/monogram-extended-italic.ttf", 32);

        words = new Words();
        Words.init();
        words.generateWordList(10);

        inputHandler = new InputHandler(words);
        stats = new StatsManager();
    }

    @Override
    public void render() {

        // input handler
        inputHandler.update();

        stats.update(inputHandler.sessionCharCount);
        if (stats.charTimer == 0f)
            inputHandler.sessionCharCount = 0;

        // render word
        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);
        batch.begin();

        // draw word
        int y = Gdx.graphics.getHeight() - 24;
        for (Pair<String, String> word : words.getCurrentWordListHighlighted()) {
            font.setColor(1f, 1f, 1f, 0.5f);
            font.draw(batch, word.left + word.right, 24, y);
            font.setColor(1f, 1f, 1f, 1f);
            font.draw(batch, word.left, 24, y);

            y -= 36;
        }

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
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
    }
}
