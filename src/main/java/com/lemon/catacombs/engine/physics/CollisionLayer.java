package com.lemon.catacombs.engine.physics;

import java.util.HashSet;
import java.util.Set;

public class CollisionLayer {
    private final int id;
    private final Set<GameObject> objects;

    public CollisionLayer(int id) {
        this(id, new HashSet<>());
    }

    public CollisionLayer(int id, Set<GameObject> objects) {
        this.objects = objects;
        this.id = id;
    }

    public Set<GameObject> getObjects() {
        return new HashSet<>(objects);
    }

    public void add(GameObject object) {
        objects.add(object);
    }

    public void remove(GameObject object) {
        objects.remove(object);
    }

    public int getId() {
        return id;
    }
}
