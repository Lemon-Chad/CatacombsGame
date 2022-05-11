package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.effects.splash.Explosion;

import java.awt.*;

public class ImpactGrenade extends Bullet {
    private final int damage;
    private final int radius;
    private final int maxCookTime;
    private int cooldown;
    private int cookTime;
    public ImpactGrenade(int x, int y, int cookTime, int damage, int radius) {
        super(x, y, ID.Projectile);
        this.maxCookTime = cookTime;
        this.cookTime = cookTime;
        this.damage = damage;
        this.radius = radius;
        this.cooldown = 10;
        addCollisionLayer(Layers.PROJECTILES);
        addCollisionMask(Layers.PLAYER);
        addCollisionMask(Layers.ENEMY);
        addCollisionMask(Layers.PROJECTILES);
    }

    @Override
    public void tick() {
        super.tick();
        cooldown--;
        x += getVelX();
        y += getVelY();

        setVelX(getVelX() * 0.99f);
        setVelY(getVelY() * 0.99f);

        cookTime--;
        if (cookTime <= 0) {
            explode();
        }
    }

    @Override
    Color getColor() {
        return new Color(255, cookTime * 255 / maxCookTime, cookTime * 255 / maxCookTime);
    }

    @Override
    int getSize() {
        return 16;
    }

    @Override
    public void collision(GameObject other) {
        super.collision(other);
        if (cooldown >= 0) {
            return;
        }
        explode();
    }

    private void explode() {
        Game.getInstance().getWorld().addObject(new Explosion(x, y, damage, radius));
        destroy();
    }
}
