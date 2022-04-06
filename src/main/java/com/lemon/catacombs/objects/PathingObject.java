package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.pathing.Pathfinder;
import com.lemon.catacombs.engine.physics.CollisionObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

abstract public class PathingObject extends CollisionObject {
    private @Nullable Point target;
    private @Nullable List<Point> path;
    private boolean reachable = false;
    private boolean visualize = false;

    private int pathCooldown = 0;

    public PathingObject(int x, int y, int id, int[] solids) {
        super(x, y, id, solids);
    }

    protected void path(int maxDepth, int stepSize) {
        if (target == null) return;
        if (pathCooldown > 0) {
            pathCooldown--;
        } else {
            // Recalculate path
            path = Pathfinder.findPath(this, getBounds(), target, getCollisionMask(), maxDepth, stepSize);
            if (path != null)
                path.remove(0);
            pathCooldown = 10;
        }

        // Do pathing
        if (path == null) {
            reachable = false;
            return;
        }
        reachable = true;
        // Move to next point
        if (!path.isEmpty()) {
            Point next = path.get(0);
            while (next.distance(x, y) < 8 && !path.isEmpty()) {
                path.remove(0);
                if (!path.isEmpty())
                    next = path.get(0);
            }
            if (path.isEmpty()) {
                return;
            }

            setVelX(next.x - getX());
            setVelY(next.y - getY());

            normalizeVelocity();
        }
    }

    public boolean isReachable() {
        return reachable;
    }

    @Override
    public void render(Graphics g) {
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
