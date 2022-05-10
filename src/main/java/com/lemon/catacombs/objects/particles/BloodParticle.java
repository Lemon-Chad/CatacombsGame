package com.lemon.catacombs.objects.particles;

import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class BloodParticle extends Particle {
    private final int redShade;
    private final int size;

    public BloodParticle(int x, int y, int size) {
        super(x, y, 0.1f, 30);
        this.size = size * 4;
        redShade = (int) (150 + Math.round(Math.random() * 105));
    }

    @Override
    public int getYSort() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(redShade, 0, 0, getAlpha()));
        g.fillOval(x + size / 2, y + size / 2, size, size);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 1, 1);
    }

    @Override
    public void collision(GameObject other) {

    }
}
