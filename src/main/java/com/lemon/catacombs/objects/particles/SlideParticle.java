package com.lemon.catacombs.objects.particles;

import com.lemon.catacombs.Utils;

import java.awt.*;

public class SlideParticle extends Particle{
    private final int size;

    public SlideParticle(int x, int y) {
        super(x, y, 0.9f, 3);
        size = Utils.intRange(2, 6);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(255, 255, 255, getAlpha()));
        g.fillRect(x, y, size, size);
    }
}
