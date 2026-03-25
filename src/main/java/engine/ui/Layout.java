package engine.ui;

import engine.math.Vec2;

/**
 * Utility for computing UI positions and sizes relative to current resolution.
 */
public class Layout {
    /** Current resolution used for layout calculations. */
    public Vec2 res;

    /**
     * Creates a layout helper for the given resolution.
     *
     * @param width  viewport width
     * @param height viewport height
     */
    public Layout(float width, float height) {
        this.res = new Vec2(width, height);
    }

    /**
     * Updates the resolution used for layout calculations.
     *
     * @param width  viewport width
     * @param height viewport height
     */
    public void resize(float width, float height) {
        this.res = new Vec2(width, height);
    }

    // Methods for aligning UI elements to different parts of the screen.

    /**
     * Positions an element from the bottom-left corner.
     */
    public Vec2 bottomLeft(float x, float y) {
        return new Vec2(x, y);
    }

    /**
     * Positions an element from the bottom-center anchor.
     */
    public Vec2 bottomCenter(float x, float y) {
        return new Vec2(res.x / 2 - x, y);
    }

    /**
     * Positions an element from the bottom-right corner.
     */
    public Vec2 bottomRight(float x, float y) {
        return new Vec2(res.x - x, y);
    }

    /**
     * Positions an element from the center-left anchor.
     */
    public Vec2 centerLeft(float x, float y) {
        return new Vec2(x, res.y / 2 - y);
    }

    /**
     * Positions an element from the center anchor.
     */
    public Vec2 center(float x, float y) {
        return new Vec2(res.x / 2 - x, res.y / 2 - y);
    }

    /**
     * Positions an element from the center-right anchor.
     */
    public Vec2 centerRight(float x, float y) {
        return new Vec2(res.x - x, res.y / 2 - y);
    }

    /**
     * Positions an element from the top-left corner.
     */
    public Vec2 topLeft(float x, float y) {
        return new Vec2(x, res.y - y);
    }

    /**
     * Positions an element from the top-center anchor.
     */
    public Vec2 topCenter(float x, float y) {
        return new Vec2(res.x / 2 - x, res.y - y);
    }

    /**
     * Positions an element from the top-right corner.
     */
    public Vec2 topRight(float x, float y) {
        return new Vec2(res.x - x, res.y - y);
    }

    // Methods for UI sizing that scales with screen resolution.

    /**
     * Returns a size spanning the full width and a fixed height.
     */
    public Vec2 fullWidth(float height) {
        return new Vec2(res.x, height);
    }

    /**
     * Returns a size spanning the full height and a fixed width.
     */
    public Vec2 fullHeight(float width) {
        return new Vec2(width, res.y);
    }

    /**
     * Returns the full-screen size vector.
     */
    public Vec2 fullScreen() {
        return res;
    }
}
