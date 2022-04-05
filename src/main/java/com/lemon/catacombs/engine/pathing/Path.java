package com.lemon.catacombs.engine.pathing;

import java.awt.*;
import java.util.List;

public class Path {
    private final int x, y;
    private final double length;
    private final List<Point> points;

    public Path(int x, int y, double length, List<Point> points) {
        this.x = x;
        this.y = y;
        this.length = length;
        this.points = points;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getLength() {
        return length;
    }

    public Point getStep(int index) {
        return points.get(index);
    }

    public void prependStep(int x, int y) {
        points.add(0, new Point(x, y));
    }

    public void appendStep(int x, int y) {
        points.add(new Point(x, y));
    }

    public boolean contains(int x, int y) {
        return points.contains(new Point(x, y));
    }

    public static double heuristic(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
