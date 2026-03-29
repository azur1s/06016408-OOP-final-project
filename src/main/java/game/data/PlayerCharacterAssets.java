package game.data;

import java.util.ArrayList;
import java.util.List;

import engine.graphics.AnimationClip;
import engine.graphics.Texture;
import engine.utils.Resources;

public final class PlayerCharacterAssets {
    public static final int CHARACTER_COUNT = 3;
    private static final float DEFAULT_FRAME_DURATION = 0.5f;
    private static final String PLAYER_ANIMATION_FOLDER = "player";
    private static final String SHOT_ANIMATION_FOLDER = "shot_animation";

    private PlayerCharacterAssets() {
    }

    public static String[] getAnimationFramePaths(int characterIndex) {
        return getFramePaths(characterIndex, PLAYER_ANIMATION_FOLDER);
    }

    public static String[] getShotAnimationFramePaths(int characterIndex) {
        return getFramePaths(characterIndex, SHOT_ANIMATION_FOLDER);
    }

    public static String[] getAllAnimationFramePaths() {
        List<String> allPaths = new ArrayList<>();
        for (int characterIndex = 0; characterIndex < CHARACTER_COUNT; characterIndex++) {
            for (String framePath : getAnimationFramePaths(characterIndex)) {
                allPaths.add(framePath);
            }
            for (String framePath : getShotAnimationFramePaths(characterIndex)) {
                allPaths.add(framePath);
            }
        }
        return allPaths.toArray(String[]::new);
    }

    public static AnimationClip createAnimationClip(int characterIndex) {
        return createAnimationClip(getAnimationFramePaths(characterIndex));
    }

    public static AnimationClip createShotAnimationClip(int characterIndex) {
        return createAnimationClip(getShotAnimationFramePaths(characterIndex));
    }

    private static String[] getFramePaths(int characterIndex, String animationFolder) {
        validateCharacterIndex(characterIndex);

        List<String> framePaths = new ArrayList<>();
        for (int frameIndex = 0;; frameIndex++) {
            String path = String.format("textures/player/player_%d/%s/frame_%d.png",
                    characterIndex,
                    animationFolder,
                    frameIndex);
            if (!resourceExists(path)) {
                break;
            }
            framePaths.add(path);
        }

        if (framePaths.isEmpty()) {
            throw new IllegalStateException(
                    "No animation frames found for character " + characterIndex + " in " + animationFolder);
        }

        return framePaths.toArray(String[]::new);
    }

    private static AnimationClip createAnimationClip(String[] framePaths) {
        Texture[] frames = new Texture[framePaths.length];
        for (int i = 0; i < framePaths.length; i++) {
            frames[i] = new Texture(framePaths[i]);
        }
        return new AnimationClip(frames, DEFAULT_FRAME_DURATION);
    }

    public static int sanitizeCharacterIndex(int characterIndex) {
        if (characterIndex < 0 || characterIndex >= CHARACTER_COUNT) {
            return 0;
        }
        return characterIndex;
    }

    public static void cleanup(AnimationClip clip) {
        if (clip == null || clip.frames == null) {
            return;
        }

        for (Texture frame : clip.frames) {
            if (frame != null) {
                frame.cleanup();
            }
        }
    }

    private static void validateCharacterIndex(int characterIndex) {
        if (characterIndex < 0 || characterIndex >= CHARACTER_COUNT) {
            throw new IllegalArgumentException("Invalid character index: " + characterIndex);
        }
    }

    private static boolean resourceExists(String resourcePath) {
        return Resources.class.getClassLoader().getResource(resourcePath) != null;
    }
}
