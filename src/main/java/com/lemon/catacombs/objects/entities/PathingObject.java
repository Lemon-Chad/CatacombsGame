package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.engine.pathing.Pathfinder;
import com.lemon.catacombs.engine.pathing.VectorField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

abstract public class PathingObject extends Damageable {
    private @Nullable Point target;
    private @Nullable List<Point> path;
    private boolean reachable = false;
    private boolean visualize = false;

    private int pathCooldown = 0;
    private final Set<Integer> obstacles;
    private final Map<Integer, Integer> costs;

    public PathingObject(int x, int y, int id, int[] solids, int health) {
        super(x, y, id, solids, health);
        obstacles = new HashSet<>();
        costs = new HashMap<>();
    }

    protected void addObstacle(int id) {
        obstacles.add(id);
    }

    protected void addCost(int id, int cost) {
        costs.put(id, cost);
    }

    protected void removeObstacle(int id) {
        obstacles.remove(id);
    }

    protected void removeCost(int id) {
        costs.remove(id);
    }

    protected Vector path(int maxDepth, int stepSize, int speed) {
        if (target == null) return new Vector(0, 0);
        if (pathCooldown > 0) {
            pathCooldown--;
        } else {
            // Recalculate path
            regeneratePath(maxDepth, stepSize);
        }

        // Do pathing
        if (path == null) {
            reachable = false;
            return new Vector(0, 0);
        }
        reachable = true;
        // Move to next point
        if (!path.isEmpty()) {
            Point next = path.get(0);
            while (next.distance(x, y) < 1 && !path.isEmpty()) {
                path.remove(0);
                if (!path.isEmpty())
                    next = path.get(0);
            }
            if (path.isEmpty()) {
                return new Vector(0, 0);
            }

            Vector velocity = new Vector(next.x - getX(), next.y - getY());
            return velocity.normalize(speed);
        }
        return new Vector(0, 0);
    }

    private void regeneratePath(int maxDepth, int stepSize) {
        if (target == null || target.distance(x, y) > stepSize * maxDepth) {
            // Target is too far away
            path = null;
            return;
        }
        path = Pathfinder.findPath(this, getBounds(), target, obstacles, costs, maxDepth, stepSize);
        if (path != null) {
            path.remove(0);
            path.add(target);
        }
        pathCooldown = 5;
    }

    public boolean isReachable() {
        return reachable;
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        if (visualize) {
            if (path != null) {
                g.setColor(new Color(0, 255, 0, 100));
                for (Point p : path) {
                    g.fillRect(p.x, p.y, 32, 32);
                }
            }
        }
    }

    public void setTarget(@NotNull Point target) {
        this.target = target;
    }

    public void clearTarget() {
        target = null;
    }

    public @Nullable Point getTarget() {
        return target;
    }

    protected void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }
}
