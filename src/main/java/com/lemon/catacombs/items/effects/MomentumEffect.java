package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;

import java.awt.*;

public class MomentumEffect implements Weapon.BulletEffect {
    private final int travelFactor;

    public MomentumEffect() {
        this.travelFactor = (int) Utils.range(96, 512);
    }

    @Override
    public void apply(Player player, Bullet bullet) {
        Point start = new Point(bullet.getX(), bullet.getY());
        bullet.addImpactEffect(((bullet1, target) -> {
            Point end = new Point(bullet1.getX(), bullet1.getY());
            double distance = start.distance(end);
            double damageScale = distance / this.travelFactor;
            int damage = (int) (bullet1.getDamage() * damageScale);
            if (target instanceof Damageable) {
                ((Damageable) target).setInvulnerable(false);
                ((Damageable) target).damage(damage);
            }
        }));
    }
}
