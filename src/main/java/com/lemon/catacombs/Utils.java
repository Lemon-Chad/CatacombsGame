package com.lemon.catacombs;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.particles.BloodParticle;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;

public class Utils {
    public static double approachZero(double n, double dn) {
        if (n > 0) {
            return Math.max(n - dn, 0);
        } else {
            return Math.min(n + dn, 0);
        }
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

            Game.getInstance().getWorld().addParticle(p);
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

    public static int hue(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));

        double h = 0;
        if (max == min) {
            h = 0;
        } else if (max == r) {
            h = 60 * (g - b) / (max - min);
        } else if (max == g) {
            h = 60 * (b - r) / (max - min) + 120;
        } else if (max == b) {
            h = 60 * (r - g) / (max - min) + 240;
        }

        h %= 360;

        return (int) h;
    }

    public static BufferedImage hueShift(BufferedImage sprite, int hue) {
        // Shift all pixels by hue
        BufferedImage hueShifted = new BufferedImage(sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = hueShifted.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
        g2d.fillRect(0, 0, hueShifted.getWidth(), hueShifted.getHeight());
        for (int x = 0; x < sprite.getWidth(); x++) {
            for (int y = 0; y < sprite.getHeight(); y++) {
                Color c = new Color(sprite.getRGB(x, y), true);
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                float newHue = (hsb[0] + hue / 360f) % 1f;
                Color newColor = Color.getHSBColor(newHue, hsb[1], hsb[2]);
                newColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), c.getAlpha());
                hueShifted.setRGB(x, y, newColor.getRGB());
            }
        }
        return hueShifted;
    }

    public static BufferedImage alpha(BufferedImage img, int a) {
        BufferedImage alpha = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = alpha.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a / 255f));
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return alpha;
    }
}
