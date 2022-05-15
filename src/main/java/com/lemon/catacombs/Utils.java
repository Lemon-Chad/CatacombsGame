package com.lemon.catacombs;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.particles.BloodParticle;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Utils {
    public static double approachZero(double n, double dn) {
        return Math.max(0, Math.abs(n) - dn) * Math.signum(n);
    }

    public static double approach(double n, double t, double dn) {
        return t + approachZero(n - t, dn);
    }

    public static void bloodsplosion(double x, double y, double amount, double min, double max) {
        for (int i = 0; i < Math.ceil(amount / 4); i++) {
            double angle = Math.random() * Math.PI * 2;
            double force = Math.random() * (max - min) + min;

            double dx = Math.cos(angle) * force;
            double dy = Math.sin(angle) * force;

            double lowerSize = Math.ceil(Math.pow(i, 0.25));
            double upperSize = Math.ceil(2 * Math.pow(i, 0.33));
            double size = Math.random() * (upperSize - lowerSize) + lowerSize;

            BloodParticle p = new BloodParticle((int) x, (int) y, (int) size);
            p.setVelX((float) dx);
            p.setVelY((float) dy);

            Game.getInstance().getWorld().addObject(p);
        }
    }

    public static double range(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static Point rotate(Point p, double angle) {
        double x = p.x * Math.cos(angle) - p.y * Math.sin(angle);
        double y = p.x * Math.sin(angle) + p.y * Math.cos(angle);
        return new Point((int) x, (int) y);
    }

    public static Point rotate(Point p, double angle, Point origin) {
        p.translate(-origin.x, -origin.y);
        p = rotate(p, angle);
        p.translate(origin.x, origin.y);
        return p;
    }

    public static BufferedImage scale(BufferedImage img, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return scaled;
    }

    public static BufferedImage flash(BufferedImage img) {
        return flash(img, 255, 255, 255, 255);
    }

    public static BufferedImage flash(BufferedImage img, int r, int g, int b, int a) {
        BufferedImage flash = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flash.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1f));
        g2d.setColor(new Color(r, g, b, a));
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.dispose();
        return flash;
    }
}
