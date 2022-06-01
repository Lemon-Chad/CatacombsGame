package com.lemon.catacombs.generation;

import com.lemon.catacombs.generation.decoration.Room;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class Hallway {
    private final int depth;
    private final @Nullable Hallway parent;
    private final @Nullable Room room;
    private final @Nullable DoorMarker door;
    private final int x;
    private final int y;
    private final int from;
    private final int[] to;

    public Hallway(int x, int y) {
        this(0, null, x, y, 0, new int[] {
                Direction.UP,
                Direction.RIGHT,
                Direction.DOWN,
                Direction.LEFT
        }, null, null);
    }

    public Hallway(int x, int y, int from){
        this(0, null, x, y, from, Direction.toFrom(from), null, null);
    }

    public Hallway(@NotNull Hallway parent, int x, int y, int from) {
        this(parent.depth + 1, parent, x, y, from, Direction.toFrom(from), null, null);
    }

    private Hallway(int depth, @Nullable Hallway parent, int x, int y, int from, int[] to, @Nullable Room room, @Nullable DoorMarker door) {
        this.depth = depth;
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.from = from;
        this.to = to;
        this.room = room;
        this.door = door;
    }

    public Hallway(int x, int y, int from, @NotNull Room room, @NotNull DoorMarker door) {
        this(0, null, x, y, from, Direction.toFrom(from), room, door);
    }

    public boolean extend() {
        return Math.random() < Math.pow(0.75, depth);
    }

    public int[] getTo() {
        return to;
    }

    public Hallway[] branches() {
        Hallway[] branches = new Hallway[to.length];
        int branchCount = 0;
        while (branchCount == 0) for (int i = 0; i < to.length; i++) {
            if (Math.random() > 1 / 3f) {
                continue;
            }
            branchCount++;
            Point p = Direction.toPoint(to[i]);
            branches[i] = new Hallway(this, x + p.x, y + p.y, Direction.flip(to[i]));
        }
        Hallway[] result = new Hallway[branchCount];
        for (Hallway branch : branches) {
            if (branch != null) {
                result[--branchCount] = branch;
            }
        }
        return result;
    }

    public Hallway forward() {
        int direction = Direction.flip(from);
        Point p = Direction.toPoint(direction);
        return new Hallway(this, x + p.x, y + p.y, from);
    }

    public Point getPosition() {
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hallway hallway = (Hallway) o;

        if (x != hallway.x) return false;
        return y == hallway.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public @Nullable Hallway getParent() {
        return parent;
    }

    public @Nullable Room getRoom() {
        return room;
    }

    public @Nullable DoorMarker getDoor() {
        return door;
    }
}
