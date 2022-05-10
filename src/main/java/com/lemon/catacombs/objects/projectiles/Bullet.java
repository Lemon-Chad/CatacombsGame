package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class Bullet extends GameObject {
    private final Set<BulletEffects> effects = new HashSet<>();
    private final Set<ImpactEffect> impactEffects = new HashSet<>();
    private int damage;

    public interface BulletEffects {
        void apply(Bullet bullet);
    }

    public interface ImpactEffect {
        void apply(Bullet bullet, GameObject target);
    }

    public Bullet(int x, int y, int id) {
        super(x, y, id);
        addCollisionMask(Layers.BLOCKS);
    }

    @Override
    public void tick() {
        x += getVelX();
        y += getVelY();

        setVelX(getVelX() * 0.99f);
        setVelY(getVelY() * 0.99f);

        if (getVelX() < 0.1 && getVelX() > -0.1) {
            destroy();
        }

        if (getVelY() < 0.1 && getVelY() > -0.1) {
            destroy();
        }

        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            if (Point.distance(x, y, player.getX(), player.getY()) > 2_000) {
                // Despawn
                destroy();
            }
        }

        for (BulletEffects effect : effects) {
            effect.apply(this);
        }
    }

    public void addEffect(BulletEffects effect) {
        effects.add(effect);
    }

    public void addImpactEffect(ImpactEffect effect) {
        impactEffects.add(effect);
    }

    abstract Color getColor();

    abstract int getSize();

    @Override
    public void render(Graphics g) {
        Color color = getColor();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int) (255 * Math.min(Math.sqrt(getVelX() * getVelX() + getVelY() * getVelY()) / 3f, 1))));
        g.fillOval(x, y, getSize(), getSize());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, getSize(), getSize());
    }

    @Override
    public void collision(GameObject other) {
        for (ImpactEffect effect : impactEffects) {
            effect.apply(this, other);
        }
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
