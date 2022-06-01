package com.lemon.catacombs.generation.decoration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public interface BuildPattern {
    Decoration[] getPattern(int x, int y, int w, int h);

    class RatioPoint {
        public double x, y;
        public double iw, ih;
        public double w, h;

        public RatioPoint(double x, double y, double iw, double ih) {
            this.x = x;
            this.y = y;
            this.w = 1;
            this.h = 1;
            this.iw = iw;
            this.ih = ih;
        }

        public Decoration apply(DecorationType type, int x, int y, int w, int h) {
            x += (int) (this.x * w / this.iw);
            y += (int) (this.y * h / this.ih);
            w = (int) (this.w * w / this.iw);
            h = (int) (this.h * h / this.ih);
            return new Decoration(type, x, y, w, h);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RatioPoint that = (RatioPoint) o;

            if (Double.compare(that.x, x) != 0) return false;
            return Double.compare(that.y, y) == 0;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    static BuildPattern from(DecorationType type, BufferedImage image) {
        Set<RatioPoint> points = new HashSet<>();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                if (rgb == 0xFFFFFFFF) {
                    points.add(new RatioPoint(x, y, image.getWidth(), image.getHeight()));
                }
            }
        }
        // Merge adjacent rectangles
        boolean merged = true;
        while (merged) {
            merged = false;
            for (RatioPoint p : points) {
                for (RatioPoint q : points) {
                    if (p == q) continue;
                    if (q.x == p.x + p.w && p.y == q.y && p.h == q.h) {
                        p.w += q.w;
                        points.remove(q);
                        merged = true;
                        break;
                    }
                    if (q.y == p.y + p.h && p.x == q.x && p.w == q.w) {
                        p.h += q.h;
                        points.remove(q);
                        merged = true;
                        break;
                    }
                }
                if (merged) break;
            }
        }
        RatioPoint[] pointsArray = points.toArray(new RatioPoint[0]);
        return (x, y, w, h) -> {
            Decoration[] result = new Decoration[pointsArray.length];
            for (int i = 0; i < pointsArray.length; i++) {
                RatioPoint point = pointsArray[i];
                result[i] = point.apply(type, x, y, w, h);
            }
            return result;
        };
    }
}
