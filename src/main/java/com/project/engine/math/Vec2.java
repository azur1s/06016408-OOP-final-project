package com.project.engine.math;

public class Vec2 {
    public float x, y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    public Vec2 sub(Vec2 v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }

    public Vec2 mul(float scalar) {
        return new Vec2(this.x * scalar, this.y * scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public static float distance(Vec2 a, Vec2 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public Vec2 normalize() {
        float len = length();
        if (len == 0)
            return new Vec2(0, 0);
        return new Vec2(x / len, y / len);
    }

    public static boolean isPointInRect(Vec2 point, Vec2 rectPos, Vec2 rectSize) {
        return point.x >= rectPos.x && point.x <= rectPos.x + rectSize.x &&
                point.y >= rectPos.y && point.y <= rectPos.y + rectSize.y;
    }

    public void set(Vec2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Vec2(" + x + ", " + y + ")";
    }
}
