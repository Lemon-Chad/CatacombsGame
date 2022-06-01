package com.lemon.catacombs.objects.particles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class FireParticle extends Particle {
    private final float hue;
    private final int size;
    private double angle;

    public FireParticle(int x, int y) {
        super(x, y, 0.1f, 3);
        angle = Math.random() * Math.PI * 2;
        hue = (float) (Math.random() * 60 / 360);
        size = Utils.intRange(4, 8);
    }

    @Override
    public void render(Graphics g) {
        float fade = (float) getFade();
        Color color = Color.getHSBColor(hue, fade, fade);
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), getAlpha());

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(angle + getLife() / 10f, x + size / 2f, y + size / 2f);
        g2d.setColor(color);
        g2d.fillRect(x, y, size, size);
        g2d.dispose();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
