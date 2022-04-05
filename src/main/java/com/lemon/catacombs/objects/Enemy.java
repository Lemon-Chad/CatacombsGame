package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.Game;

import java.awt.*;

public class Enemy extends PathingObject {
    public Enemy(int x, int y) {
        super(x, y, ID.Enemy, new ID[]{ ID.Block });
        setVisualize(true);
        addCollisionMask(Layers.BLOCKS);
        addCollisionLayer(Layers.ENEMY);
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Game.getInstance().player;
        if (player != null) {
            setTarget(new Point(player.getX(), player.getY()));
        } else {
            clearTarget();
        }

        setVelX(0);
        setVelY(0);

        path(10, 32);

        normalizeVelocity(10);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
