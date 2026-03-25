package engine.entities;

import engine.math.Vec2;

/**
 * Base 2D entity implementation with position, size, movement, collision
 * support and etc.
 */
public class Entity implements Collidable {
    /** The current world position of the entity center. */
    public Vec2 position;
    /** The entity size used to build its bounding box. */
    public Vec2 size;
    /** The current velocity vector. */
    public Vec2 velocity = new Vec2(0f, 0f);
    /** Whether the entity is currently active. */
    public boolean active = true;
    /** Axis-aligned bounding box used for broad collision checks. */
    public AABB boundingBox;

    /**
     * Creates a new entity with the given position and size.
     *
     * <p>
     * Size is used to calculate the bounding box, centered on the position.
     * </p>
     *
     * @param position initial center position
     * @param size     dimensions used to compute the bounding box extents
     */
    public Entity(Vec2 position, Vec2 size) {
        this.size = size;
        this.position = position;
        this.boundingBox = new AABB(
                new Vec2(position.x - size.x / 2, position.y - size.y / 2),
                new Vec2(position.x + size.x / 2, position.y + size.y / 2));
    }

    /**
     * Updates entity state.
     *
     * @param delta elapsed time in seconds since the previous frame
     */
    public void update(float delta) {
        // Default implementation does nothing, override in subclasses
    }

    /**
     * Renders the entity.
     */
    public void render() {
        // Default implementation does nothing, override in subclasses
    }

    /**
     * Moves the entity by the given direction/offset scaled by delta time and
     * updates the bounding box accordingly.
     *
     * @param delta     movement vector applied this frame
     * @param deltaTime elapsed time in seconds used as scale for movement
     */
    public void move(Vec2 delta, float deltaTime) {
        position.set(position.add(delta.mul(deltaTime)));
        boundingBox.min.set(boundingBox.min.add(delta.mul(deltaTime)));
        boundingBox.max.set(boundingBox.max.add(delta.mul(deltaTime)));
    }

    // ========================================================================
    // Collidable interface implementation

    /** {@inheritDoc} */
    @Override
    public AABB getBoundingBox() {
        return boundingBox;
    }

    /**
     * Returns the collision layer bit for this entity.
     *
     * @return layer bitmask; default is {@code 0}
     */
    @Override
    public int getLayer() {
        return 0; // Default layer, override in subclasses for specific layers
    }

    /**
     * Returns the collision mask used to filter target layers.
     *
     * @return collision mask bitmask; default is {@code 0}
     */
    @Override
    public int getMask() {
        return 0; // Default layer, override in subclasses for specific layers
    }

    /** {@inheritDoc} */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Handles collision callbacks.
     *
     * @param other the collidable collided with
     */
    @Override
    public void onCollision(Collidable other) {
        // Default implementation does nothing, override in subclasses
    }
}
