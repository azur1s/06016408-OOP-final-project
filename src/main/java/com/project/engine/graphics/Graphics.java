package com.project.engine.graphics;

public class Graphics {
    private float deltaTime = 0;
    private int fps = 0;

    private int framesThisSecond = 0;
    private double timeThisSecond = 0;

    public void updateTime(float deltaTime) {
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

    /** The time span between the current frame and the last frame in seconds. */
    public float getDeltaTime() {
        return deltaTime;
    }

    /** Returns the average number of frames per second. */
    public int getFramesPerSecond() {
        return fps;
    }
}
