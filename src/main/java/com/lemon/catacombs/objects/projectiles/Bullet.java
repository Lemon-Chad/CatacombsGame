package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.Layers;

import java.awt.*;

public abstract class Bullet extends GameObject {
    private int damage;

    public Bullet(int x, int y, int id) {
        super(x, y, id);
        addCollisionMask(Layers.BLOCKS);
    }

    @Override
    public void tick() {
        x += getVelX();
        y += getVelY();

        setVelX(getVelX() * 0.99f);
        setVelY(getVelY() * 0.99f);

        if (getVelX() < 0.1 && getVelX() > -0.1) {
            destroy();
        }

        if (getVelY() < 0.1 && getVelY() > -0.1) {
            destroy();
        }
    }

    abstract Color getColor();

    abstract int getSize();

    @Override
    public void render(Graphics g) {
        Color color = getColor();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int) (255 * Math.min(Math.sqrt(getVelX() * getVelX() + getVelY() * getVelY()) / 3f, 1))));
        g.fillOval(x, y, getSize(), getSize());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, getSize(), getSize());
    }

    @Override
    public void collision(GameObject other) {
        destroy();
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
