package com.project.engine.entities;

import com.project.engine.math.Vec2;

public class Entity implements Collidable {
    public Vec2 position;
    public Vec2 velocity = new Vec2(0f, 0f);
    public boolean active = true;
    public AABB boundingBox;

    /// Creates a new entity with the given position and size.
    /// Size is used to calculate the bounding box, which is centered on the
    /// position.
    public Entity(Vec2 position, Vec2 size) {
        this.position = position;
        this.boundingBox = new AABB(
                new Vec2(position.x - size.x / 2, position.y - size.y / 2),
                new Vec2(position.x + size.x / 2, position.y + size.y / 2));
    }

    public void update(float delta) {
        // Default implementation does nothing, override in subclasses
    }

    public void render() {
        // Default implementation does nothing, override in subclasses
    }

    /// Moves the entity by the given delta, and updates the bounding box
    /// accordingly.
    public void move(Vec2 delta, float deltaTime) {
        position.set(position.add(delta.mul(deltaTime)));
        boundingBox.min.set(boundingBox.min.add(delta.mul(deltaTime)));
        boundingBox.max.set(boundingBox.max.add(delta.mul(deltaTime)));
    }

    // ========================================================================
    // Collidable interface implementation

    @Override
    public AABB getBoundingBox() {
        return boundingBox;
    }

    @Override
    public int getLayer() {
        return 0; // Default layer, override in subclasses for specific layers
    }

    @Override
    public int getMask() {
        return 0; // Default layer, override in subclasses for specific layers
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void onCollision(Collidable other) {
        // Default implementation does nothing, override in subclasses
    }
}
