package com.lemon.catacombs.generation.decoration;

import java.awt.*;

public class Decoration {
    private final int x, y;
    private final int w, h;
    private final DecorationType type;

    public Decoration(DecorationType type, int x, int y, int w, int h) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public DecorationType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Decoration that = (Decoration) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + type.hashCode();
        return result;
    }

    public boolean intersects(Decoration other) {
        return new Rectangle(x, y, w, h).intersects(new Rectangle(other.x, other.y, other.w, other.h));
    }
}
