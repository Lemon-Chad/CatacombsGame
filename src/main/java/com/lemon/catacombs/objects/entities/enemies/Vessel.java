package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class Vessel extends Enemy {
    public Vessel(int x, int y) {
        super(x, y, (int) (Math.random() * 30 + 20));
    }

    @Override
    protected int getSpeed() {
        return 5;
    }

    @Override
    public void tick() {
        super.tick();
        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            setTarget(new Point(player.getX(), player.getY()));
        } else {
            clearTarget();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(getColor(Color.YELLOW));
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    @Override
    protected void onPlayerHit(Player player) {
        player.damage(3, this);
    }
}
