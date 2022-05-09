package com.lemon.catacombs.engine.pathing;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Graph {
    private class Node {
        public int x;
        public int y;
        public Set<Node> neighbors;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
            neighbors = new HashSet<>();
        }

        public void addNeighbor(Node neighbor) {
            neighbors.add(neighbor);
        }

        public Set<Node> getNeighbors() {
            return neighbors;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (x != node.x) return false;
            return y == node.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    private class Edge {
        public Node from;
        public Node to;
        public double cost;

        public Edge(Node from, Node to, double cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (cost != edge.cost) return false;
            if (!from.equals(edge.from)) return false;
            return to.equals(edge.to);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = from.hashCode();
            result = 31 * result + to.hashCode();
            temp = Double.doubleToLongBits(cost);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    private Set<Node> nodes;
    private Set<Edge> edges;

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public void addVertex(int x, int y) {
        this.nodes.add(new Node(x, y));
    }

    public void addVertex(Point p) {
        addVertex(p.x, p.y);
    }

    public void addEdge(int x1, int y1, int x2, int y2, double cost) {
        Node from = getNode(x1, y1);
        Node to = getNode(x2, y2);
        Edge edge = new Edge(from, to, cost);
        this.edges.add(edge);
        from.addNeighbor(to);
        to.addNeighbor(from);
    }

    public void addEdge(Point from, Point to) {
        addEdge(from.x, from.y, to.x, to.y, from.distance(to));
    }

    private Node getNode(int x1, int y1) {
        for (Node node : nodes) {
            if (node.hashCode() == 31 * x1 + y1) {
                return node;
            }
        }
        Node node = new Node(x1, y1);
        nodes.add(node);
        return node;
    }

}
