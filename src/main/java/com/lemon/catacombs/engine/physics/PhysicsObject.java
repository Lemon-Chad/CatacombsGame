package com.lemon.catacombs.engine.physics;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import org.jetbrains.annotations.Nullable;

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

        x += Math.round(getVelX() * Game.getInstance().getPhysicsSpeed());
        y += Math.round(getVelY() * Game.getInstance().getPhysicsSpeed());

        addVelX(futureVelX);
        futureVelX = 0;
        addVelY(futureVelY);
        futureVelY = 0;
    }

    @Override
    public void collision(GameObject other) {
        if (solids.contains(other.getId())) {
            Rectangle r = getBounds();
            Vector step = new Vector(r.x, r.y);
            int dx = (int) Math.round(x - r.getX());
            int dy = (int) Math.round(y - r.getY());
            Vector vel = new Vector(getVelX(), getVelY());
            double length = vel.length();
            vel = vel.normalize();
            boolean collided = false;
            for (int i = 0; i < length; i++) {
                Rectangle steptangle = new Rectangle((int) Math.round(step.x), (int) Math.round(step.y), r.width, r.height);
                collided = collides(other, vel, r, steptangle, dx, dy);
                if (collided) break;
                step = step.add(vel);
            }
            if (!collided) {
                Rectangle steptangle = new Rectangle((int) Math.round(step.x), (int) Math.round(step.y), r.width, r.height);
                Vector remaining = vel.mul(length).sub(step.sub(new Vector(r.x, r.y)));
                collides(other, remaining, r, steptangle, dx, dy);
            }
        }
    }

    public boolean collides(GameObject other, Vector velocity, Rectangle r, Rectangle step, int dx, int dy) {
        int result = collisionCheck(other, step, velocity);
        if ((result & 1) != 0) {
            if (getVelX() > 0) x = other.getBounds().x - r.width + dx;
            else if (getVelX() < 0) x = other.getBounds().x + other.getBounds().width + dx;
            setVelX(0);
            setFVelX(0);
        }
        if ((result & 2) != 0) {
            if (getVelY() > 0) y = other.getBounds().y - r.height + dy;
            else if (getVelY() < 0) y = other.getBounds().y + other.getBounds().height + dy;
            setVelY(0);
            setFVelY(0);
        }
        return result != 0;
    }

    public int collisionCheck(GameObject other, Rectangle r, Vector velocity) {
        Rectangle horizontal = new Rectangle((int) Math.round(r.x + velocity.x), r.y + 1, r.width, r.height - 2);
        Rectangle vertical = new Rectangle(r.x + 1, (int) Math.round(r.y + velocity.y), r.width - 2, r.height);
        int result = 0;
        if (horizontal.intersects(other.getBounds())) result |= 1;
        if (vertical.intersects(other.getBounds())) result |= 2;
        return result;
    }

    @Override
    public boolean collidesWith(GameObject o) {
        return getSweepBounds().intersects(o.getBounds());
    }

    public Polygon getSweepBounds() {
        Rectangle r = getBounds();
        Rectangle future = new Rectangle(Math.round(r.x + getVelX()), Math.round(r.y + getVelY()), r.width, r.height);
        int[] x;
        int[] y;
        if (getVelX() >= 0 && getVelY() >= 0) {
            x = new int[] {
                    r.x,
                    r.x + r.width,
                    future.x + future.width,
                    future.x + future.width,
                    future.x,
                    r.x
            };
            y = new int[] {
                    r.y,
                    r.y,
                    future.y,
                    future.y + future.height,
                    future.y + future.height,
                    r.y + r.height
            };
        } else if (getVelX() >= 0 && getVelY() < 0) {
            x = new int[] {
                    r.x,
                    r.x + r.width,
                    future.x + future.width,
                    future.x + future.width,
                    future.x,
                    r.x
            };
            y = new int[] {
                    r.y + r.height,
                    r.y + r.height,
                    future.y + future.height,
                    future.y,
                    future.y,
                    r.y
            };
        } else if (getVelX() < 0 && getVelY() >= 0) {
            x = new int[] {
                    r.x + r.width,
                    r.x,
                    future.x,
                    future.x,
                    future.x + future.width,
                    r.x + r.width
            };
            y = new int[] {
                    r.y,
                    r.y,
                    future.y,
                    future.y + future.height,
                    future.y + future.height,
                    r.y + r.height
            };
        } else {
            x = new int[] {
                    r.x + r.width,
                    r.x,
                    future.x,
                    future.x,
                    future.x + future.width,
                    r.x + r.width
            };
            y = new int[] {
                    r.y + r.height,
                    r.y + r.height,
                    future.y + future.height,
                    future.y,
                    future.y,
                    r.y
            };
        }
        return new Polygon(x, y, 6);
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
