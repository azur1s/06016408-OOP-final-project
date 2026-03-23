package engine.graphics;

public class AnimationClip {
    public final Texture[] frames;
    public final float frameDuration; // Duration of each frame in seconds

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

    public Texture getFrame(float time) {
        int frameIndex = (int) (time / frameDuration) % frames.length;
        return frames[frameIndex];
    }
}
