package com.lemon.catacombs.engine.pathing;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Pathfinder {
    private final Point goal;
    private final Set<Integer> wallMask;
    private final Map<Integer, Integer> costs;
    private final int maxDepth;
    private final int stepSize;

    private Pathfinder(Point goal, Set<Integer> wallMask, Map<Integer, Integer> costs, int maxDepth, int stepSize) {
        this.goal = goal;
        this.wallMask = wallMask;
        this.costs = costs;
        this.maxDepth = maxDepth;
        this.stepSize = stepSize;
    }

    private List<Point> findPath(GameObject pather, Rectangle start) {
        if (blocked(pather, new Rectangle((int) goal.getX(), (int) goal.getY(), stepSize, stepSize), wallMask)) {
            return null;
        }

        start = new Rectangle((int) Math.floor((float) start.x / stepSize) * stepSize, (int) Math.floor((float) start.y / stepSize) * stepSize, start.width, start.height);

        List<Step> open = new LinkedList<>();
        List<Step> closed = new LinkedList<>();

        open.add(new Step(start.getLocation(), null, 0, 0));
        while (!open.isEmpty()) {
            // get the next step with the lowest F cost
            Step current = open.get(0);
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

            for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) continue;
                Point next = new Point(current.location);
                next.translate(x * stepSize, y * stepSize);
                int g = current.g + stepSize;
                int h = Math.abs(next.x - goal.x) + Math.abs(next.y - goal.y) + costOf(pather, current, start, x, y);
                Step step = new Step(next, current, g, h);

                if (open.contains(step) || closed.contains(step) || g > maxDepth * stepSize) {
                    continue;
                }

                if (diagonalBlock(pather, current, start, x, y, wallMask)) {
                    continue;
                }

                open.add(step);
            }
        }
        return null;
    }

    private boolean diagonalBlock(GameObject pather, Step current, Rectangle start, int x, int y, Set<Integer> mask) {
        if (x != 0 && blocked(pather, new Rectangle(new Point(current.location.x + x * stepSize, current.location.y), start.getSize()), mask))
            return true;
        if (y != 0 && blocked(pather, new Rectangle(new Point(current.location.x, current.location.y + y * stepSize), start.getSize()), mask))
            return true;
        return x != 0 && y != 0 && blocked(pather, new Rectangle(new Point(current.location.x + x * stepSize, current.location.y + y * stepSize), start.getSize()), mask);
    }

    private int costOf(GameObject pather, Step current, Rectangle start, int x, int y) {
        int cost = 0;
        for (int layer : costs.keySet()) {
            Set<Integer> mask = new HashSet<>();
            mask.add(layer);
            if (diagonalBlock(pather, current, start, x, y, mask)) {
                cost += costs.get(layer);
            }
        }
        return cost;
    }

    private boolean blocked(GameObject pather, Rectangle start, Set<Integer> mask) {
        return Game.getInstance().getWorld().blocked(pather, start, mask);
    }

    public static List<Point> findPath(GameObject pather, Rectangle start, Point end, Set<Integer> wallMask, Map<Integer, Integer> costs, int maxDepth, int stepSize) {
        return new Pathfinder(end, wallMask, costs, maxDepth, stepSize).findPath(pather, start);
    }
}
