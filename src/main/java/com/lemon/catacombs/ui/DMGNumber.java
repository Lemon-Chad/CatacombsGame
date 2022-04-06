package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;

import java.awt.*;

public class DMGNumber extends UIComponent {
    private final int damage;
    private final Color color;
    private final int size;

    private int lifetime;

    public DMGNumber(final int damage, final int x, final int y, final Color color, final int size) {
        super(x, y);
        this.damage = damage;
        this.color = color;
        this.size = size;
        this.lifetime = 30;
    }

    @Override
    public void tick() {
        y -= size * Game.delta();
        lifetime--;
        if (lifetime <= 0) {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        g.drawString(String.valueOf(damage), x, y);
    }
}
