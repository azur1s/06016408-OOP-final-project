package com.project.engine;

import com.project.math.Matrix4f;
import com.project.math.Vec2;
import com.project.math.Vec3;

public class OrthoCamera {
    public final Vec3 position = new Vec3(0, 0, 0);
    public float viewportWidth;
    public float viewportHeight;

    public final Matrix4f projection = new Matrix4f();
    public final Matrix4f view = new Matrix4f();
    public final Matrix4f combined = new Matrix4f();

    public OrthoCamera(int viewportWidth, int viewportHeight, boolean centerCamera) {
        setOrtho(viewportWidth, viewportHeight, centerCamera);
    }

    /**
     * Sets up the camera's projection and view matrices for orthographic rendering.
     *
     * @param centerCamera Whether to center the camera on the viewport. If true,
     *                     (0, 0) will be the center of the viewport. If false, (0,
     *                     0) will be the bottom-left corner.
     */
    public void setOrtho(float viewportWidth, float viewportHeight, boolean centerCamera) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;

        if (centerCamera) {
            // Look directly at the origin (0, 0)
            this.position.set(0, 0);
        } else {
            // Shift the camera up and right so that (0, 0) is at the bottom-left corner of
            // the viewport
            this.position.set(viewportWidth / 2f, viewportHeight / 2f);
        }
        update();
    }

    /**
     * Recalculates the matrices. Must be called every time we move the camera's
     * position or change the viewport size.
     */
    public void update() {
        // Create the projection matrix
        // (left, right, bottom, top, near plane, far plane)
        projection.setOrtho(-viewportWidth / 2f, viewportWidth / 2f,
                -viewportHeight / 2f, viewportHeight / 2f,
                0.0f, 100.0f);

        // Create the view matrix by translating in the opposite direction of
        // the camera's position.
        view.set(Matrix4f.translate(-position.x, -position.y, -position.z));

        // Combined = Projection * View
        combined.set(Matrix4f.multiply(projection, view));
    }

    public Vec2 screenToWorld(Vec2 screenCoords) {
        return screenToWorld(screenCoords.x, screenCoords.y);
    }

    public Vec2 screenToWorld(float screenX, float screenY) {
        // Convert screen coordinates to (-1 to 1) range
        float ndcX = (2.0f * screenX) / viewportWidth - 1.0f;
        float ndcY = 1.0f - (2.0f * screenY) / viewportHeight; // Invert Y for NDC

        // Create a vector in (-1 to 1) space
        Vec2 ndcPos = new Vec2(ndcX, ndcY);

        // Invert the combined matrix to transform from (-1 to 1) space back to world
        // coordinates
        Matrix4f invCombined = Matrix4f.invert(combined);
        Vec3 worldPos = Matrix4f.transform(invCombined, new Vec3(ndcPos.x, ndcPos.y, 0));
        return new Vec2(worldPos.x, worldPos.y);
    }

    /**
     * Converts world coordinates to screen coordinates (top-left origin).
     */
    public Vec2 worldToScreen(Vec2 worldCoords) {
        return worldToScreen(worldCoords.x, worldCoords.y);
    }

    /**
     * Converts world coordinates to screen coordinates (top-left origin).
     */
    public Vec2 worldToScreen(float worldX, float worldY) {
        float screenX = (worldX - position.x) + (viewportWidth / 2f);
        float screenY = (viewportHeight / 2f) - (worldY - position.y);
        return new Vec2(screenX, screenY);
    }
}
