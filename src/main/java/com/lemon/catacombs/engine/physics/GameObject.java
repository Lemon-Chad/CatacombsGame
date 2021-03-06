package com.lemon.catacombs.engine.physics;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.YSortable;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

abstract public class GameObject implements YSortable {
    protected int x, y;
    private float velX = 0, velY = 0;
    protected final int id;
    private final Set<Integer> collisionLayer;
    private final Set<Integer> collisionMask;
    private final Set<Effect> effects;
    private boolean affected;

    public GameObject(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.collisionLayer = new HashSet<>();
        this.collisionMask = new HashSet<>();
        this.effects = new HashSet<>();
    }

    private class Effect {
        private final EffectListener listener;
        private final int duration;
        private int timeLeft;

        public Effect(EffectListener listener, int duration) {
            this.listener = listener;
            this.duration = duration;
            this.timeLeft = duration;
        }

        public boolean update() {
            if (timeLeft == duration) {
                listener.onEffectStart(GameObject.this);
            }
            timeLeft--;
            listener.onEffect(GameObject.this);
            if (timeLeft <= 0) {
                listener.onEffectEnd(GameObject.this);
                return true;
            }
            return false;
        }

        public void render(Graphics g) {
            listener.drawEffect(GameObject.this, g);
        }
    }

    public interface EffectListener {
        void onEffectStart(GameObject gameObject);
        void onEffect(GameObject object);
        void onEffectEnd(GameObject gameObject);
        void drawEffect(GameObject object, Graphics g);
    }

    public void addEffect(EffectListener effect, int duration) {
        effects.add(new Effect(effect, duration));
        affected = true;
    }

    public void tick() {
        if (Game.getInstance().getPhysicsSpeed() == 0) return;
        if (affected) {
            Set<Effect> toRemove = new HashSet<>();
            for (Effect effect : effects) {
                if (effect.update())
                    toRemove.add(effect);
            }
            effects.removeAll(toRemove);
            affected = !effects.isEmpty();
        }
    }

    public void render(Graphics g) {
        for (Effect effect : effects) {
            effect.render(g);
        }
    }

    private void renderFutureHiboxes(Graphics g) {
        Rectangle self = getBounds();
        g.setColor(Color.RED);
        int velXSign = velX > 0 ? 1 : -1;
        for (int i = 0; i < Math.abs(getVelX()); i++) {
            g.drawRect(self.x + velXSign * i, self.y, self.width, self.height);
        }
        g.setColor(Color.BLUE);
        int velYSign = velY > 0 ? 1 : -1;
        for (int i = 0; i < Math.abs(getVelY()); i++) {
            g.drawRect(self.x, self.y + i * velYSign, self.width, self.height);
        }
    }

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

    public int getId() {
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

    public void normalizeVelocity() { normalizeVelocity(1); }

    public void normalizeVelocity(float maxVel) {
        double magnitude = Math.sqrt(velX * velX + velY * velY);
        if (magnitude < maxVel) {
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

    protected void addVelX(float velX) {
        this.velX += velX;
    }

    protected void addVelY(float velY) {
        this.velY += velY;
    }

    protected void addVel(float velX, float velY) {
        this.velX += velX;
        this.velY += velY;
    }

    public void friction(float friction) {
        velX *= Math.pow(friction, Game.getInstance().getPhysicsSpeed());
        velY *= Math.pow(friction, Game.getInstance().getPhysicsSpeed());
    }
}
