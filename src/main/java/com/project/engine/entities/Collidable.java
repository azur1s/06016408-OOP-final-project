package com.project.engine.entities;

public interface Collidable {
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

    boolean isActive();

    void onCollision(Collidable other);
}
