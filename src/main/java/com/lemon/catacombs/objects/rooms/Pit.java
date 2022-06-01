package com.lemon.catacombs.objects.rooms;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;

import java.awt.*;

public class Pit extends GameObject {
    public static final int DEPTH = 30;
    private final int w, h;

    public Pit(int x, int y, int w, int h) {
        super(x, y, ID.Pit);
        this.w = w;
        this.h = h;
        addCollisionLayer(Layers.PIT);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Color.BLUE);
        g.fillRect(x, y, w, h);
    }

    @Override
    public int getYSort() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    @Override
    public void collision(GameObject other) {

    }
}
