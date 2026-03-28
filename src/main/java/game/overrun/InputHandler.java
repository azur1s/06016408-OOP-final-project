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

        } else if (Engine.input.isKeyPressed(GLFW_KEY_SPACE)) {
            words.addInputChar(' ');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_SEMICOLON)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? ':' : ';');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_COMMA)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '<' : ',');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_PERIOD)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '>' : '.');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_SLASH)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '?' : '/');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_BACKSLASH)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '|' : '\\');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_LEFT_BRACKET)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '{' : '[');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_RIGHT_BRACKET)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '}' : ']');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else if (Engine.input.isKeyPressed(GLFW_KEY_EQUAL)) {
            boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                    || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
            words.addInputChar(isShiftPressed ? '+' : '=');
            words.checkAndRemoveMatchedWord();
            sessionCharCount++;

        } else {
            for (int i = 0; i < 26; i++) {
                int c = GLFW_KEY_A + i;
                if (Engine.input.isKeyPressed(c)) {
                    boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                            || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
                    char charToAdd = isShiftPressed ? (char) ('A' + i) : (char) ('a' + i);
                    words.addInputChar(charToAdd);
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
            for (int i = 0; i < 10; i++) {
                if (Engine.input.isKeyPressed(GLFW_KEY_0 + i)) {
                    boolean isShiftPressed = Engine.input.isKeyDown(GLFW_KEY_LEFT_SHIFT)
                            || Engine.input.isKeyDown(GLFW_KEY_RIGHT_SHIFT);
                    char charToAdd = switch (i) {
                        case 0 -> isShiftPressed ? ')' : '0';
                        case 1 -> isShiftPressed ? '!' : '1';
                        case 2 -> isShiftPressed ? '@' : '2';
                        case 3 -> isShiftPressed ? '#' : '3';
                        case 4 -> isShiftPressed ? '$' : '4';
                        case 5 -> isShiftPressed ? '%' : '5';
                        case 6 -> isShiftPressed ? '^' : '6';
                        case 7 -> isShiftPressed ? '&' : '7';
                        case 8 -> isShiftPressed ? '*' : '8';
                        case 9 -> isShiftPressed ? '(' : '9';
                        default -> (char) ('0' + i);
                    };
                    words.addInputChar(charToAdd);
                    words.checkAndRemoveMatchedWord();
                    sessionCharCount++;
                    break;
                }
            }
        }
    }
}
