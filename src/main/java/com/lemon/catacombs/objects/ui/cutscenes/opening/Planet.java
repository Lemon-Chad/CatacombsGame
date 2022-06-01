package com.lemon.catacombs.objects.ui.cutscenes.opening;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Planet {
    private final BufferedImage image;
    private final int w, h;
    int x, y;
    private double angle;

    public Planet(int x, int y) {
        int type = (int)(Utils.range(1, 3));
        BufferedImage img = Game.loadImage("/sprites/cutscene/planet" + type + ".png");
        int hue = (int)(Utils.range(0, 360));
        this.image = Utils.hueShift(img, hue);
        this.x = x;
        this.y = y;
        this.angle = Math.random() * Math.PI * 2;

        int scale = (int)(Utils.range(3, 10));
        this.w = image.getWidth() * scale;
        this.h = image.getHeight() * scale;
    }

    public void render(Graphics g, int x, int y, int shadow) {
        Graphics2D g2d = (Graphics2D) g.create();
        x += this.x;
        y += this.y;
        g2d.rotate(angle, x + w / 2f, y + h / 2f);
        g2d.drawImage(Utils.flash(image, 0, 0, 0, shadow), x, this.y, w, h, null);
        g2d.dispose();
        angle += Math.random() / 1000;
    }
}
