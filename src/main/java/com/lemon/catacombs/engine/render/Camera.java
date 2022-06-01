package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class Camera {
    private float x, y;
    private float zoom, shake;
    private float zoomDecayRate = 0.9f, shakeDecayRate = 0.9f;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
        this.zoom = 1;
        this.shake = 1;
    }

    public void tick(GameObject object) {
        if (object != null) {
            tick(new Point(object.getX(), object.getY()));
        } else {
            tick((Point) null);
        }
    }

    public void tick(Point focus) {
        if (focus != null) {
            x += (focus.x - x - Game.getInstance().getWidth() / 2f) * 0.05f;
            y += (focus.y - y - Game.getInstance().getHeight() / 2f) * 0.05f;
        }

        if (Game.getInstance().getMap() != null) {
            double maxX = Game.getInstance().getMap().getWidth() - Game.getInstance().getWidth();
            double maxY = Game.getInstance().getMap().getHeight() - Game.getInstance().getHeight();

            x = (float) Math.min(Math.max(0, x), maxX);
            y = (float) Math.min(Math.max(0, y), maxY);
        }
    }

    public float getX() {
        return x + (float) Utils.range(-getShake(), getShake());
    }

    public float getY() {
        return y + (float) Utils.range(-getShake(), getShake());
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZoom() {
        return zoom;
    }

    public double getShake() {
        return Math.log(shake * shake) / Math.log(1.25);
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void setShake(float shake) {
        this.shake = shake;
    }

    public void decayZoom() {
        decayZoom(zoomDecayRate);
    }

    public void decayZoom(float rate) {
        this.zoom = 1 + (this.zoom - 1) * rate;
    }

    public void decayShake() {
        decayShake(shakeDecayRate);
    }

    public void decayShake(float rate) {
        this.shake = 1 + (this.shake - 1) * rate;
    }

    public void setShakeDecayRate(float shakeDecayRate) {
        this.shakeDecayRate = shakeDecayRate;
    }

    public void setZoomDecayRate(float zoomDecayRate) {
        this.zoomDecayRate = zoomDecayRate;
    }

    public float getRawShake() {
        return shake;
    }
}
