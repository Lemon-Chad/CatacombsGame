package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.ui.DMGNumber;

import java.awt.*;

public class Enemy extends PathingObject {
    private int health;

    public Enemy(int x, int y) {
        super(x, y, ID.Enemy, new int[]{ ID.Block });

        health = 10;

        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.ENEMY);
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

        path(15, 32);

        normalizeVelocity(5);
    }

    public void damage(int damage) {
        Game.getInstance().getWorld().addObject(new DMGNumber(damage, x, y, Color.RED, 32));
        health -= damage;
        if (health <= 0) {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
