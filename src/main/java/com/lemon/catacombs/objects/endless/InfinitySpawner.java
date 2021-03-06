package com.lemon.catacombs.objects.endless;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.entities.enemies.*;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class InfinitySpawner extends GameObject {
    private static final int spawnDelay = 100;
    private static final int spawnRadius = 1000;
    private int spawnTimer = -spawnDelay;
    private int spawns = 0;

    public InfinitySpawner() {
        super(0, 0, ID.UI);
    }

    @Override
    public void tick() {
        super.tick();
        spawnTimer++;
        if (spawnTimer >= spawnDelay) {
            spawnTimer = -(int)(Math.random() * spawnDelay / 2);
            spawn();
            spawns++;
        }
    }

    private void spawn() {
        double angle = Math.random() * Math.PI * 2;
        double ox = spawnRadius * Math.cos(angle);
        double oy = spawnRadius * Math.sin(angle);
        Player player = Game.getInstance().getPlayer();
        if (player == null) return;
        Enemy enemy = getEnemy((int) ox + player.getX(), (int) oy + player.getY());
        Game.getInstance().getWorld().addObject(enemy);
        double vesselChance = spawns / 200.0;
        while (Math.random() < vesselChance) {
            Enemy vessel = new Vessel(enemy.getX(), enemy.getY());
            Game.getInstance().getWorld().addObject(vessel);
            vesselChance -= 1;
        }
    }

    public Enemy getEnemy(int x, int y) {
        double chance = Math.random();
        if (chance > 0.99 && spawns > 150) {
            // Spawn goliath (blue)
            return new Goliath(x, y);
        } else if (chance <= 0.99 && chance > 0.8 && spawns > 100) {
            // Spawn shadow (purple)
            return new Shadow(x, y);
        } else if (chance <= 0.8 && chance > 0.6 && spawns > 50) {
            // Spawn hunter (red)
            return new Hunter(x, y);
        } else if (chance <= 0.6 && chance > 0.3 && spawns > 20) {
            // Spawn stalker (orange)
            return new Stalker(x, y);
        } else {
            // Spawn vessel (yellow)
            return new Vessel(x, y);
        }
    }

    @Override
    public int getYSort() {
        return 0;
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void collision(GameObject other) {

    }
}
