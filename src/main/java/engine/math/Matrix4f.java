package engine.math;

public class Matrix4f {
    // OpenGL use column-major order by default so we will store the matrix
    // respectively.
    public float[] elements = new float[16];

    public Matrix4f() {
        setIdentity();
    }

    public void set(Matrix4f other) {
        System.arraycopy(other.elements, 0, this.elements, 0, 16);
    }

    // Sets this matrix to the Identity Matrix
    public void setIdentity() {
        for (int i = 0; i < 16; i++)
            elements[i] = 0;
        elements[0 + 0 * 4] = 1.0f;
        elements[1 + 1 * 4] = 1.0f;
        elements[2 + 2 * 4] = 1.0f;
        elements[3 + 3 * 4] = 1.0f;
    }

    /**
     * Multiplies two 4x4 matrices and returns the result as a new Matrix4f
     */
    public static Matrix4f multiply(Matrix4f a, Matrix4f b) {
        Matrix4f result = new Matrix4f();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                float sum = 0.0f;
                for (int k = 0; k < 4; k++) {
                    sum += a.elements[row + k * 4] * b.elements[k + col * 4];
                }
                result.elements[row + col * 4] = sum;
            }
        }
        return result;
    }

    /**
     * Creates a translation matrix for the given x, y, z translation values.
     * The resulting matrix will move points by (x, y, z) when applied to them.
     */
    public static Matrix4f translate(float x, float y, float z) {
        Matrix4f result = new Matrix4f(); // Starts as identity
        result.elements[0 + 3 * 4] = x;
        result.elements[1 + 3 * 4] = y;
        result.elements[2 + 3 * 4] = z;
        return result;
    }

    /**
     * Creates a scaling matrix for the given x, y, z scaling factors.
     * The resulting matrix will scale points by (x, y, z) when applied to them.
     */
    public static Matrix4f scale(float x, float y, float z) {
        Matrix4f result = new Matrix4f();
        result.elements[0 + 0 * 4] = x;
        result.elements[1 + 1 * 4] = y;
        result.elements[2 + 2 * 4] = z;
        return result;
    }

    /**
     * Creates a rotation matrix that rotates points around the Y-axis by the given
     * angle in degrees.
     */
    public static Matrix4f rotateY(float angleDegrees) {
        Matrix4f result = new Matrix4f();
        float angle = (float) Math.toRadians(angleDegrees);
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        result.elements[0 + 0 * 4] = cos;
        result.elements[0 + 2 * 4] = sin;
        result.elements[2 + 0 * 4] = -sin;
        result.elements[2 + 2 * 4] = cos;
        return result;
    }

    /**
     * Calculates the inverse of a matrix by using Analytic solution.
     * See https://en.wikipedia.org/wiki/Invertible_matrix#Analytic_solution
     * If the matrix is not invertible (determinant is zero), this method returns an
     * identity matrix.
     */
    public static Matrix4f invert(Matrix4f m) {
        float[] inv = new float[16]; // Inverse matrix elements
        float[] mE = m.elements; // Original matrix elements for easier access

        // #region Calculate the matrix of cofactors (also known as the adjugate matrix)
        // C11, C12, C13, C14
        inv[0] = mE[5] * mE[10] * mE[15] -
                mE[5] * mE[11] * mE[14] -
                mE[9] * mE[6] * mE[15] +
                mE[9] * mE[7] * mE[14] +
                mE[13] * mE[6] * mE[11] -
                mE[13] * mE[7] * mE[10];

        inv[4] = -mE[4] * mE[10] * mE[15] +
                mE[4] * mE[11] * mE[14] +
                mE[8] * mE[6] * mE[15] -
                mE[8] * mE[7] * mE[14] -
                mE[12] * mE[6] * mE[11] +
                mE[12] * mE[7] * mE[10];

        inv[8] = mE[4] * mE[9] * mE[15] -
                mE[4] * mE[11] * mE[13] -
                mE[8] * mE[5] * mE[15] +
                mE[8] * mE[7] * mE[13] +
                mE[12] * mE[5] * mE[11] -
                mE[12] * mE[7] * mE[9];

        inv[12] = -mE[4] * mE[9] * mE[14] +
                mE[4] * mE[10] * mE[13] +
                mE[8] * mE[5] * mE[14] -
                mE[8] * mE[6] * mE[13] -
                mE[12] * mE[5] * mE[10] +
                mE[12] * mE[6] * mE[9];

        // C21, C22, C23, C24
        inv[1] = -mE[1] * mE[10] * mE[15] +
                mE[1] * mE[11] * mE[14] +
                mE[9] * mE[2] * mE[15] -
                mE[9] * mE[3] * mE[14] -
                mE[13] * mE[2] * mE[11] +
                mE[13] * mE[3] * mE[10];

        inv[5] = mE[0] * mE[10] * mE[15] -
                mE[0] * mE[11] * mE[14] -
                mE[8] * mE[2] * mE[15] +
                mE[8] * mE[3] * mE[14] +
                mE[12] * mE[2] * mE[11] -
                mE[12] * mE[3] * mE[10];

        inv[9] = -mE[0] * mE[9] * mE[15] +
                mE[0] * mE[11] * mE[13] +
                mE[8] * mE[1] * mE[15] -
                mE[8] * mE[3] * mE[13] -
                mE[12] * mE[1] * mE[11] +
                mE[12] * mE[3] * mE[9];

        inv[13] = mE[0] * mE[9] * mE[14] -
                mE[0] * mE[10] * mE[13] -
                mE[8] * mE[1] * mE[14] +
                mE[8] * mE[2] * mE[13] +
                mE[12] * mE[1] * mE[10] -
                mE[12] * mE[2] * mE[9];

        // C31, C32, C33, C34
        inv[2] = mE[1] * mE[6] * mE[15] -
                mE[1] * mE[7] * mE[14] -
                mE[5] * mE[2] * mE[15] +
                mE[5] * mE[3] * mE[14] +
                mE[13] * mE[2] * mE[7] -
                mE[13] * mE[3] * mE[6];

        inv[6] = -mE[0] * mE[6] * mE[15] +
                mE[0] * mE[7] * mE[14] +
                mE[4] * mE[2] * mE[15] -
                mE[4] * mE[3] * mE[14] -
                mE[12] * mE[2] * mE[7] +
                mE[12] * mE[3] * mE[6];

        inv[10] = mE[0] * mE[5] * mE[15] -
                mE[0] * mE[7] * mE[13] -
                mE[4] * mE[1] * mE[15] +
                mE[4] * mE[3] * mE[13] +
                mE[12] * mE[1] * mE[7] -
                mE[12] * mE[3] * mE[5];

        inv[14] = -mE[0] * mE[5] * mE[14] +
                mE[0] * mE[6] * mE[13] +
                mE[4] * mE[1] * mE[14] -
                mE[4] * mE[2] * mE[13] -
                mE[12] * mE[1] * mE[6] +
                mE[12] * mE[2] * mE[5];

        // C41, C42, C43, C44
        inv[3] = -mE[1] * mE[6] * mE[11] +
                mE[1] * mE[7] * mE[10] +
                mE[5] * mE[2] * mE[11] -
                mE[5] * mE[3] * mE[10] -
                mE[9] * mE[2] * mE[7] +
                mE[9] * mE[3] * mE[6];

        inv[7] = mE[0] * mE[6] * mE[11] -
                mE[0] * mE[7] * mE[10] -
                mE[4] * mE[2] * mE[11] +
                mE[4] * mE[3] * mE[10] +
                mE[8] * mE[2] * mE[7] -
                mE[8] * mE[3] * mE[6];

        inv[11] = -mE[0] * mE[5] * mE[11] +
                mE[0] * mE[7] * mE[9] +
                mE[4] * mE[1] * mE[11] -
                mE[4] * mE[3] * mE[9] -
                mE[8] * mE[1] * mE[7] +
                mE[8] * mE[3] * mE[5];

        inv[15] = mE[0] * mE[5] * mE[10] -
                mE[0] * mE[6] * mE[9] -
                mE[4] * mE[1] * mE[10] +
                mE[4] * mE[2] * mE[9] +
                mE[8] * mE[1] * mE[6] -
                mE[8] * mE[2] * mE[5];
        // #endregion

        // Calculate the determinant using the first row of the original matrix and the
        // corresponding cofactors from the inverse matrix
        float det = mE[0] * inv[0] + mE[1] * inv[4] + mE[2] * inv[8] + mE[3] * inv[12];

        // If the determinant is zero, the matrix is not invertible. Return an identity
        // matrix in this case.
        if (det == 0) {
            Matrix4f identity = new Matrix4f();
            identity.setIdentity();
            return identity;
        }

        // Divide the adjugate matrix (stored in inv) by the determinant to get the
        // inverse matrix
        det = 1.0f / det;

        Matrix4f result = new Matrix4f();

        for (int i = 0; i < 16; i++) {
            result.elements[i] = inv[i] * det;
        }

        return result;
    }

    /**
     * Creates an orthographic projection matrix that maps a specified cube region
     * (left, right, bottom, top, near, far) to normalized device coordinates (-1 to
     * 1 in all axes).
     */
    public void setOrtho(float left, float right, float bottom, float top, float near, float far) {
        setIdentity(); // Start with an identity matrix

        // Diagonal scaling
        elements[0 + 0 * 4] = 2.0f / (right - left);
        elements[1 + 1 * 4] = 2.0f / (top - bottom);
        elements[2 + 2 * 4] = -2.0f / (far - near);

        // Translation
        elements[0 + 3 * 4] = -(right + left) / (right - left);
        elements[1 + 3 * 4] = -(top + bottom) / (top - bottom);
        elements[2 + 3 * 4] = -(far + near) / (far - near);
    }

    /**
     * Transforms a Vec3 by this matrix. Useful for converting between
     * coordinate spaces (e.g. world to screen coordinates).
     */
    public static Vec3 transform(Matrix4f m, Vec3 v) {
        float x = m.elements[0] * v.x + m.elements[4] * v.y + m.elements[8] * v.z + m.elements[12];
        float y = m.elements[1] * v.x + m.elements[5] * v.y + m.elements[9] * v.z + m.elements[13];
        float z = m.elements[2] * v.x + m.elements[6] * v.y + m.elements[10] * v.z + m.elements[14];
        float w = m.elements[3] * v.x + m.elements[7] * v.y + m.elements[11] * v.z + m.elements[15];

        // If w is not 1 (when perspective transformations are involved), we need to
        // divide by w to get the correct coordinates
        if (w != 0 && w != 1) {
            x /= w;
            y /= w;
            z /= w;
        }

        return new Vec3(x, y, z);
    }
}
