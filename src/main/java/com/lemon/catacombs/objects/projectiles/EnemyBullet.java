package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class EnemyBullet extends Bullet {
    private final int size;
    public EnemyBullet(int x, int y, int size) {
        super(x, y, ID.EnemyProjectile);
        addCollisionMask(Layers.PLAYER);
        addCollisionLayer(Layers.ENEMY_PROJECTILES);
        this.size = size;
    }

    @Override
    Color getColor() {
        return new Color(255, 100, 100);
    }

    @Override
    int getSize() {
        return size;
    }

    @Override
    public void collision(GameObject other) {
        if (other.getId() == ID.Player) {
            Player player = (Player) other;
            player.damage(getDamage(), this);
        }
        destroy();
    }
}
