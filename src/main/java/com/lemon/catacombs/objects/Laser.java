package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class Laser extends GameObject {
    public Laser(int x, int y) {
        super(x, y, ID.Laser);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.ENEMY);
        addCollisionLayer(Layers.PLAYER_PROJECTILES);
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, 16, 16);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }

    @Override
    public void collision(GameObject other) {
        if (other.getId() == ID.Enemy) {
            Enemy enemy = (Enemy) other;
            enemy.damage((int) (Math.random() * 3) + 1);
        }
        destroy();
    }
}
