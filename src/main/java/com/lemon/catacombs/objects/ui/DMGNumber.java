package com.lemon.catacombs.objects.ui;

import com.lemon.catacombs.engine.Game;

import java.awt.*;

public class DMGNumber extends UIObject {
    private int damage;
    private final Color color;
    private final int startSize;
    private float size;

    private int lifetime;

    public DMGNumber(final int damage, final int x, final int y, final Color color, final int size) {
        super(x, y);
        this.damage = damage;
        this.color = color;
        this.startSize = size;
        this.size = size;
        this.lifetime = 30;
    }

    @Override
    public void tick() {
        y -= size / 10;
        lifetime--;
        size = startSize + (size - startSize) * 0.85f;
        if (lifetime <= 0) {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255 * (lifetime / 30f))));
        g.setFont(new Font("Arial", Font.BOLD, (int) size));
        g.drawString(String.valueOf(damage), x, y);
    }

    public void stack(int damage, int x, int y) {
        this.damage += damage;
        this.x = x;
        this.y = y;
        this.lifetime = 30;
        this.size += Math.min(damage, 50);
    }

    public boolean isDead() {
        return lifetime <= 0;
    }
}
