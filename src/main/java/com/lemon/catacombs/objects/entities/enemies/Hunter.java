package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.engine.AudioHandler;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.EnemyBullet;

import java.awt.*;

public class Hunter extends Enemy {
    public static final int PELLET_COUNT = 10;
    public static final double PELLET_SPREAD = 0.5;
    public static final int FIRE_RATE = 60;

    private int cooldown = 0;

    public Hunter(int x, int y) {
        super(x, y, (int) (Math.random() * 50 + 50));
    }

    @Override
    protected int getSpeed() {
        return 7;
    }

    @Override
    public void tick() {
        super.tick();

        if (getState() == State.EVADE) {
            setVelX(30 * (float) Math.cos(getEvadeAngle()));
            setVelY(30 * (float) Math.sin(getEvadeAngle()));
        }

        cooldown--;
        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            if (getState() == State.CHASE) {
                if (new Point(x, y).distance(player.getX(), player.getY()) < 128 && cooldown <= 0) {
                    cooldown = FIRE_RATE;
                    shoot(player);
                    evade(player, 30);
                }
                setTarget(new Point(player.getX(), player.getY()));
            }
        } else {
            clearTarget();
        }
    }

    protected void shoot(Player player) {
        double theta = Math.atan2(player.getY() - y, player.getX() - x);
        for (int i = 0; i < PELLET_COUNT; i++) {
            double angle = theta + Math.random() * PELLET_SPREAD - PELLET_SPREAD / 2;
            float speed = (float) Math.random() * 15 + 10;

            Bullet bullet = new EnemyBullet(x, y, 8);
            bullet.setVelX(speed * (float) Math.cos(angle));
            bullet.setVelY(speed * (float) Math.sin(angle));
            bullet.setDamage(5);

            Game.getInstance().getWorld().addObject(bullet);
        }
        Game.getInstance().getAudioHandler().playSound("/sounds/shotgun/fire" + (int) (1 + Math.random() * 2)
                + ".wav", 0.25f, false);
    }

    @Override
    public void render(Graphics g) {
        g.setColor(getColor(Color.RED));
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    @Override
    protected void onPlayerHit(Player player) {

    }
}
