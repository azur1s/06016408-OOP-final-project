package com.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.project.words.WordEntitiesManager;

public class InputHandler {
    public WordEntitiesManager words;
    public int sessionCharCount = 0; // Keep track of chars for WPM

    public InputHandler(WordEntitiesManager words) {
        this.words = words;
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            words.removeInputChar();
        } else if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                && Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            words.addInputChar('_');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;
        } else {
            for (int i = 0; i < 26; i++) {
                char c = (char) ('a' + i);
                if (Gdx.input.isKeyJustPressed(Input.Keys.valueOf(Character.toUpperCase(c) + ""))) {
                    words.addInputChar(c);
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0 + i)) {
                    words.addInputChar((char) ('0' + i));
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
        }
    }
}
