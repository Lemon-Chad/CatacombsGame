package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Sprite;

import java.awt.*;

public class Block extends GameObject {
    private final Sprite sprite;

    public Block(int x, int y) {
        super(x, y, ID.Block);
        addCollisionLayer(Layers.BLOCKS);
        sprite = Sprite.LoadSprite("/sprites/tiles/stonebrick16set/" + ((int) (Math.random() * 3 + 1) - 1) + ".png");
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        sprite.render(g, x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    @Override
    public void collision(GameObject other) {

    }
}
