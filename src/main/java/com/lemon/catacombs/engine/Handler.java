package com.lemon.catacombs.engine;

import com.lemon.catacombs.engine.pathing.QuadTree;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.physics.CollisionLayer;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.engine.render.YSortable;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Handler {
    private final Set<GameObject> objects = new HashSet<>();
    private final Set<GameObject> objectsToRemove = new HashSet<>();
    private final Set<GameObject> objectsToAdd = new HashSet<>();
    private final Set<UIComponent> uiObjects = new HashSet<>();
    private final Set<UIComponent> uiObjectsToRemove = new HashSet<>();
    private final Set<UIComponent> uiObjectsToAdd = new HashSet<>();
    private final Map<Integer, CollisionLayer> collisionLayers = new HashMap<>();

    private static final int quadTreeRadius = 20;

    public void tick() {
        for (GameObject object : objects) {
            object.tick();
        }
        for (UIComponent uiComponent : uiObjects) {
            uiComponent.tick();
        }

        for (GameObject object : objectsToRemove) {
            objects.remove(object);
            for (CollisionLayer collisionLayer : collisionLayers.values()) {
                collisionLayer.remove(object);
            }
        }
        objectsToRemove.clear();
        objects.addAll(objectsToAdd);
        objectsToAdd.clear();
        uiObjects.removeAll(uiObjectsToRemove);
        uiObjectsToRemove.clear();
        uiObjects.addAll(uiObjectsToAdd);
        uiObjectsToAdd.clear();
    }

    public QuadTree quadTreeForLayers(int[] mask) {
        Player player = Game.getInstance().getPlayer();
        QuadTree.INNOVATION = 0;
        return QuadTree.build(mask, 0, 0, quadTreeRadius * 64, quadTreeRadius * 64);
    }

    public void collisions() {
        for (GameObject object : objects) {
            for (int layer : object.getCollisionMask()) {
                CollisionLayer collisionLayer = collisionLayers.computeIfAbsent(layer, CollisionLayer::new);
                for (GameObject collisionObject : collisionLayer.getObjects())
                    if (object != collisionObject && object.collidesWith(collisionObject)) {
                        object.collision(collisionObject);
                    }
            }
        }
    }

    public boolean blocked(GameObject origin, Rectangle location, Set<Integer> collisionMask) {
        for (int layer : collisionMask) {
            CollisionLayer collisionLayer = collisionLayers.computeIfAbsent(layer, CollisionLayer::new);
            for (GameObject object : collisionLayer.getObjects()) {
                if (object != origin && object.getBounds().intersects(location)) {
                    return true;
                }
            }
        }
        return false;
//        int[] mask = new int[collisionMask.size()];
//        int i = 0;
//        for (int layer : collisionMask) {
//            mask[i] = layer;
//            i++;
//        }
//        QuadTree quadTree = quadTreeForLayers(mask);
//        for (QuadTree quad : quadTree.getTrees(location)) {
//            Set<GameObject> objects = quad.getObjects();
//            if (!quad.empty() && !(objects.size() == 1 && objects.contains(origin))) {
//                return true;
//            }
//        }
//        return false;
    }

    public void render(Graphics g) {
        for (GameObject object : YSortable.sort(objects)) {
            object.render(g);
        }
    }

    private void renderTree(QuadTree quadTree, Graphics2D g) {
        g.setStroke(new BasicStroke(3));
        g.setColor(quadTree.empty() ? Color.GREEN : Color.RED);
        Rectangle bounds = quadTree.getBounds();
        for (QuadTree child : quadTree.getChildren()) {
            if (child != null) {
                renderTree(child, g);
            }
        }

        if (quadTree.empty()) {
            g.setColor(Color.BLUE);
            g.drawOval((int) bounds.getX(), (int) bounds.getY(), 2, 2);
            g.drawOval((int) (bounds.getX() + bounds.getWidth()), (int) bounds.getY(), 2, 2);
            g.drawOval((int) bounds.getX(), (int) (bounds.getY() + bounds.getHeight()), 2, 2);
            g.drawOval((int) (bounds.getX() + bounds.getWidth()), (int) (bounds.getY() + bounds.getHeight()), 2, 2);
        }

//        g.setColor(Color.WHITE);
//        g.drawString(quadTree.id + "", bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
//        g.drawString(quadTree.getObjects().size() + "", bounds.x + bounds.width / 2, bounds.y + bounds.height / 2 + 20);
    }

    public void renderUI(Graphics g) {
        for (UIComponent uiComponent : YSortable.sort(uiObjects)) {
            uiComponent.render(g);
        }
    }

    public void addObject(GameObject object) {
        objectsToAdd.add(object);
    }

    public void addObject(UIComponent object) {
        uiObjectsToAdd.add(object);
    }

    public void removeObject(GameObject object) {
        objectsToRemove.add(object);
    }

    public void removeObject(UIComponent object) {
        uiObjectsToRemove.add(object);
    }

    public Set<GameObject> getObjects() {
        return objects;
    }

    public Set<UIComponent> getUI() {
        return uiObjects;
    }

    public void addToLayer(GameObject object, int layer) {
        collisionLayers.computeIfAbsent(layer, CollisionLayer::new).add(object);
    }

    public void removeFromLayer(GameObject gameObject, int layer) {
        Optional.ofNullable(collisionLayers.get(layer)).ifPresent(collisionLayer -> collisionLayer.remove(gameObject));
    }

    public CollisionLayer getLayer(int layer) {
        return collisionLayers.get(layer);
    }

    public void clear() {
        Set<GameObject> objects = this.objects;
        Set<UIComponent> uiObjects = this.uiObjects;
        for (GameObject object : objects) {
            object.destroy();
        }
        collisionLayers.clear();
        for (UIComponent object : uiObjects) {
            object.destroy();
        }
    }
}