package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.effects.splash.Explosion;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;

public class ExplosionEffect implements Weapon.BulletEffect {
    private final int damage;
    private final int radius;

    public ExplosionEffect() {
        damage = (int) (Utils.range(5, 15));
        radius = (int) (Utils.range(2, 4));
    }

    @Override
    public void apply(Player player, Bullet bullet) {
        bullet.addImpactEffect((b, h) -> {
            Game.getInstance().getWorld().addObject(new Explosion(
                    b.getX(), b.getY(), damage, radius
            ));
            b.destroy();
        });
    }
}
