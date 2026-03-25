package engine.entities;

import engine.math.Vec2;

/**
 * Axis-aligned bounding box used for 2D collision checks.
 */
public class AABB {
    /** The minimum corner (bottom-left in world coordinates). */
    public Vec2 min;
    /** The maximum corner (top-right in world coordinates). */
    public Vec2 max;

    /**
     * Creates an axis-aligned bounding box from two corners.
     *
     * @param min the minimum corner
     * @param max the maximum corner
     */
    public AABB(Vec2 min, Vec2 max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Checks whether this box overlaps another box.
     *
     * @param other the other bounding box to test against
     * @return {@code true} if the two boxes overlap; otherwise {@code false}
     */
    public boolean intersects(AABB other) {
        return this.max.x > other.min.x && this.min.x < other.max.x &&
                this.max.y > other.min.y && this.min.y < other.max.y;
    }
}
