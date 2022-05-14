package com.lemon.catacombs.engine;

import com.lemon.catacombs.engine.pathing.QuadTree;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.physics.CollisionLayer;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.engine.render.YSortable;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.projectiles.Bullet;

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

        QuadTree.clear();
    }

    public QuadTree quadTreeForLayers(int[] mask) {
        Camera camera = Game.getInstance().getCamera();
        int x = (int) camera.getX() + Game.getInstance().getWidth() / 2;
        int y = (int) camera.getY() + Game.getInstance().getHeight() / 2;
        QuadTree.INNOVATION = 0;
        return QuadTree.build(mask, x - quadTreeRadius * 32, y - quadTreeRadius * 32,
                quadTreeRadius * 64, quadTreeRadius * 64);
    }

    public void collisions() {
        for (GameObject object : objects) {
            int[] mask = new int[object.getCollisionMask().size()];
            int i = 0;
            for (int layer : object.getCollisionMask()) {
                mask[i] = layer;
                i++;
            }
            QuadTree quadTree = quadTreeForLayers(mask);
            Set<QuadTree> trees = quadTree.getTrees(object.getBounds());
            for (QuadTree tree : trees) {
                for (GameObject other : tree.getObjects()) {
                    if (object != other && object.collidesWith(other)) {
                        object.collision(other);
                    }
                }
            }
        }
    }

    public boolean blocked(GameObject origin, Rectangle location, Set<Integer> collisionMask) {
        int[] mask = new int[collisionMask.size()];
        int i = 0;
        for (int layer : collisionMask) {
            mask[i] = layer;
            i++;
        }
        QuadTree quadTree = quadTreeForLayers(mask);
        Set<QuadTree> trees = quadTree.getTrees(location);
        for (QuadTree tree : trees) {
            for (GameObject object : tree.getObjects()) {
                if (object != origin && object.collidesWith(origin)) {
                    return true;
                }
            }
        }
        return false;
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
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        for (QuadTree child : quadTree.getChildren()) {
            if (child != null) {
                renderTree(child, g);
            }
        }
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

    public Set<GameObject> objectsIn(Shape polygon, Set<Integer> mask) {
        Set<GameObject> objects = new HashSet<>();
        for (int layerID : mask) {
            CollisionLayer layer = getLayer(layerID);
            for (GameObject object : layer.getObjects())
                if (polygon.intersects(object.getBounds()))
                    objects.add(object);
        }
        return objects;
    }
}
