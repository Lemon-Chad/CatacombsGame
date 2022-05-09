package com.lemon.catacombs.engine;

public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector sub(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public Vector mul(double d) {
        return new Vector(x * d, y * d);
    }

    public Vector div(double d) {
        return new Vector(x / d, y / d);
    }

    public double dot(Vector v) {
        return x * v.x + y * v.y;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector normalize() {
        return div(length());
    }

    public Vector normalize(double length) {
        if (length() < length) {
            return this;
        }
        return div(length()).mul(length);
    }

    public Vector rotate(double angle) {
        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vector(x * cos - y * sin, x * sin + y * cos);
    }

    public Vector rotate(Vector v, double angle) {
        return sub(v).rotate(angle).add(v);
    }
}
