package com.lemon.catacombs.engine.pathing;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;

public class Pathfinder {
    private final Point goal;
    private final Set<Integer> wallMask;
    private final int maxDepth;
    private final int stepSize;

    private Pathfinder(Point goal, Set<Integer> wallMask, int maxDepth, int stepSize) {
        this.goal = goal;
        this.wallMask = wallMask;
        this.maxDepth = maxDepth;
        this.stepSize = stepSize;
    }

    private List<Point> findPath(GameObject pather, Rectangle start) {
        start = new Rectangle(Math.floorDiv(start.x, stepSize) * stepSize, Math.floorDiv(start.y, stepSize) * stepSize, start.width, start.height);

        List<Step> open = new LinkedList<>();
        List<Step> closed = new LinkedList<>();

        open.add(new Step(start.getLocation(), null, 0, 0));
        while (!open.isEmpty()) {
            // get the next step with the lowest F cost
            Step current = open.iterator().next();
            for (Step step : open) {
                if (step.f() < current.f()) {
                    current = step;
                } else if (step.f() == current.f() && step.h < current.h) {
                    current = step;
                }
            }
            open.remove(current);
            closed.add(current);

            if (current.location.distance(goal) <= stepSize) {
                return current.path();
            }

            for (int i = 0; i < 4; i++) {
                Point next = new Point(current.location);
                switch (i) {
                    case 0:
                        next.x += stepSize;
                        break;
                    case 1:
                        next.x -= stepSize;
                        break;
                    case 2:
                        next.y += stepSize;
                        break;
                    case 3:
                        next.y -= stepSize;
                        break;
                }
                int g = current.g + stepSize;
                int h = Math.abs(next.x - goal.x) + Math.abs(next.y - goal.y);
                Step step = new Step(next, current, g, h);

                if (open.contains(step) || closed.contains(step) || Game.getInstance().getWorld().blocked(pather, new Rectangle(next, start.getSize()), wallMask) || g > maxDepth * stepSize) {
                    continue;
                }

                open.add(step);
            }
        }
        return null;
    }

    public static List<Point> findPath(GameObject pather, Rectangle start, Point end, Set<Integer> wallMask, int maxDepth, int stepSize) {
        return new Pathfinder(end, wallMask, maxDepth, stepSize).findPath(pather, start);
    }
}
