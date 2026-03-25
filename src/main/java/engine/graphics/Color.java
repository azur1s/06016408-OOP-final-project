package engine.graphics;

/**
 * RGBA color represented with normalized float components in range [0, 1].
 */
public class Color {
    /** Opaque white color. */
    public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
    /** Opaque black color. */
    public static final Color BLACK = new Color(0f, 0f, 0f, 1f);
    /** Opaque red color. */
    public static final Color RED = new Color(1f, 0f, 0f, 1f);
    /** Opaque green color. */
    public static final Color GREEN = new Color(0f, 1f, 0f, 1f);
    /** Opaque blue color. */
    public static final Color BLUE = new Color(0f, 0f, 1f, 1f);

    /** Red, green, blue, and alpha components. */
    public float r, g, b, a;

    /**
     * Creates a color from normalized RGBA components.
     *
     * @param r red component
     * @param g green component
     * @param b blue component
     * @param a alpha component
     */
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Compares two colors by exact float component equality.
     *
     * @param obj object to compare
     * @return {@code true} if all RGBA components are equal
     */
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
}