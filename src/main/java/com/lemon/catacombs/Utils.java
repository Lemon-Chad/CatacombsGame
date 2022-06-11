package com.lemon.catacombs;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.objects.particles.BloodParticle;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static final Random random = new Random();
    public static final double NORMAL_CONSTANT = 1 / Math.sqrt(2 * Math.PI);

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

    public static int intRange(int min, int max) {
        return (int) range(min, max + 1);
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

    public static Point randomPointInCircle(int radius) {
        double angle = Math.random() * 2 * Math.PI;
        double u = Math.random() + Math.random();
        double r = u > 1 ? 2 - u : u;
        int x = (int) (Math.cos(angle) * r * radius);
        int y = (int) (Math.sin(angle) * r * radius);
        return new Point(x, y);
    }

    public static double normalDistribution(double mean, double stddev) {
        return random.nextGaussian() * stddev + mean;
    }

    public static double normal(double min, double max) {
        return normalDistribution(0.5, 0.1) * (max - min) + min;
    }

    public static Vector circumcenter(Point a, Point b, Point c) {
        double ad = a.x * a.x + a.y * a.y;
        double bd = b.x * b.x + b.y * b.y;
        double cd = c.x * c.x + c.y * c.y;
        double D = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
        double x = (1 / D * (ad * (b.y - c.y) + bd * (c.y - a.y) + cd * (a.y - b.y)));
        double y = (1 / D * (ad * (c.x - b.x) + bd * (a.x - c.x) + cd * (b.x - a.x)));
        return new Vector(x, y);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static boolean intervalOverlap(double a1, double a2, double b1, double b2) {
        return a1 < b2 && b1 < a2;
    }

    public static double intervalMiddle(double a1, double b1, double a2, double b2) {
        // Return middle of intersection of two intervals
        return (Math.max(a1, b1) + Math.min(a2, b2)) / 2;
    }

    private static double constrainRect(double x) {
        // Restrict wave to give position on a rectangle
        return Math.max(-1, Math.min(1, Math.sqrt(2) * x));
    }

    public static Point pointOnRect(int x, int y, int w, int h, double angle) {
        double x1 = constrainRect(Math.cos(angle));
        double y1 = constrainRect(Math.sin(angle));
        double x2 = x + x1 * w / 2;
        double y2 = y + y1 * h / 2;
        return new Point((int) x2, (int) y2);
    }

    public static String[] listFiles(String path) {
        return Game.getInstance().listFiles(path);
    }

    public static BufferedImage flip(BufferedImage image) {
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-image.getWidth(null), 0);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    public static void scale(Graphics2D g, double scale, double cx, double cy) {
        double offsetX = cx * (1 - scale);
        double offsetY = cy * (1 - scale);
        g.scale(scale, scale);
        g.translate(offsetX / scale, offsetY / scale);
    }

    public static void unscale(Graphics2D g, double scale, double cx, double cy) {
        double offsetX = cx * (1 - scale);
        double offsetY = cy * (1 - scale);
        g.translate(-offsetX / scale, -offsetY / scale);
        g.scale(1 / scale, 1 / scale);
    }

    public static boolean contains(Rectangle rect, Polygon p) {
        for (int i = 0; i < p.npoints; i++) {
            int x = p.xpoints[i];
            int y = p.ypoints[i];
            if (!rect.contains(x, y)) {
                return false;
            }
        }
        return true;
    }

    public static Polygon polyRect(Point p, Dimension d) {
        return polyRect(p.x, p.y, d.width, d.height);
    }

    public static Polygon polyRect(int x, int y, int w, int h) {
        int[] xpoints = { x, x, x + w, x + w };
        int[] ypoints = { y, y + h, y + h, y };
        return new Polygon(xpoints, ypoints, 4);
    }

}
