package com.lemon.catacombs.engine.pathing;

import com.lemon.catacombs.engine.Game;

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

    private List<Point> findPath(Point start) {
        Set<Step> open = new HashSet<>();
        List<Step> closed = new LinkedList<>();

        double ID = Math.random();

        open.add(new Step(start, null, 0, 0));
        while (!open.isEmpty()) {
            System.out.println(ID + " Open:" + open.size());
            System.out.println(ID + " Closed:" + closed.size());
            Step current = open.iterator().next();
            open.remove(current);
            closed.add(current);

            if (current.location.distance(goal) <= stepSize) {
                return current.path();
            }

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }

                    Point next = new Point(current.location.x + i * stepSize, current.location.y + j * stepSize);
                    int g = current.g + 1;
                    int h = Math.abs(next.x - goal.x) + Math.abs(next.y - goal.y);
                    Step step = new Step(next, current, g, h);

                    if (closed.contains(step) || Game.getInstance().getWorld().blocked(next, wallMask) || g > maxDepth) {
                        continue;
                    }

                    open.add(step);
                }
            }
        }
        return null;
    }

    public static List<Point> findPath(Point start, Point end, Set<Integer> wallMask, int maxDepth, int stepSize) {
        return new Pathfinder(end, wallMask, maxDepth, stepSize).findPath(start);
    }
}
