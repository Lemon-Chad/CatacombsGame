package com.lemon.catacombs.engine.pathing;

import com.lemon.catacombs.engine.Game;

import java.awt.*;
import java.util.*;

public class VectorField {

    private static class VectorCache {
        private final Point goal;
        private final Set<Integer> wallCollisionLayer;
        private final int tileSize;

        public VectorCache(Point goal, Set<Integer> wallCollisionLayer, int tileSize) {
            this.goal = goal;
            this.wallCollisionLayer = wallCollisionLayer;
            this.tileSize = tileSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VectorCache that = (VectorCache) o;

            if (!goal.equals(that.goal)) return false;
            if (tileSize != that.tileSize) return false;
            return wallCollisionLayer.equals(that.wallCollisionLayer);
        }

        @Override
        public int hashCode() {
            int result = goal.hashCode();
            result = 31 * result + wallCollisionLayer.hashCode();
            result = 31 * result + tileSize;
            return result;
        }
    }

    private static final Map<VectorCache, Set<VectorField>> cache = new HashMap<>();

    private final int x, y, w, h;
    private final int tileSize;
    private final int[][] heatMap;
    private final Point[][] field;
    private final boolean[][] visited;

    public VectorField(int x, int y, int w, int h, int tileSize) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.tileSize = tileSize;
        heatMap = new int[w / tileSize][h / tileSize];
        field = new Point[w / tileSize][h / tileSize];
        visited = new boolean[w / tileSize][h / tileSize];
    }

    public Point getVelocity(double x, double y) {
        if (x < this.x || x > this.x + w || y < this.y || y > this.y + h) {
            return null;
        }
        return field[(int) (x - this.x) / tileSize][(int) (y - this.y) / tileSize];
    }

    public void init(Point goal, Set<Integer> walls) {
        if (goal.x < this.x || goal.x > this.x + w || goal.y < this.y || goal.y > this.y + h) {
            return;
        }
        generateHeatMap(goal, walls);
        generateField();
    }

    private void generateField() {
        for (int i = 1; i < w / tileSize - 1; i++) {
            for (int j = 1; j < h / tileSize - 1; j++) {
                int current = heatMap[i][j];
                int left = heatMap[i - 1][j];
                left = left == -1 ? current : left;
                int right = heatMap[i + 1][j];
                right = right == -1 ? current : right;
                int up = heatMap[i][j - 1];
                up = up == -1 ? current : up;
                int down = heatMap[i][j + 1];
                down = down == -1 ? current : down;

                Point velocity = new Point(left - right, up - down);
//                double magnitude = velocity.distance(0, 0);
//                velocity.x /= magnitude;
//                velocity.y /= magnitude;
                field[i][j] = velocity;
            }
        }
    }

    private void generateHeatMap(Point goal, Set<Integer> walls) {
        flood((goal.x - this.x) / tileSize, (goal.y - this.y) / tileSize, 0, walls);
    }

    private void flood(int x, int y, int heat, Set<Integer> walls) {
        if (heatMap[x][y] <= heat && visited[x][y]) {
            return;
        }
        visited[x][y] = true;
        if (Game.getInstance().getWorld().blocked(null, new Rectangle(x * tileSize, y * tileSize, tileSize, tileSize), walls)) {
            heatMap[x][y] = -1;
            return;
        }
        heatMap[x][y] = heat;
        if (x > 0) {
            flood(x - 1, y, heat + 1, walls);
        }
        if (x < w / tileSize - 1) {
            flood(x + 1, y, heat + 1, walls);
        }
        if (y > 0) {
            flood(x, y - 1, heat + 1, walls);
        }
        if (y < h / tileSize - 1) {
            flood(x, y + 1, heat + 1, walls);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    private void inherit(VectorField parent) {
        Rectangle intersection = getBounds().intersection(parent.getBounds());
        if (intersection.width == 0 || intersection.height == 0) {
            return;
        }

        for (int x = (intersection.x - this.x) / tileSize; x < intersection.width / tileSize; x++) {
            for (int y = (intersection.y - this.y) / tileSize; y < intersection.height / tileSize; y++) {
                field[x][y] = parent.field[x][y];
                heatMap[x][y] = parent.heatMap[x][y];
                visited[x][y] = parent.visited[x][y];
            }
        }
    }

    public static Point getVelocity(double x, double y, Point goal, double searchRadius, int tileSize, Set<Integer> walls) {
        VectorCache key = new VectorCache(goal, walls, tileSize);
        if (cache.containsKey(key)) {
            for (VectorField field : cache.get(key)) {
                Point velocity = field.getVelocity(x, y);
                if (velocity != null) {
                    return velocity;
                }
            }
        }

        VectorField field = new VectorField((int) (x - searchRadius / 2), (int) (y - searchRadius / 2),
                (int) searchRadius, (int) searchRadius, tileSize);
        if (cache.containsKey(key)) {
            for (VectorField field2 : cache.get(key)) {
                field.inherit(field2);
            }
        }
        field.init(goal, walls);
        cache.putIfAbsent(key, new HashSet<>());
        cache.get(key).add(field);
        return field.getVelocity(x, y) != null ? field.getVelocity(x, y) : new Point(0, 0);
    }
}
