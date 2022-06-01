package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.PlayerBullet;

public class SplitEffect implements Weapon.BulletEffect {
    private final int count;
    private final float drag;

    public SplitEffect() {
        this.count = Utils.intRange(2, 5);
        this.drag = (float) Utils.range(0.1, 0.5);
    }

    @Override
    public void apply(Player player, Bullet bullet) {
        bullet.addImpactEffect(((bullet1, target) -> {
            bullet.destroy();
            for (int i = 0; i < count; i++) {
                double angle = Math.random() * Math.PI * 2;
                float velX = (float) Math.cos(angle) * bullet.getVelX() * drag;
                float velY = (float) Math.sin(angle) * bullet.getVelY() * drag;

                Bullet bullet2 = new PlayerBullet(bullet.getX(), bullet.getY());
                bullet2.setVelX(velX);
                bullet2.setVelY(velY);
                bullet2.setDamage(bullet.getDamage());

                Game.getInstance().getWorld().addObject(bullet2);
            }
        }));
    }
}
