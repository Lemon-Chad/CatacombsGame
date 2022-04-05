package com.lemon.catacombs.engine.physics;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.ID;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

abstract public class GameObject {
    protected int x, y;
    protected float velX = 0, velY = 0;
    protected final ID id;
    private final Set<Integer> collisionLayer;
    private final Set<Integer> collisionMask;

    public GameObject(int x, int y, ID id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.collisionLayer = new HashSet<>();
        this.collisionMask = new HashSet<>();
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public ID getId() {
        return id;
    }

    public boolean collidesWith(GameObject o) {
        return getBounds().intersects(o.getBounds());
    }

    public Set<Integer> getCollisionMask() {
        return Collections.unmodifiableSet(collisionMask);
    }

    public Set<Integer> getCollisionLayer() {
        return Collections.unmodifiableSet(collisionLayer);
    }

    public abstract void collision(GameObject other);

    protected void addCollisionLayers(Set<Integer> collisionLayer) {
        for (int layer : collisionLayer) {
            addCollisionLayer(layer);
        }
    }

    protected void addCollisionLayer(int layer) {
        collisionLayer.add(layer);
        Game.getInstance().getWorld().addToLayer(this, layer);
    }

    protected void removeCollisionLayers(Set<Integer> collisionLayer) {
        for (int layer : collisionLayer) {
            removeCollisionLayer(layer);
        }
    }

    protected void removeCollisionLayer(int layer) {
        collisionLayer.remove(layer);
        Game.getInstance().getWorld().removeFromLayer(this, layer);
    }

    protected void normalizeVelocity() { normalizeVelocity(1); }

    protected void normalizeVelocity(float maxVel) {
        double magnitude = Math.sqrt(velX * velX + velY * velY);
        if (magnitude < 1) {
            return;
        }
        velX = maxVel * (float) (velX / magnitude);
        velY = maxVel * (float) (velY / magnitude);
    }

    protected void addCollisionMasks(Set<Integer> collisionMask) {
        for (int mask : collisionMask) {
            addCollisionMask(mask);
        }
    }

    protected void addCollisionMask(int mask) {
        collisionMask.add(mask);
    }

    protected void removeCollisionMasks(Set<Integer> collisionMask) {
        for (int mask : collisionMask) {
            removeCollisionMask(mask);
        }
    }

    protected void removeCollisionMask(int mask) {
        collisionMask.remove(mask);
    }

    public void destroy() {
        Game.getInstance().getWorld().removeObject(this);
    }

    public int getYSort() {
        return y + getBounds().height / 2;
    }
}
