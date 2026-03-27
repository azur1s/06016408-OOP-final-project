package engine;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import engine.graphics.Graphics;

public class Engine {
    private static long window;

    public static int width;
    public static int height;

    public static Input input = new Input();
    public static Graphics graphics = new Graphics();
    public static AudioManager audio = new AudioManager();

    private static final Object scheduledTasksLock = new Object();
    private static final List<ScheduledTask> scheduledTasks = new ArrayList<>();

    private static Scene currentScene;

    private static class ScheduledTask {
        final float executeAt;
        final Runnable action;

        ScheduledTask(float executeAt, Runnable action) {
            this.executeAt = executeAt;
            this.action = action;
        }
    }

    public static void setScene(Scene newScene) {
        if (currentScene != null)
            currentScene.internalCleanup();
        currentScene = newScene;
        currentScene.internalInit(width, height);
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Schedules an action to run on the game thread after delaySeconds.
     */
    public static void runAfter(Runnable action, float delaySeconds) {
        if (action == null)
            return;

        float executeAt = graphics.getTime() + Math.max(0f, delaySeconds);
        synchronized (scheduledTasksLock) {
            scheduledTasks.add(new ScheduledTask(executeAt, action));
        }
    }

    /**
     * Called once per frame from the main loop to execute due scheduled actions.
     */
    public static void tickScheduledTasks() {
        List<Runnable> dueActions = new ArrayList<>();
        float now = graphics.getTime();

        synchronized (scheduledTasksLock) {
            Iterator<ScheduledTask> it = scheduledTasks.iterator();
            while (it.hasNext()) {
                ScheduledTask task = it.next();
                if (task.executeAt <= now) {
                    dueActions.add(task.action);
                    it.remove();
                }
            }
        }

        for (Runnable action : dueActions) {
            try {
                action.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void requestExit() {
        glfwSetWindowShouldClose(window, true);
    }

    public static void setWindow(long win) {
        window = win;
    }
}
