package com.lemon.catacombs.items.effects;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.objects.projectiles.Bullet;
import javafx.scene.shape.Ellipse;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import java.util.Set;

public class LightningEffect implements Weapon.BulletEffect {
    private final int radius;
    private final int damage;
    private final double chainChance;

    public LightningEffect() {
        radius = (int) Utils.range(32, 80);
        damage = (int) Utils.range(5, 15);
        chainChance = Utils.range(0.1, 0.5);
    }

    private static class Chain {
        private final Set<Chain> chain = new HashSet<>();
        private final Damageable target;

        public Chain(Damageable target) {
            this.target = target;
        }

        public void add(Chain chain) {
            this.chain.add(chain);
        }

        public Set<Chain> getChain() {
            return chain;
        }

        public Damageable getTarget() {
            return target;
        }

        public void render(Graphics g) {
            for (Chain c : chain) {
                g.setColor(new Color(0, 178, 255, 255));
                ((Graphics2D) g).setStroke(new BasicStroke(5));
                drawLine(g, c.getTarget());

                g.setColor(new Color(101, 233, 248, 255));
                ((Graphics2D) g).setStroke(new BasicStroke(3));
                drawLine(g, c.getTarget());

                c.render(g);
            }
        }

        private void drawLine(Graphics g, GameObject to) {
            int cx = (int) getTarget().getBounds().getCenterX();
            int cy = (int) getTarget().getBounds().getCenterY();

            int ocx = (int) to.getBounds().getCenterX();
            int ocy = (int) to.getBounds().getCenterY();

            g.drawLine(cx, cy, ocx, ocy);
        }

        public void damageAll(int damage) {
            getTarget().setInvulnerable(false);
            getTarget().damage(damage);
            for (Chain c : chain) {
                c.damageAll(damage);

            }
        }
    }

    private Chain chain(GameObject target, Set<GameObject> chained) {
        if (!(target instanceof Enemy)) {
            return null;
        }
        chained.add(target);
        Damageable d = (Damageable) target;
        // Chain
        Shape e = new Ellipse2D.Double(target.getX() - radius, target.getY() - radius, radius * 2, radius * 2);
        Set<Integer> mask = new HashSet<>();
        mask.add(Layers.ENEMY);
        Set<GameObject> nearby = Game.getInstance().getWorld().objectsIn(e, mask);
        nearby.removeAll(chained);
        Chain chain = new Chain(d);
        nearby.forEach(o -> {
            if (Math.random() < chainChance) {
                chain.add(chain(o, chained));
            }
        });
        return chain;
    }

    private static class ChainObject extends GameObject {
        private final Chain chain;
        private int life;

        public ChainObject(Chain chain) {
            super(0, 0, ID.Particle);
            this.chain = chain;
            life = 10;
        }


        @Override
        public void tick() {
            life--;
            if (life <= 0) {
                Game.getInstance().getWorld().removeParticle(this);
            }
        }

        @Override
        public int getYSort() {
            return Integer.MAX_VALUE - 16;
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(0, 0, 0, 0);
        }

        @Override
        public void collision(GameObject other) {

        }

        @Override
        public void render(Graphics g) {
            chain.render(g);
        }
    }

    @Override
    public void apply(Player player, Bullet b) {
        b.addImpactEffect((bullet, target) -> {
            bullet.destroy();
            if (!(target instanceof Enemy)) {
                return;
            }
            Set<GameObject> chained = new HashSet<>();
            Chain chain = chain(target, chained);
            Game.getInstance().getWorld().addParticle(new ChainObject(chain));
            target.addEffect(new GameObject.EffectListener() {
                @Override
                public void onEffectStart(GameObject gameObject) {}

                @Override
                public void onEffect(GameObject object) {
                    chain.damageAll(damage);
                }

                @Override
                public void onEffectEnd(GameObject gameObject) {}

                @Override
                public void drawEffect(GameObject object, Graphics g) {}
            }, 10);
        });
    }
}
