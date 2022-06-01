package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.projectiles.Bullet;

import java.awt.*;

public class IceEffect implements Weapon.BulletEffect {
    private static final int FROZEN_STATE = Enemy.State.newState();
    private final int duration;

    public IceEffect() {
        this.duration = Utils.intRange(30, 90);
    }

    @Override
    public void apply(Player player, Bullet bullet) {
        bullet.addImpactEffect(((bullet1, target) -> {
            if (!(target instanceof Enemy)) return;
            target.addEffect(new GameObject.EffectListener() {
                @Override
                public void onEffectStart(GameObject gameObject) {
                    ((Enemy) gameObject).setState(FROZEN_STATE, duration);
                }

                @Override
                public void onEffect(GameObject object) {
                    object.setVelX(0);
                    object.setVelY(0);
                }

                @Override
                public void onEffectEnd(GameObject gameObject) {

                }

                @Override
                public void drawEffect(GameObject object, Graphics g) {
                    int x = object.getBounds().x - 16;
                    int y = object.getBounds().y - 16;
                    g.setColor(new Color(151, 239, 253, 134));
                    g.fillRect(x, y, object.getBounds().width + 32, object.getBounds().height + 32);
                }
            }, duration);
        }));
    }
}
