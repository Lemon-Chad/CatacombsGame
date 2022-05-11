package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class TileSet {
//    private final TilePattern[] patterns = {new TilePattern(0b00001011), new TilePattern(0b00011111), new TilePattern(0b00010110), new TilePattern(0b00000010), new TilePattern(0b00001010), new TilePattern(0b00011110), new TilePattern(0b00011011), new TilePattern(0b00010010), new TilePattern(0b00011010), new TilePattern(0b11011011), new TilePattern(0b01101011), new TilePattern(0b11111111), new TilePattern(0b11010110), new TilePattern(0b01000010), new TilePattern(0b01101010), new TilePattern(0b11111110), new TilePattern(0b11111011), new TilePattern(0b11010010), new TilePattern(0b11111010), new TilePattern(0b01111110), new TilePattern(0b01101000), new TilePattern(0b11111000), new TilePattern(0b11010000), new TilePattern(0b01000000), new TilePattern(0b01001011), new TilePattern(0b11011111), new TilePattern(0b01111111), new TilePattern(0b01010110), new TilePattern(0b01011111), new TilePattern(0b01011011), new TilePattern(0b01011110), new TilePattern(0b00001000), new TilePattern(0b00011000), new TilePattern(0b00010000), new TilePattern(0b00000000), new TilePattern(0b01001000), new TilePattern(0b11011000), new TilePattern(0b01111000), new TilePattern(0b01010000), new TilePattern(0b01011000), new TilePattern(0b01111010), new TilePattern(0b11011010), new TilePattern(0b01001010), new TilePattern(0b11011110), new TilePattern(0b01111011), new TilePattern(0b01010010), new TilePattern(0b01011010),};
    private static final TilePattern[] patterns = getPatterns();

    private static TilePattern[] getPatterns() {
        BufferedImage img = Game.loadImage("/engine/tilepatterns.png");
        TilePattern[] patterns = new TilePattern[47];
        int i = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 11; x++) {
                BufferedImage pattern = img.getSubimage(x * 3, y * 3, 3, 3);
                // If 0, 0 is (0, 0, 255), skip
                if (pattern.getRGB(0, 0) == toRGB(255, 0, 0, 255)) {
                    continue;
                }
                int key = ifRed(pattern.getRGB(2, 2)) |
                        ifRed(pattern.getRGB(1, 2)) << 1 |
                        ifRed(pattern.getRGB(0, 2)) << 2 |
                        ifRed(pattern.getRGB(2, 1)) << 3 |
                        ifRed(pattern.getRGB(0, 1)) << 4 |
                        ifRed(pattern.getRGB(2, 0)) << 5 |
                        ifRed(pattern.getRGB(1, 0)) << 6 |
                        ifRed(pattern.getRGB(0, 0)) << 7;
                patterns[i++] = new TilePattern(key);
            }
        }
        return patterns;
    }

    private static int ifRed(int color) {
        return color == toRGB(255, 255, 0, 0) ? 1 : 0;
    }

    private static int toRGB(int a, int r, int g, int b) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private final Sprite[] sprites;
    private final Set<Tile> tiles;
    private final Set<Point> tileLocations;
    private final int z;
    private final int tileWidth;
    private final int tileHeight;
    private final Set<Integer> collisionLayer;
    private final int id;

    public TileSet(Sprite[] sprites, int tileWidth, int tileHeight, int z, Set<Integer> collisionLayer, int id) {
        this.id = id;
        this.sprites = sprites;
        this.tiles = new HashSet<>();
        this.tileLocations = new HashSet<>();
        this.z = z;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.collisionLayer = collisionLayer;
    }

    public class Tile extends GameObject {
        private Sprite tile;

        public Tile(int x, int y) {
            super(x, y, TileSet.this.id);
            addCollisionLayers(collisionLayer);
            tiles.add(this);
            tileLocations.add(new Point(x, y));
        }

        @Override
        public int getYSort() {
            return z + super.getYSort();
        }

        private void updateSprite() {
            int up = tileLocations.contains(new Point(x, y - tileHeight)) ? 1 : 0;
            int down = tileLocations.contains(new Point(x, y + tileHeight)) ? 1 : 0;
            int left = tileLocations.contains(new Point(x - tileWidth, y)) ? 1 : 0;
            int right = tileLocations.contains(new Point(x + tileWidth, y)) ? 1 : 0;

            int upLeft = tileLocations.contains(new Point(x - tileWidth, y - tileHeight)) ? 1 : 0;
            int upRight = tileLocations.contains(new Point(x + tileWidth, y - tileHeight)) ? 1 : 0;
            int downLeft = tileLocations.contains(new Point(x - tileWidth, y + tileHeight)) ? 1 : 0;
            int downRight = tileLocations.contains(new Point(x + tileWidth, y + tileHeight)) ? 1 : 0;

            int key = downRight | (down << 1) | (downLeft << 2) | (right << 3) | (left << 4) | (upRight << 5) | (up << 6) | (upLeft << 7);

            int c = 34;
            for (int i = 0; i < patterns.length; i++) {
                if (patterns[i].match(key) && patterns[i].complexity() > patterns[c].complexity()) {
                    c = i;
                }
            }
            tile = sprites[c];
        }

        @Override
        public void render(Graphics g) {
            tile.render(g, x, y, tileWidth, tileHeight);
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(x, y, tileWidth, tileHeight);
        }

        @Override
        public void collision(GameObject other) {
            // Do nothing
        }
    }

    public void updateAll() {
        for (Tile tile : tiles) {
            tile.updateSprite();
        }
    }

    public void placeTile(int x, int y) {
        Game.getInstance().getWorld().addObject(new Tile(x, y));
        updateAll();
    }

    public static TileSet LoadTilemap(int id, String path, int width, int height, int z, int[] collisionLayer) {
        Set<Integer> collisionLayerSet = new HashSet<>();
        for (int i : collisionLayer) {
            collisionLayerSet.add(i);
        }
        return LoadTilemap(id, path, width, height, z, collisionLayerSet);
    }

    public static TileSet LoadTilemap(int id, String path, int width, int height, int z, Set<Integer> collisionLayer) {
        BufferedImage img = Game.loadImage(path);
        Sprite[] sprites = new Sprite[47];
        int tileWidth =  img.getWidth() / 11;
        int tileHeight = img.getHeight() / 5;

        int i = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 11; x++) {
                int j = x + y * 11;
                if (j == 10 || j == 21 || (43 < j && j < 48) || j > 52) {
                    i++;
                    continue;
                }
                sprites[j - i] = new Sprite(img.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight));
            }
        }

        return new TileSet(sprites, width, height, z, collisionLayer, id);
    }

}
