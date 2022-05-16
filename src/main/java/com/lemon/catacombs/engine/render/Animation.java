package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;

import java.awt.image.BufferedImage;

public class Animation implements Spriteable {
    private final Sprite[] frames;
    private final int numFrames;

    private int speed;
    private int currentFrame;
    private int delta;
    private long lastTime;

    private boolean playing;

    public Animation(Sprite[] frames) {
        this.frames = frames;
        numFrames = frames.length;

        currentFrame = 0;
        delta = 0;
        speed = 0;
        lastTime = System.currentTimeMillis();

        playing = false;
    }

    public Animation setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }

    public void originFromUV() {
        for (Sprite frame : frames) {
            frame.originFromUV();
        }
    }

    public void start() {
        start(speed);
    }

    public void start(int speed) {
        currentFrame = 0;
        delta = 0;
        this.speed = speed;
        lastTime = System.currentTimeMillis();
        play();
    }

    public void continueAnimation() {
        lastTime = System.currentTimeMillis();
        play();
    }

    public void update() {
        if (!playing)
            return;

        long time = System.currentTimeMillis();
        delta += time - lastTime;
        lastTime = time;

        while (delta > Math.abs(speed) && speed != 0) {
            currentFrame += speed > 0 ? 1 : -1;
            delta -= Math.abs(speed);

            if (currentFrame == numFrames)
                currentFrame = 0;
            else if (currentFrame < 0)
                currentFrame = numFrames - 1;
        }
    }

    public void reverse() {
        if (speed > 0) speed = -speed;
    }

    public void forwards() {
        if (speed < 0) speed = -speed;
    }

    public Sprite getFrame() {
        return frames[currentFrame];
    }

    public static Animation LoadSpriteSheet(String ref, int hFrames, int vFrames) {
        BufferedImage spriteSheet = Game.loadImage(ref);
        int width = spriteSheet.getWidth() / hFrames;
        int height = spriteSheet.getHeight() / vFrames;
        Sprite[] frames = new Sprite[hFrames * vFrames];
        for (int y = 0; y < vFrames; y++) {
            for (int x = 0; x < hFrames; x++) {
                frames[x + y * hFrames] = new Sprite(spriteSheet.getSubimage(x * width, y * height, width, height));
            }
        }
        return new Animation(frames);
    }

    public static Animation LoadSpriteSheet(String ref, int frameCount, int frameWidth, int frameHeight) {
        BufferedImage spriteSheet = Game.loadImage(ref);
        int width = spriteSheet.getWidth();
        int height = spriteSheet.getHeight();
        Sprite[] frames = new Sprite[frameCount];
        int i = 0;
        for (int y = 0; y < height; y += frameHeight) {
            for (int x = 0; x < width; x += frameWidth) {
                frames[i] = new Sprite(spriteSheet.getSubimage(x, y, frameWidth, frameHeight));
                i++;
                if (i == frameCount)
                    return new Animation(frames);
            }
        }
        throw new RuntimeException("Frame count does not match frame count in sprite sheet");
    }

    public void reset() {
        currentFrame = 0;
        delta = 0;
        lastTime = System.currentTimeMillis();
    }

    @Override
    public Sprite getSprite() {
        update();
        return getFrame();
    }
}
