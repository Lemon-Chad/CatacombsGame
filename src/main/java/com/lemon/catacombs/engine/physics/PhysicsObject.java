package com.lemon.catacombs.engine.physics;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

abstract public class PhysicsObject extends GameObject {
    private final Set<Integer> solids;
    private float futureVelX;
    private float futureVelY;

    public PhysicsObject(int x, int y, int id, int[] solids) {
        super(x, y, id);

        this.solids = new HashSet<>(solids.length);
        for (int i : solids) this.solids.add(i);
    }

    @Override
    public void tick() {
        super.tick();

        x += Math.round(getVelX());
        y += Math.round(getVelY());

        addVelX(futureVelX);
        futureVelX = 0;
        addVelY(futureVelY);
        futureVelY = 0;
    }

    @Override
    public void collision(GameObject other) {
        if (solids.contains(other.getId())) {
            Rectangle r = getBounds();
            int dx = (int) (x - r.getX());
            int dy = (int) (y - r.getY());
            Rectangle horizontal = new Rectangle((int) (r.x + getVelX()), r.y, r.width, r.height);
            Rectangle vertical = new Rectangle(r.x, (int) (r.y + getVelY()), r.width, r.height);
            if (horizontal.intersects(other.getBounds())) {
                if (getVelX() > 0) x = other.getBounds().x - r.width + dx;
                else if (getVelX() < 0) x = other.getBounds().x + other.getBounds().width + dx;
                setVelX(0);
                setFVelX(0);
            }
            if (vertical.intersects(other.getBounds())) {
                if (getVelY() > 0) y = other.getBounds().y - r.height + dy;
                else if (getVelY() < 0) y = other.getBounds().y + other.getBounds().height + dy;
                setVelY(0);
                setFVelY(0);
            }
        }
    }

    @Override
    public boolean collidesWith(GameObject o) {
        Rectangle r = getBounds();
        Rectangle future = new Rectangle((int) (r.x + getVelX()), (int) (r.y + getVelY()), r.width, r.height);
        return future.intersects(o.getBounds());
    }

    public float getFVelX() {
        return futureVelX;
    }

    public float getFVelY() {
        return futureVelY;
    }

    public void setFVelX(float x) {
        futureVelX = x;
    }

    public void setFVelY(float y) {
        futureVelY = y;
    }

    public void addFVelX(float x) {
        futureVelX += x;
    }

    public void addFVelY(float y) {
        futureVelY += y;
    }

    protected void normalizeFVelocity() {
        normalizeFVelocity(1);
    }

    protected void normalizeFVelocity(float maxVel) {
        double magnitude = Math.sqrt(futureVelX * futureVelX + futureVelY * futureVelY);
        if (magnitude < maxVel) {
            return;
        }
        futureVelX = maxVel * (float) (futureVelX / magnitude);
        futureVelY = maxVel * (float) (futureVelY / magnitude);
    }

    public void addFVel(float x, float y) {
        futureVelX += x;
        futureVelY += y;
    }
}
