package com.lemon.catacombs.generation.decoration;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.BufferedImageLoader;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.generation.DoorMarker;
import com.lemon.catacombs.objects.entities.enemies.Vessel;
import com.lemon.catacombs.objects.rooms.Crate;
import com.lemon.catacombs.objects.rooms.Pit;
import com.lemon.catacombs.objects.rooms.Wall;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Room {
    private final int x, y;
    private final int w, h;
    private final Set<DoorMarker> doors;

    public Room(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.doors = new HashSet<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public Set<DoorMarker> getDoors() {
        return doors;
    }

    public void addDoor(DoorMarker door) {
        doors.add(door);
    }

    public Point getCenter() {
        return new Point(x + w / 2, y + h / 2);
    }

    public void removeDoor(DoorMarker door) {
        doors.remove(door);
    }

    private static final BuildPattern[] WallPatterns = loadPatterns("wall", DecorationType.WALL);

    private static final BuildPattern[] PitPatterns = loadPatterns("pit", DecorationType.PIT);

    private static BuildPattern[] loadPatterns(String type, DecorationType decorType) {
        String path = "/engine/generation/room_patterns/" + type;
        String[] files = Utils.listFiles(path);
        BuildPattern[] patterns = new BuildPattern[files.length * 4];
        BufferedImageLoader loader = new BufferedImageLoader();
        for (int i = 0; i < files.length; i++) {
            BufferedImage image = loader.loadImage(path + "/" + files[i]);

            BufferedImage onSide = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = onSide.createGraphics();
            g.rotate(Math.PI / 2, onSide.getWidth() / 2f, onSide.getHeight() / 2f);
            g.drawImage(image, 0, 0, null);
            g.dispose();

            patterns[i * 4] = BuildPattern.from(decorType, image);
            patterns[i * 4 + 1] = BuildPattern.from(decorType, onSide);
            patterns[i * 4 + 2] = BuildPattern.from(decorType, Utils.flip(image));
            patterns[i * 4 + 3] = BuildPattern.from(decorType, Utils.flip(onSide));
        }
        return patterns;
    }

    public boolean canPlace(Decoration[] pattern, Set<Decoration> placed) {
        for (Decoration decor : pattern) {
            for (Decoration decoration : placed) {
                if (decor.intersects(decoration)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void decorate(int tileSize, int wallThickness) {
        int x = this.x * tileSize;
        int y = this.y * tileSize;
        int w = this.w * tileSize;
        int h = this.h * tileSize;
        Set<Decoration> decorations = new HashSet<>();
        // Step 1: Apply wall patterns
        int j = (int) (Math.random() * WallPatterns.length);
        for (int i = 0; i < WallPatterns.length; i++) {
            BuildPattern pattern = WallPatterns[(i + j) % WallPatterns.length];
            if (Math.random() < 5f / WallPatterns.length) {
                Decoration[] wallPattern = pattern.getPattern(x, y, w, h);
                if (canPlace(wallPattern, decorations)) {
                    decorations.addAll(Arrays.asList(wallPattern));
                }
            }
        }
        // Step 2: Apply pit patterns
        j = (int) (Math.random() * PitPatterns.length);
        for (int i = 0; i < PitPatterns.length; i++) {
            BuildPattern pattern = PitPatterns[(i + j) % PitPatterns.length];
            if (Math.random() < 1f / (2 * PitPatterns.length)) {
                Decoration[] pitPattern = pattern.getPattern(x, y, w, h);
                decorations.addAll(Arrays.asList(pitPattern));
            }
        }
        // Step 3: Scatter crates
        int cratePacking = (w * h) / 48 / 5000;
        int crateCount = (int) (Math.random() * cratePacking + 2);
        for (int i = 0; i < crateCount; i++) {
            double angle = Math.random() * Math.PI * 2;
            Point onWall = Utils.pointOnRect(x + w / 2 - 24, y + h / 2 - 24, w - 72, h - 72, angle);
            Decoration crate = new Decoration(DecorationType.CRATE, onWall.x, onWall.y, 48, 48);
            if (canPlace(new Decoration[] { crate }, decorations)) {
                decorations.add(crate);
            }
        }

        // Step 4: Build all
        for (Decoration decoration : decorations) {
            GameObject object;
            switch (decoration.getType()) {
                case CRATE:
                     object = new Crate(decoration.getX(), decoration.getY(), 48, 48);
//                    object = new Vessel(decoration.getX(), decoration.getY());
                    break;
                case PIT:
                    object = new Pit(decoration.getX(), decoration.getY(), decoration.getW(), decoration.getH());
                    break;
                case WALL:
                    object = new Wall(decoration.getX(), decoration.getY(), decoration.getW(), decoration.getH(), wallThickness);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown decoration type: " + decoration.getType());
            }
            Game.getInstance().getWorld().addObject(object);
        }
    }
}
