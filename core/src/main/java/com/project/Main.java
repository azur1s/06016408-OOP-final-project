package com.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Fonts fonts;
    private BitmapFont font;
    private GlyphLayout gl;

    private Words words;

    private float charTimer = 0f;
    private int charCount = 0;
    private int lastWpm = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fonts = new Fonts();
        gl = new GlyphLayout();

        font = fonts.get("fonts/monogram-extended-italic.ttf", 32);

        words = new Words();
        Words.init();
        words.generateWordList(10);
    }

    @Override
    public void render() {
        charTimer += Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            words.removeInputChar();
        } else {
            for (int i = 0; i < 26; i++) {
                char c = (char) ('a' + i);
                if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(Character.toUpperCase(c) + ""))) {
                    words.addInputChar(c);
                    words.checkAndRemoveMatchedWord();
                    charCount++;
                    break;
                }
            }
        }

        if (charTimer >= 1.0f) {
            lastWpm = (int) ((charCount / 5.0f) * (60.0f / charTimer));

            charTimer = 0f;
            charCount = 0;
        }

        ScreenUtils.clear(0.0f, 0.0f, 0.0f, 1f);
        batch.begin();

        int y = Gdx.graphics.getHeight() - 24;
        for (Pair<String, String> word : words.getCurrentWordListHighlighted()) {
            font.setColor(1f, 1f, 1f, 0.5f);
            font.draw(batch, word.left + word.right, 24, y);
            font.setColor(1f, 1f, 1f, 1f);
            font.draw(batch, word.left, 24, y);

            y -= 36;
        }

        String wpmText = lastWpm + " WPM";

        gl.setText(font, wpmText);
        font.draw(batch, wpmText, Gdx.graphics.getWidth() - gl.width - 24, 24);

        String fpsText = Gdx.graphics.getFramesPerSecond() + " FPS";

        gl.setText(font, fpsText);
        font.draw(batch, fpsText, Gdx.graphics.getWidth() - gl.width - 24, Gdx.graphics.getHeight() - 24);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
    }
}
