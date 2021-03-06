package com.lemon.catacombs.engine.pathing;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.CollisionLayer;
import com.lemon.catacombs.engine.physics.GameObject;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class QuadTree {
    public static int INNOVATION = 0;

    private static class QuadTreeData {
        public final int mask;
        private final int x, y, w, h;

        public QuadTreeData(int mask, int x, int y, int w, int h) {
            this.mask = mask;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            QuadTreeData that = (QuadTreeData) o;

            return mask == that.mask;
        }

        @Override
        public int hashCode() {
            return mask;
        }

        @Override
        public String toString() {
            return "QuadTreeData{" +
                    "mask=" + mask +
                    ", x=" + x +
                    ", y=" + y +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }
    public static final Map<QuadTreeData, QuadTree> quadTrees = new HashMap<>();

    private static class Node {
        private final int x, y;
        private final GameObject value;

        public Node(int x, int y, GameObject value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

        public GameObject getValue() {
            return value;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (x != node.x) return false;
            if (y != node.y) return false;
            return value.equals(node.value);
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            result = 31 * result + value.hashCode();
            return result;
        }
    }

    public static final int MIN_SUBDIVISION_SIZE = 32;
    public static final int MAX_OBJECTS = 3;

    public static final int PIXEL_SIZE = 32;

    private QuadTree parent;
    private final QuadTree[] children = new QuadTree[4];
    private final Set<Node> nodes = new HashSet<>();
    private final Set<GameObject> objects = new HashSet<>();
    public final int depth;
    private final Rectangle bounds;
    private final boolean canSplit;
    private boolean hasSplit;
    public final int id;

    public QuadTree(QuadTree parent, int depth, Rectangle bounds) {
        this.parent = parent;
        this.depth = depth;
        this.bounds = bounds;
        this.id = INNOVATION++;
        canSplit = bounds.getWidth() > MIN_SUBDIVISION_SIZE && bounds.getHeight() > MIN_SUBDIVISION_SIZE;
    }

    public void insert(GameObject object) {
        Rectangle objectBounds = object.getBounds();
        if (!bounds.intersects(objectBounds)) {
            return;
        }

        for (int x = objectBounds.x; x <= objectBounds.x + objectBounds.width; x += PIXEL_SIZE) {
            for (int y = objectBounds.y; y <= objectBounds.y + objectBounds.height; y += PIXEL_SIZE) {
                insert(new Node(x, y, object));
            }
        }
    }

    public void insert(Node node) {
        if (!bounds.contains(node.getX(), node.getY())) return;
        addNode(node);
        if (canSplit && nodes.size() > MAX_OBJECTS) {
            if (!hasSplit) {
                split();
            } else {
                index(node);
            }
        }
    }

    private void addNode(Node node) {
        nodes.add(node);
        objects.add(node.getValue());
    }

    private void split() {
        if (hasSplit) return;
        hasSplit = true;
        for (int i = 0; i < 4; i++) {
            children[i] = new QuadTree(this, depth + 1, getChildBounds(i));
        }
        for (Node node : nodes) {
            index(node);
        }
    }

    public void index(Node node) {
        indexChild(0, node);
        indexChild(1, node);
        indexChild(2, node);
        indexChild(3, node);
    }

    public void indexChild(int i, Node node) {
        if (children[i].bounds.intersects(node.getValue().getBounds())) {
            children[i].insert(node);
        }
    }

    private @NotNull Rectangle getChildBounds(int index) {
        switch (index) {
            case 0:
                return new Rectangle(bounds.x, bounds.y, bounds.width / 2, bounds.height / 2);
            case 1:
                return new Rectangle(bounds.x + bounds.width / 2, bounds.y, bounds.width / 2, bounds.height / 2);
            case 2:
                return new Rectangle(bounds.x, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2);
            case 3:
                return new Rectangle(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2, bounds.width / 2, bounds.height / 2);
            default:
                throw new IllegalArgumentException("Invalid index: " + index);
        }
    }

    public Point getCenter() {
        return new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public QuadTree[] getChildren() {
        return children;
    }

    public Set<GameObject> getObjects() {
        return objects;
    }

    public Set<QuadTree> getTrees(Polygon shape) {
        Set<QuadTree> trees = new HashSet<>();
        if (!Utils.contains(bounds, shape)) {
            return trees;
        }
        for (QuadTree tree : children) {
            if (tree != null && Utils.contains(tree.bounds, shape)) {
                trees.addAll(tree.getTrees(shape));
            }
        }
        if (trees.size() == 0) {
            trees.add(this);
        }
        return trees;
    }

    public QuadTree getTree(int x, int y) {
        if (!bounds.contains(x, y)) return null;
        for (QuadTree child : children) {
            if (child != null && child.bounds.contains(x, y)) {
                return child.getTree(x, y);
            }
        }
        return this;
    }

    public static Set<QuadTree> build(int[] collisionMask, int x, int y, int width, int height) {
        Set<QuadTree> trees = new HashSet<>();
        for (int i : collisionMask) {
            trees.add(build(i, x, y, width, height));
        }
        return trees;
    }

    public static QuadTree build(int layer, int x, int y, int width, int height) {
        QuadTreeData data = new QuadTreeData(layer, x, y, width, height);
        if (quadTrees.containsKey(data)) {
            return quadTrees.get(data);
        }

        QuadTree quadTree = new QuadTree(null, 0, new Rectangle(x, y, width, height));

        CollisionLayer collisionLayer = Game.getInstance().getWorld().getLayer(layer);
        if (collisionLayer != null) {
            for (GameObject object : collisionLayer.getObjects()) {
                quadTree.insert(object);
            }
        }

        quadTrees.put(data, quadTree);
        return quadTree;
    }

    public static void clear() {
        quadTrees.clear();
    }

    public boolean empty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    public boolean contains(GameObject object) {
        return nodes.contains(new Node(object.getX(), object.getY(), object));
    }

    public Set<Point> traversable() {
        Set<Point> traversable = new HashSet<>();
        if (empty()) {
            traversable.add(new Point(bounds.x, bounds.y));
            traversable.add(new Point(bounds.x + bounds.width, bounds.y));
            traversable.add(new Point(bounds.x, bounds.y + bounds.height));
            traversable.add(new Point(bounds.x + bounds.width, bounds.y + bounds.height));
        }
        for (QuadTree child : children) {
            if (child != null) {
                traversable.addAll(child.traversable());
            }
        }
        return traversable;
    }

//    public Graph getGraph() {
//        Set<Point> points = traversable();
//        Graph triangulation = new Graph();
//
//        // Add super triangle
//        Point p1 = new Point(bounds.x - bounds.width / 2, bounds.y - bounds.height);
//        Point p2 = new Point((int) (bounds.x + 1.5 * bounds.width), bounds.y - bounds.height);
//        Point p3 = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height);
//
//        triangulation.addEdge(p1, p2);
//        triangulation.addEdge(p2, p3);
//        triangulation.addEdge(p3, p1);
//
//        // Add points
//        for (Point point : points) {
//            Set<>
//        }
//
//        return triangulation;
//    }
}
