package engine.graphics;

/**
 * Represents a looping frame-based animation.
 */
public class AnimationClip {
    /** Ordered frames used by this animation clip. */
    public final Texture[] frames;
    /** Duration of each frame in seconds. */
    public final float frameDuration;

    /**
     * Creates an animation clip from an array of texture frames.
     *
     * @param frames        The frames of the animation
     * @param frameDuration The duration of each frame in seconds
     */
    public AnimationClip(Texture[] frames, float frameDuration) {
        this.frames = frames;
        this.frameDuration = frameDuration;
    }

    /**
     * Returns the frame for a given playback time.
     *
     * @param time elapsed animation time in seconds
     * @return frame texture at the current time
     */
    public Texture getFrame(float time) {
        int frameIndex = (int) (time / frameDuration) % frames.length;
        return frames[frameIndex];
    }
}
