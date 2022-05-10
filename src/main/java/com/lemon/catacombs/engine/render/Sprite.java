package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite implements Spriteable {
    private final BufferedImage image;

    public Sprite(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void render(Graphics g, int x, int y) {
        g.drawImage(image, x, y, null);
    }

    public void render(Graphics g, int x, int y, int width, int height) {
        g.drawImage(image, x, y, width, height, null);
    }

    public static Sprite LoadSprite(String path) {
        return new Sprite(Game.loadImage(path));
    }

    @Override
    public Sprite getSprite() {
        return this;
    }
}
