package com.project;

import com.badlogic.gdx.graphics.Color;

public class GameConfig {

    // --- World/Camera Configuration ---

    /** Virtual world width for the game viewport */
    public static final float WORLD_WIDTH = 1000f;

    /** Virtual world height for the game viewport */
    public static final float WORLD_HEIGHT = 600f;

    /** Y-coordinate of the center of the lane system */
    public static final float LANE_Y_CENTER = 300f;

    // --- UI Configuration ---

    /** Standard padding used for UI elements from screen edges */
    public static final float UI_PADDING = 24f;

    /** Height of the health bar in pixels */
    public static final float HEALTH_BAR_HEIGHT = 10f;

    /** Total horizontal padding for health bar (left + right) */
    public static final float HEALTH_BAR_TOTAL_PADDING = UI_PADDING * 2f;

    /** Base scale factor for score text */
    public static final float BASE_SCORE_SCALE = 1.5f;

    // --- Font Configuration ---

    /** Path to the main game font */
    public static final String MAIN_FONT_PATH = "fonts/monogram-extended-italic.ttf";

    /** Size of the main font in pixels */
    public static final int MAIN_FONT_SIZE = 32;

    // --- Particles Configuration ---

    /** Path to the death particle effect file */
    public static final String DEATH_PARTICLE_PATH = "particles/entityDeath.p";

    /** Directory containing particle assets */
    public static final String PARTICLE_DIRECTORY = "particles/";

    // --- Game Logic Configuration ---

    /** Initial number of word entities to spawn */
    public static final int INITIAL_WORD_COUNT = 10;

    /** Maximum streak count that affects score scale bonus */
    public static final int MAX_STREAK_FOR_BONUS = 10;

    /** Time in seconds for streak bonus to decay */
    public static final float STREAK_DECAY_TIME = 3f;

    // --- Animation/Lerp Speeds ---

    /** Interpolation speed for health bar animation */
    public static final float HEALTH_LERP_SPEED = 5f;

    /** Interpolation speed for score scale animation */
    public static final float SCORE_SCALE_LERP_SPEED = 10f;

    // --- Lane Background Configuration ---

    /** Number of lanes in the game */
    public static final int LANE_COUNT = 5;

    /** Color for even-numbered lanes (0, 2, 4) */
    public static final Color LANE_COLOR_EVEN = new Color(0.2f, 0.2f, 0.25f, 0.1f);

    /** Color for odd-numbered lanes (1, 3) */
    public static final Color LANE_COLOR_ODD = new Color(0.15f, 0.15f, 0.2f, 0.1f);

    // --- Fail Zone Configuration ---

    /** X-coordinate of the fail zone line */
    public static final float FAIL_ZONE_X = 100f;

    /** Color of the fail zone indicator */
    public static final Color FAIL_ZONE_COLOR = new Color(1f, 0.5f, 0.5f, 0.5f);

    // --- Color Palette ---

    /** Background clear color */
    public static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.15f, 1f);

    /** Health bar background color */
    public static final Color HEALTH_BAR_BG_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.8f);

    /** Health bar fill color */
    public static final Color HEALTH_BAR_FILL_COLOR = new Color(0.5f, 1f, 0.5f, 0.8f);

    // Private constructor to prevent instantiation
    private GameConfig() {
        throw new AssertionError("Configuration class should not be instantiated");
    }
}
