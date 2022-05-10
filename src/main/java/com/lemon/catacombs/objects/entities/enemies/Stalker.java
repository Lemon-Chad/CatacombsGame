package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.EnemyBullet;

import java.awt.*;

public class Stalker extends Enemy {
    public static final int STALKING_DISTANCE = 300;
    public static final int FIRE_RATE = 30;
    public int cooldown = 0;

    public Stalker(int x, int y) {
        super(x, y, (int) (Math.random() * 50 + 50));
    }

    @Override
    public void render(Graphics g) {
        g.setColor(getColor(new Color(255, 122, 0)));
        g.fillRect(x, y, 64, 64);
    }

    @Override
    protected int getSpeed() {
        return 3;
    }

    @Override
    public void tick() {
        super.tick(16, 64);
        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            if (getState() == State.CHASE && cooldown <= 0) {
                cooldown = FIRE_RATE;
                shoot(player, 1, 10);
            }
            Point position = new Point(player.getX(), player.getY());
            if (position.distance(x, y) < STALKING_DISTANCE || position.distance(x, y) > STALKING_DISTANCE * 2.5f) {
                double angleToPlayer = Math.atan2(y - position.y, x - position.x) + Math.random();
                Point targetPosition = new Point((int) (position.x + STALKING_DISTANCE * 1.5f * Math.cos(angleToPlayer)),
                        (int) (position.y + STALKING_DISTANCE * 1.5f * Math.sin(angleToPlayer)));
                setTarget(targetPosition);
            } else {
                clearTarget();
            }
        } else {
            clearTarget();
        }
        cooldown--;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 64, 64);
    }

    @Override
    protected void onPlayerHit(Player player) {

    }
}
