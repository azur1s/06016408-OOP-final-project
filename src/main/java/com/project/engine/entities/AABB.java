package com.project.engine.entities;

import com.project.engine.math.Vec2;

public class AABB {
    public Vec2 min;
    public Vec2 max;

    public AABB(Vec2 min, Vec2 max) {
        this.min = min;
        this.max = max;
    }

    public boolean intersects(AABB other) {
        return this.max.x > other.min.x && this.min.x < other.max.x &&
                this.max.y > other.min.y && this.min.y < other.max.y;
    }
}
