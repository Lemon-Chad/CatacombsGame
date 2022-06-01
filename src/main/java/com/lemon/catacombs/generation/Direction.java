package com.lemon.catacombs.generation;

import java.awt.*;

public class Direction {
    public static final int UP = 1 << 0;
    public static final int DOWN = 1 << 1;
    public static final int LEFT = 1 << 2;
    public static final int RIGHT = 1 << 3;
    public static final int UP_LEFT = UP | LEFT;
    public static final int UP_RIGHT = UP | RIGHT;
    public static final int DOWN_LEFT = DOWN | LEFT;
    public static final int DOWN_RIGHT = DOWN | RIGHT;

    public static Point toPoint(int direction) {
        Point point = new Point();
        if ((direction & UP) != 0) {
            point.y--;
        }
        if ((direction & DOWN) != 0) {
            point.y++;
        }
        if ((direction & LEFT) != 0) {
            point.x--;
        }
        if ((direction & RIGHT) != 0) {
            point.x++;
        }
        return point;
    }


    public static int[] toFrom(int from) {
        int[] to = new int[3];
        for (int i = 0; i < 4; i++) {
            int j = 1 << i;
            if (j == from) continue;
            to[i - (j > from ? 1 : 0)] = j;
        }
        return to;
    }

    public static String toString(int direction) {
        switch (direction) {
            case UP:
                return "UP";
            case DOWN:
                return "DOWN";
            case LEFT:
                return "LEFT";
            case RIGHT:
                return "RIGHT";
            default:
                return "NONE";
        }
    }

    public static int flip(int direction) {
        // Right -> Left, Down -> Up, etc.
        switch (direction) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                return direction;
        }
    }
}
