package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;

public class Block extends GameObject {
    public Block(int x, int y) {
        super(x, y, ID.Block);
        addCollisionLayer(Layers.BLOCKS);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(200, 200, 200, 100));
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    @Override
    public void collision(GameObject other) {

    }
}
