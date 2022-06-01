package com.lemon.catacombs.objects.rooms;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;

import java.awt.*;

public class Wall extends GameObject {
    private final int w, h;
    private final boolean hollow;
    private final int wallThickness;
    public Wall(int x, int y, int w, int h) {
        this(x, y, w, h, 0);
    }

    public Wall(int x, int y, int w, int h, int wallThickness) {
        super(x, y, ID.Block);
        this.w = w;
        this.h = h;
        this.wallThickness = wallThickness;
        this.hollow = wallThickness != 0;
        addCollisionLayer(Layers.BLOCKS);
        addCollisionMask(Layers.PLAYER);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(new Color(255, 255, 255, 255));
        g.fillRect(x, y, w, h);
        if (hollow) {
            g.setColor(new Color(0, 0, 0, 255));
            g.fillRect(x + wallThickness, y + wallThickness, w - wallThickness * 2, h - wallThickness * 2);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    @Override
    public void collision(GameObject other) {

    }
}
