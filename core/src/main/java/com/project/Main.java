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
    private int wrongCharCount = 0;
    private int lastWpm = 0;
    private int lastAccuracy = 100;

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

        boolean typedCharThisFrame = false;

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            words.removeInputChar();
        } else {
            for (int i = 0; i < 26; i++) {
                char c = (char) ('a' + i);
                if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(Character.toUpperCase(c) + ""))) {
                    words.addInputChar(c);
                    charCount++;
                    typedCharThisFrame = true;
                    break;
                }
            }
        }

        words.checkAndRemoveMatchedWord();

        // count an incorrect character exactly when a newly typed character
        // makes the buffer unmatchable
        if (typedCharThisFrame && !words.isInputBufferMatchable()) {
            wrongCharCount++;
        }

        if (charTimer >= 1.0f) {
            lastWpm = (int) ((charCount / 5.0f) * (60.0f / charTimer));

            if (charCount <= 0) {
                lastAccuracy = 100;
            } else {
                int correctCharCount = Math.max(0, charCount - wrongCharCount);
                lastAccuracy = Math.round((correctCharCount * 100.0f) / charCount);
            }

            charTimer = 0f;
            charCount = 0;
            wrongCharCount = 0;
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

        // draw the input buffer at the bottom left
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(batch, words.inputBuffer, 24, 24);

        // display the WPM at the bottom right
        String wpmText = lastWpm + " WPM";
        String accText = lastAccuracy + "% ACC";
        String hudText = wpmText + "  " + accText;

        gl.setText(font, hudText);
        float wpmWidth = gl.width;
        font.draw(batch, hudText, Gdx.graphics.getWidth() - wpmWidth - 24, 24);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        fonts.dispose();
    }
}
