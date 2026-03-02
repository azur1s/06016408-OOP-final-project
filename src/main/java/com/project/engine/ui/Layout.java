package com.project.engine.ui;

import com.project.engine.math.Vec2;

public class Layout {
    public Vec2 res;

    public Layout(float width, float height) {
        this.res = new Vec2(width, height);
    }

    public void resize(float width, float height) {
        this.res = new Vec2(width, height);
    }

    // Methods for aligning UI elements to different parts of the screen.

    public Vec2 bottomLeft(float x, float y) {
        return new Vec2(x, y);
    }

    public Vec2 bottomCenter(float x, float y) {
        return new Vec2(res.x / 2 - x, y);
    }

    public Vec2 bottomRight(float x, float y) {
        return new Vec2(res.x - x, y);
    }

    public Vec2 centerLeft(float x, float y) {
        return new Vec2(x, res.y / 2 - y);
    }

    public Vec2 center(float x, float y) {
        return new Vec2(res.x / 2 - x, res.y / 2 - y);
    }

    public Vec2 centerRight(float x, float y) {
        return new Vec2(res.x - x, res.y / 2 - y);
    }

    public Vec2 topLeft(float x, float y) {
        return new Vec2(x, res.y - y);
    }

    public Vec2 topCenter(float x, float y) {
        return new Vec2(res.x / 2 - x, res.y - y);
    }

    public Vec2 topRight(float x, float y) {
        return new Vec2(res.x - x, res.y - y);
    }

    // Methods for UI sizing that scales with screen resolution.

    public Vec2 fullWidth(float height) {
        return new Vec2(res.x, height);
    }

    public Vec2 fullHeight(float width) {
        return new Vec2(width, res.y);
    }

    public Vec2 fullScreen() {
        return res;
    }
}
