package com.project.engine.math;

public class Vec3 {
    public float x, y, z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3 add(Vec3 v) {
        return new Vec3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vec3 scale(float s) {
        return new Vec3(this.x * s, this.y * s, this.z * s);
    }

    // Magnitude/Length
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    // Normalization (turning the vector into a unit vector of length 1)
    public Vec3 normalize() {
        float len = length();
        if (len == 0)
            return new Vec3(0, 0, 0);
        return new Vec3(x / len, y / len, z / len);
    }

    public float dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
