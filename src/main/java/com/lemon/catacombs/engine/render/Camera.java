package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;

public class Camera {
    private float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(GameObject focus) {
        x += (focus.getX() - x - Game.getInstance().getWidth() / 2f) * 0.05f;
        y += (focus.getY() - y - Game.getInstance().getHeight() / 2f) * 0.05f;

        double maxX = Game.getInstance().getMap().getWidth() - Game.getInstance().getWidth();
        double maxY = Game.getInstance().getMap().getHeight() - Game.getInstance().getHeight();

        x = (float) Math.min(Math.max(0, x), maxX);
        y = (float) Math.min(Math.max(0, y), maxY);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }
}
