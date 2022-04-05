package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.physics.GameObject;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

abstract public class CollisionObject extends GameObject {
    private final Set<ID> solids;

    public CollisionObject(int x, int y, ID id, ID[] solids) {
        super(x, y, id);

        this.solids = new HashSet<>(solids.length);
        Collections.addAll(this.solids, solids);
    }

    @Override
    public void tick() {
        x += velX;
        y += velY;
    }

    @Override
    public void collision(GameObject other) {
        if (solids.contains(other.getId())) {
            System.out.println("Collision with " + other.getId());
            Rectangle r = getBounds();
            Rectangle horizontal = new Rectangle((int) (x + velX), y, r.width, r.height);
            Rectangle vertical = new Rectangle(x, (int) (y + velY), r.width, r.height);
            if (horizontal.intersects(other.getBounds())) {
                if (velX > 0) x = other.getBounds().x - r.width;
                else if (velX < 0) x = other.getBounds().x + r.width;
                velX = 0;
            }
            if (vertical.intersects(other.getBounds())) {
                if (velY > 0) y = other.getBounds().y - r.height;
                else if (velY < 0) y = other.getBounds().y + r.height;
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
