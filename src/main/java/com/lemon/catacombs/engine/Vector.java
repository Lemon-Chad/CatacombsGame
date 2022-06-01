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

    public double invLength() {
        float length = (float) (x * x + y * y);
        int i;
        float x2, y;
        final float threehalfs = 1.5F;

        x2 = length * 0.5F;
        y = length;
        i = Float.floatToIntBits(y);
        i = 0x5f3759df - (i >> 1);
        y = Float.intBitsToFloat(i);
        y = y * (threehalfs - (x2 * y * y));

        return y;
    }

    public Vector normalize() {
        return mul(invLength());
    }

    public Vector normalize(double length) {
        if (x * x + y * y < length * length) {
            return this;
        }
        return normalize().mul(length);
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

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
