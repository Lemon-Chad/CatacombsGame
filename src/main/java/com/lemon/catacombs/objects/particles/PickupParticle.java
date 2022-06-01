package com.lemon.catacombs.objects.particles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class PickupParticle extends Particle {
    private double angle;
    private final int size;

    public PickupParticle(int x, int y) {
        super(x, y, 0.1f, 10);
        angle = Math.random() * Math.PI * 2;
        size = Utils.intRange(6, 16);
        float force = (float) Utils.range(3, 6);
        setVelX((float) Math.cos(angle) * force);
        setVelY((float) Math.sin(angle) * force);
    }

    @Override
    public void render(Graphics g) {
        angle += 0.1;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(255, 255, 255, getAlpha()));
        g2d.rotate(angle, x + size / 2f, y + size / 2f);
        g2d.fillRect(x, y, size, size);
        g2d.dispose();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }
}
