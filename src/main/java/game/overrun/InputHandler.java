package game.overrun;

import static org.lwjgl.glfw.GLFW.*;

import engine.Engine;
import game.overrun.words.WordEntitiesManager;

public class InputHandler {
    // Reference to Words for adding input chars and checking matches
    public WordEntitiesManager words;
    public int sessionCharCount = 0;

    public InputHandler(WordEntitiesManager words) {
        this.words = words;
    }

    public void update() {
        if (Engine.input.isKeyPressed(GLFW_KEY_TAB)) {
            words.addNewEntites(1);

        } else if (Engine.input.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            words.removeInputChar();

        } else if (Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                && Engine.input.isKeyPressed(GLFW_KEY_MINUS)) {
            words.addInputChar('_');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else {
            for (int i = 0; i < 26; i++) {
                int c = GLFW_KEY_A + i;
                if (Engine.input.isKeyPressed(c)) {
                    words.addInputChar((char) ('a' + i));
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
            for (int i = 0; i < 10; i++) {
                if (Engine.input.isKeyPressed(GLFW_KEY_0 + i)) {
                    words.addInputChar((char) ('0' + i));
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
        }
    }
}
