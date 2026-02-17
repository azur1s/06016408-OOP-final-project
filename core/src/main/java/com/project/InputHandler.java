package com.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputHandler {
    private Words words;
    public int sessionCharCount = 0; // Keep track of chars for WPM

    public InputHandler(Words words) {
        this.words = words;
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            words.removeInputChar();
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
        }
    }
}
