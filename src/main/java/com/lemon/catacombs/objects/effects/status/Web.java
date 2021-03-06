package com.lemon.catacombs.objects.effects.status;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.projectiles.Bullet;

import java.awt.*;

public class Web extends Bullet {
    private final int radius;
    private int r;
    private int life;

    public Web(int x, int y, int r, int life) {
        super(x, y, ID.SplashEffect);
        this.radius = Utils.intRange(r, r * 2);
        this.r = 0;
        this.life = Utils.intRange(life, life * 2);
        addCollisionLayer(Layers.SPLASH);
        addCollisionMask(Layers.ENEMY);
        addCollisionMask(Layers.BLOCKS);
    }

    @Override
    protected Color getColor() {
        return Color.WHITE;
    }

    @Override
    protected int getSize() {
        return r;
    }

    @Override
    public void tick() {
        x += Math.round(getVelX());
        y += Math.round(getVelY());

        friction(0.9f);

        if (r < radius) {
            r++;
        }
        life--;
        if (life <= 0) {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        int alpha = (int) (100 * Math.min(1, life / 30.0));
        g.setColor(new Color(255, 255, 255, alpha));
        g.fillOval(getX() - r, getY() - r, r * 2, r * 2);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(getX() - r, getY() - r, r * 2, r * 2);
    }

    @Override
    public boolean collidesWith(GameObject o) {
        return Point.distance(o.getX(), o.getY(), getX(), getY()) < r;
    }

    @Override
    public void collision(GameObject other) {
        if (other.getId() == ID.Block || other.getId() == ID.Door) {
            setVelX(0);
            setVelY(0);
        }
        other.addEffect(new EffectListener() {
            @Override
            public void onEffectStart(GameObject gameObject) {}

            @Override
            public void onEffect(GameObject object) {
                object.normalizeVelocity(1f);
            }

            @Override
            public void onEffectEnd(GameObject gameObject) {}

            @Override
            public void drawEffect(GameObject object, Graphics g) {

            }
        }, 1);
    }
}
