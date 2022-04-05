package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.pathing.Pathfinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

abstract public class PathingObject extends CollisionObject {
    private @Nullable Point target;
    private @Nullable List<Point> path;
    private boolean visualize = false;

    public PathingObject(int x, int y, ID id, ID[] solids) {
        super(x, y, id, solids);
    }

    protected void path(int maxDepth, int stepSize) {
        if (target == null) return;
        // Do pathing
        path = Pathfinder.findPath(new Point(getX(), getY()), target, getCollisionMask(), maxDepth, stepSize);
        if (path == null) return;
        // Move to next point
        if (path.size() > 1) {
            Point next = path.get(1);

            setVelX(next.x - getX());
            setVelY(next.y - getY());

            normalizeVelocity();

            System.out.println(next.x + " " + next.y);
        }
    }

    @Override
    public void render(Graphics g) {
        if (visualize) {
            if (path != null) {
                g.setColor(new Color(0, 255, 0, 100));
                for (Point p : path) {
                    g.fillRect(p.x, p.y, 16, 16);
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
