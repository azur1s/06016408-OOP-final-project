package engine.graphics;

/**
 * A class for managing graphics-related state (timing, FPS, etc.) and
 * functionality that can be accessed by renderers and other graphics
 * components.
 */
public class Graphics {
    private float time;
    private float deltaTime = 0;
    private int fps = 0;

    private int framesThisSecond = 0;
    private double timeThisSecond = 0;

    /**
     * Updates frame timing and FPS counters.
     *
     * @param time      total elapsed runtime in seconds
     * @param deltaTime elapsed time in seconds since previous frame
     */
    public void updateTime(float time, float deltaTime) {
        this.time = time;
        this.deltaTime = deltaTime;

        // Calculate FPS
        framesThisSecond++;
        timeThisSecond += deltaTime;

        if (timeThisSecond >= 1.0) {
            fps = framesThisSecond;
            framesThisSecond = 0;
            timeThisSecond -= 1.0;
        }
    }

    /** Returns the total time in seconds since the application started. */
    public float getTime() {
        return time;
    }

    /** The time span between the current frame and the last frame in seconds. */
    public float getDeltaTime() {
        return deltaTime;
    }

    /** Returns the average number of frames per second. */
    public int getFramesPerSecond() {
        return fps;
    }
}
