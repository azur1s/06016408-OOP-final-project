package com.project.math;

public class Vec2 {
    public float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float distance(Vec2 a, Vec2 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public static Vec2 normalize(Vec2 v) {
        float len = v.length();
        if (len == 0)
            return new Vec2(0, 0);
        return new Vec2(v.x / len, v.y / len);
    }

    public void set(Vec2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
