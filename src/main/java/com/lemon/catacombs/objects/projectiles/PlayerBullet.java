package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.enemies.Enemy;

import java.awt.*;

public class PlayerBullet extends Bullet {
    public PlayerBullet(int x, int y) {
        super(x, y, ID.PlayerProjectile);
        addCollisionMask(Layers.ENEMY);
        addCollisionLayer(Layers.PLAYER_PROJECTILES);
    }

    @Override
    protected Color getColor() {
        return new Color(255, 255, 255);
    }

    @Override
    protected int getSize() {
        return 8;
    }

    @Override
    public void collision(GameObject other) {
        super.collision(other);
        if (other.getId() == ID.Enemy) {
            Enemy enemy = (Enemy) other;
            if (enemy.getState() == Enemy.State.STUN) return;
            enemy.damage(getDamage(), this);
            setVelX(getVelX() / 2);
            setVelY(getVelY() / 2);
            return;
        }
        destroy();
    }
}
