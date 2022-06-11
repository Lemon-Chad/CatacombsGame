package com.lemon.catacombs.engine;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.pathing.QuadTree;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.physics.CollisionLayer;
import com.lemon.catacombs.engine.physics.PhysicsObject;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.engine.render.YSortable;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;
import java.util.*;
public class Handler {
    private final ConcurrentSet<GameObject> objects;
    private final ConcurrentSet<UIComponent> uiObjects;
    // Particles are simply GameObjects without collision computation
    private final ConcurrentSet<GameObject> particles;
    private final Map<Integer, CollisionLayer> collisionLayers = new HashMap<>();

    private static final int quadTreeRadius = 100;

    public Handler() {
        objects = new ConcurrentSet<>(item -> {
            for (int layer : item.getCollisionLayer()) {
                addToLayer(item, layer);
            }
        }, item -> {
            for (CollisionLayer collisionLayer : collisionLayers.values()) {
                collisionLayer.remove(item);
            }
        });
        uiObjects = new ConcurrentSet<>();
        particles = new ConcurrentSet<>();
    }

    public void tick() {
        QuadTree.INNOVATION = 0;
        for (GameObject object : objects) {
            object.tick();
        }

        for (UIComponent uiComponent : uiObjects) {
            uiComponent.tick();
        }

        for (GameObject object : particles) {
            object.tick();
        }

        objects.commit();
        uiObjects.commit();
        particles.commit();

        QuadTree.clear();
    }

    public Set<QuadTree> quadTreeForLayers(int[] mask) {
        float ox, oy;
        if (Game.getInstance().getPlayer() != null) {
            ox = Game.getInstance().getPlayer().getX();
            oy = Game.getInstance().getPlayer().getY();
        } else {
            ox = Game.getInstance().getCamera().getX() + Game.getInstance().getWidth() / 2f;
            oy = Game.getInstance().getCamera().getY() + Game.getInstance().getHeight() / 2f;
        }
        return quadTreeForLayers(mask, (int) ox, (int) oy);
    }

    public Set<QuadTree> quadTreeForLayers(int[] mask, int x, int y) {
        return QuadTree.build(mask, x - quadTreeRadius * 32, y - quadTreeRadius * 32,
                quadTreeRadius * 64, quadTreeRadius * 64);
    }

    public void collisions() {
        for (GameObject object : objects) {
            int[] mask = new int[object.getCollisionMask().size()];
            if (mask.length == 0) continue;
            int i = 0;
            for (int layer : object.getCollisionMask()) {
                mask[i] = layer;
                i++;
            }
            Set<QuadTree> quadTrees = quadTreeForLayers(mask);
            for (QuadTree quadTree : quadTrees) {
                Set<QuadTree> trees = quadTree.getTrees(PhysicsObject.getSweepBounds(object.getBounds(),
                        object.getVelX(), object.getVelY()));
                for (QuadTree tree : trees) {
                    for (GameObject other : tree.getObjects()) {
                        if (object != other && colliding(object, other)) {
                            object.collision(other);
                        }
                    }
                }
            }
        }
    }

    public interface CollisionCheck {
        boolean collides(GameObject other);
    }

    public boolean blocked(GameObject origin, Polygon location, Set<Integer> collisionMask) {
        int[] mask = new int[collisionMask.size()];
        int i = 0;
        for (int layer : collisionMask) {
            mask[i] = layer;
            i++;
        }
        Set<QuadTree> quadTrees = quadTreeForLayers(mask);
        for (QuadTree quadTree : quadTrees) {
            Set<QuadTree> trees = quadTree.getTrees(location);
            for (QuadTree tree : trees) {
                for (GameObject object : tree.getObjects()) {
                    if (object != origin && Utils.contains(origin.getBounds(), location)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean colliding(GameObject object, GameObject other) {
        return object.collidesWith(other) || other.collidesWith(object);
    }

    public void render(Graphics g) {
        for (GameObject object : YSortable.sort(objects, particles)) {
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
        objects.add(object);
    }

    public void addObject(UIComponent object) {
        uiObjects.add(object);
    }

    public void removeObject(GameObject object) {
        objects.delete(object);
    }

    public void removeObject(UIComponent object) {
        uiObjects.delete(object);
    }

    public void addParticle(GameObject particle) {
        particles.add(particle);
    }

    public void removeParticle(GameObject particle) {
        particles.delete(particle);
    }

    public ConcurrentSet<GameObject> getObjects() {
        return objects;
    }

    public ConcurrentSet<UIComponent> getUI() {
        return uiObjects;
    }

    public void addToLayer(GameObject object, int layer) {
        if (!objects.contains(object)) {
            return;
        }
        CollisionLayer l = collisionLayers.computeIfAbsent(layer, CollisionLayer::new);
        l.add(object);
    }

    public void removeFromLayer(GameObject gameObject, int layer) {
        Optional.ofNullable(collisionLayers.get(layer)).ifPresent(collisionLayer -> collisionLayer.remove(gameObject));
    }

    public CollisionLayer getLayer(int layer) {
        if (collisionLayers.containsKey(layer)) {
            return collisionLayers.get(layer);
        }
        collisionLayers.put(layer, new CollisionLayer(layer));
        return collisionLayers.get(layer);
    }

    public void clear() {
        ConcurrentSet<GameObject> objects = this.objects;
        ConcurrentSet<UIComponent> uiObjects = this.uiObjects;
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
