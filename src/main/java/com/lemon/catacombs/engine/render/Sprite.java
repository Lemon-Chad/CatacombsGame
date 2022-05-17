package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite implements Spriteable {
    private BufferedImage image;
    private int originX, originY;

    public Sprite(BufferedImage image) {
        this.image = image;
        this.originX = image.getWidth() / 2;
        this.originY = image.getHeight() / 2;
    }

    public Sprite(BufferedImage image, int originX, int originY) {
        this.image = image;
        this.originX = originX;
        this.originY = originY;
    }

    public void setOrigin(int x, int y) {
        this.originX = x;
        this.originY = y;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public Sprite originFromUV() {
        Color color = new Color(image.getRGB(0, 0));
        int r = color.getRed();
        int g = color.getGreen();

        int x = (int) Math.floor(r / 255f * image.getWidth());
        int y = (int) Math.floor(g / 255f * image.getHeight());

        setOrigin(x, y);

        BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(this.image, 0, 0, null);
        // Replace top left corner with air
        image.setRGB(0, 0, 0);
        this.image = image;
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void render(Graphics g, int x, int y) {
        g.drawImage(getImage(), x, y, null);
    }

    public void render(Graphics g, int x, int y, int width, int height) {
        g.drawImage(getImage(), x, y, width, height, null);
    }

    public void render(Graphics g, int x, int y, int width, int height, double theta) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(theta, x + originX * ((float) width / getImage().getWidth()), y + originY * ((float) height / getImage().getHeight()));
        g2d.drawImage(getImage(), x, y, width, height, null);
        g2d.dispose();
    }

    public static Sprite LoadSprite(String path) {
        return new Sprite(Game.loadImage(path));
    }

    @Override
    public Sprite getSprite() {
        return this;
    }
}
