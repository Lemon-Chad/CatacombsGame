package com.lemon.catacombs.engine.render;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class BlendSpace<T> {
    private class Node {
        private final float x, y;
        private final T key;

        public Node(float x, float y, T key) {
            this.x = x;
            this.y = y;
            this.key = key;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public T getKey() {
            return key;
        }

        public double distance(float x, float y) {
            return Point.distance(x, y, this.x, this.y);
        }
    }

    private final Set<Node> nodes = new HashSet<>();
    private float x, y;

    public void add(float x, float y, T key) {
        nodes.add(new Node(x, y, key));
    }

    public T get() {
        Node closest = null;
        for (Node node : nodes) {
            if (closest == null || node.distance(x, y) < closest.distance(x, y)) {
                closest = node;
            }
        }
        return closest == null ? null : closest.getKey();
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
