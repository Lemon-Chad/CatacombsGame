package com.lemon.catacombs.engine.physics;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Handler {
    private final Set<GameObject> objects = new HashSet<>();
    private final Map<Integer, CollisionLayer> collisionLayers = new HashMap<>();

    public void tick() {
        for (GameObject object : objects) {
            object.tick();
        }
    }

    public void collisions() {
        for (GameObject object : new HashSet<>(objects)) {
            for (int layer : object.getCollisionMask()) {
                CollisionLayer collisionLayer = collisionLayers.computeIfAbsent(layer, CollisionLayer::new);
                for (GameObject collisionObject : collisionLayer.getObjects())
                    if (object.collidesWith(collisionObject)) {
                        object.collision(collisionObject);
                    }
            }
        }
    }

    public boolean blocked(Point location, Set<Integer> collisionMask) {
        for (int layer : collisionMask) {
            if (collisionLayers.computeIfAbsent(layer, CollisionLayer::new).getObjects().stream()
                    .anyMatch(gameObject -> gameObject.getBounds().contains(location))) {
                return true;
            }
        }
        return false;
    }

    public void render(Graphics g) {
        List<GameObject> ySortedObjects = new LinkedList<>(objects);
        ySortedObjects.sort(Comparator.comparingInt(GameObject::getYSort));
        for (GameObject object : ySortedObjects) {
            object.render(g);
        }
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void removeObject(GameObject object) {
        objects.remove(object);
    }

    public Set<GameObject> getObjects() {
        return objects;
    }

    public void addToLayer(GameObject object, int layer) {
        collisionLayers.computeIfAbsent(layer, CollisionLayer::new).add(object);
    }

    public void removeFromLayer(GameObject gameObject, int layer) {
        Optional.ofNullable(collisionLayers.get(layer)).ifPresent(collisionLayer -> collisionLayer.remove(gameObject));
    }
}
