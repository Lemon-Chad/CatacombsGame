package com.lemon.catacombs.engine.pathing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Step {
    public @NotNull final Point location;
    public @Nullable final Step previousStep;
    public final int g;
    public final int h;

    public Step(@NotNull Point location, @Nullable Step previousStep, int g, int h) {
        this.location = location;
        this.previousStep = previousStep;
        this.g = g;
        this.h = h;
    }

    public int f() {
        return g + h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return location.equals(step.location);
    }

    public List<Point> path() {
        List<Point> path = new LinkedList<>();
        Step step = this;
        while (step != null) {
            path.add(0, step.location);
            step = step.previousStep;
        }
        return path;
    }
}
