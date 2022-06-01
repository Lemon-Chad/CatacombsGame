package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.particles.FireParticle;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.sun.xml.internal.ws.wsdl.writer.document.Part;

import java.awt.*;

public class FireEffect implements Weapon.BulletEffect {
    private final int damage;
    private final int duration;

    public FireEffect() {
        damage = Utils.intRange(5, 10);
        duration = Utils.intRange(30, 90);
    }

    @Override
    public void apply(Player player, Bullet b) {
        b.addImpactEffect((bullet, target) -> target.addEffect(new GameObject.EffectListener() {
            @Override
            public void onEffectStart(GameObject gameObject) {

            }

            @Override
            public void onEffect(GameObject object) {
                if (object instanceof Damageable) {
                    Damageable d = (Damageable) object;
                    d.damage(damage);
                    if (d instanceof Enemy) {
                        Enemy e = (Enemy) d;
                        e.setState(Enemy.State.CHASE, 10);
                    }

                    int cx = (int) (Math.random() * object.getBounds().getWidth()) + object.getX();
                    int cy = (int) object.getBounds().getCenterY();

                    Particle particle = new FireParticle(cx, cy);

                    float velX = (float) ((Math.random() * 2 - 1) * 1);
                    float velY = (float) (Math.random() * -6);

                    particle.setVelX(velX);
                    particle.setVelY(velY);

                    Game.getInstance().getWorld().addParticle(particle);
                }
            }

            @Override
            public void onEffectEnd(GameObject gameObject) {

            }

            @Override
            public void drawEffect(GameObject object, Graphics g) {

            }
        }, duration));
    }
}
