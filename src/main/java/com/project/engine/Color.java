package com.project.engine;

public class Color {
    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    public static final Color BLACK = new Color(0f, 0f, 0f, 1f);

    public float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public boolean equals(Object obj) {
        // If same reference, they are equal
        if (this == obj)
            return true;
        // If different classes or null, not equal
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Compare RGBA values
        Color other = (Color) obj;
        return Float.compare(other.r, r) == 0
                && Float.compare(other.g, g) == 0
                && Float.compare(other.b, b) == 0
                && Float.compare(other.a, a) == 0;
    }

    @Override
    public int hashCode() {
        int result = (r != 0.0f ? Float.floatToIntBits(r) : 0);
        result = 31 * result + (g != 0.0f ? Float.floatToIntBits(g) : 0);
        result = 31 * result + (b != 0.0f ? Float.floatToIntBits(b) : 0);
        result = 31 * result + (a != 0.0f ? Float.floatToIntBits(a) : 0);
        return result;
    }
}