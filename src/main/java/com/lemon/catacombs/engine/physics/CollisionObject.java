package com.lemon.catacombs.engine.physics;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

abstract public class CollisionObject extends GameObject {
    private final Set<Integer> solids;

    public CollisionObject(int x, int y, int id, int[] solids) {
        super(x, y, id);

        this.solids = new HashSet<>(solids.length);
        for (int i : solids) this.solids.add(i);
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;
    }

    @Override
    public void collision(GameObject other) {
        if (solids.contains(other.getId())) {
            Rectangle r = getBounds();
            Rectangle horizontal = new Rectangle((int) (x + velX), y, r.width, r.height);
            Rectangle vertical = new Rectangle(x, (int) (y + velY), r.width, r.height);
            if (horizontal.intersects(other.getBounds())) {
                if (velX > 0) x = other.getBounds().x - r.width;
                else if (velX < 0) x = other.getBounds().x + other.getBounds().width;
                velX = 0;
            }
            if (vertical.intersects(other.getBounds())) {
                if (velY > 0) y = other.getBounds().y - r.height;
                else if (velY < 0) y = other.getBounds().y + other.getBounds().height;
                velY = 0;
            }
        }
    }

    @Override
    public boolean collidesWith(GameObject o) {
        Rectangle r = getBounds();
        Rectangle future = new Rectangle((int) (x + velX), (int) (y + velY), r.width, r.height);
        return future.intersects(o.getBounds());
    }
}
