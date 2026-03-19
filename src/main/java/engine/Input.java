package engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;

import engine.math.Vec2;

public class Input {
    // An array of booleans representing the current state of each key. True if
    // the key is currently held down.
    private final boolean[] keysDown = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] keysPressed = new boolean[GLFW_KEY_LAST + 1];

    private final boolean[] mouseButtonsPressed = new boolean[2];
    private final boolean[] mouseButtonsReleased = new boolean[2];

    private Vec2 mouse = new Vec2(0, 0);

    public void updateKeyState(int key, int action) {
        if (key >= 0 && key < keysDown.length) {
            if (action == GLFW_PRESS) {
                keysDown[key] = true;
                keysPressed[key] = true; // Key was just pressed
            } else if (action == GLFW_RELEASE) {
                keysDown[key] = false;
            }
        }
    }

    public void updateMousePosition(float xpos, float ypos) {
        mouse.set(xpos, ypos);
    }

    /**
     * Wipe the keysPressed array at the end of each frame to ensure
     * isKeyPressed only returns true once per key press.
     */
    public void endFrame() {
        Arrays.fill(keysPressed, false);
        Arrays.fill(mouseButtonsReleased, false);
    }

    /**
     * Checks if a key is currently held down.
     */
    public boolean isKeyDown(int key) {
        if (key >= 0 && key < keysDown.length) {
            return keysDown[key];
        }
        return false;
    }

    /**
     * Checks if a key was just pressed (transitioned from up to down).
     * This will return true only once per key press.
     */
    public boolean isKeyPressed(int key) {
        if (key >= 0 && key < keysPressed.length) {
            return keysPressed[key];
        }
        return false;
    }

    public Vec2 getMousePosition() {
        return mouse;
    }

    public void updateMouseButtonState(int button, int action) {
        if (button >= 0 && button < mouseButtonsPressed.length) {
            if (action == GLFW_PRESS) {
                mouseButtonsPressed[button] = true;
                mouseButtonsReleased[button] = false;
            } else if (action == GLFW_RELEASE) {
                mouseButtonsPressed[button] = false;
                mouseButtonsReleased[button] = true;
            }
        }
    }

    public boolean isMouseButtonPressed(int button) {
        if (button >= 0 && button < mouseButtonsPressed.length) {
            return mouseButtonsPressed[button];
        }
        return false;
    }

    public boolean isMouseButtonReleased(int button) {
        if (button >= 0 && button < mouseButtonsReleased.length) {
            return mouseButtonsReleased[button];
        }
        return false;
    }
}
