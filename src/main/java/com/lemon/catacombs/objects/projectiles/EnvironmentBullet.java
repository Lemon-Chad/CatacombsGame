package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Damageable;

import java.awt.*;

public class EnvironmentBullet extends Bullet {
    public EnvironmentBullet(int x, int y) {
        super(x, y, ID.Projectile);
        addCollisionMask(Layers.ENEMY);
        addCollisionMask(Layers.PLAYER);
    }

    @Override
    protected Color getColor() {
        return new Color(255, 200, 200);
    }

    @Override
    protected int getSize() {
        return 8;
    }

    @Override
    public void collision(GameObject other) {
        super.collision(other);
        if (other instanceof Damageable) {
            Damageable damageable = (Damageable) other;
            if (damageable.damage(getDamage(), this)) {
                setVelX(getVelX() / 2);
                setVelY(getVelY() / 2);
            }
            return;
        }
        destroy();
    }
}
