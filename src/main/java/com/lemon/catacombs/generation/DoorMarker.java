package com.lemon.catacombs.generation;

public class DoorMarker {
    private final int x, y;
    private final int direction;

    public DoorMarker(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }
}
