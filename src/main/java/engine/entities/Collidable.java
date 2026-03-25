package engine.entities;

/**
 * Interface for objects that participate in collision detection.
 */
public interface Collidable {
    /**
     * Returns the current world-space axis-aligned bounding box.
     *
     * @return bounding box used for overlap checks
     */
    AABB getBoundingBox();

    /**
     * A bitmask representing what collision layer this entity belongs to.
     */
    int getLayer();

    /**
     * A bitmask representing what collision layers this entity should collide
     * with.
     */
    int getMask();

    /**
     * Indicates whether this object should currently be considered for
     * collision checks.
     *
     * @return {@code true} if active, otherwise {@code false}
     */
    boolean isActive();

    /**
     * Callback invoked when a collision with another collidable is detected.
     *
     * @param other the other collidable involved in the collision
     */
    void onCollision(Collidable other);
}
