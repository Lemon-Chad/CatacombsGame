package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Player;
import org.w3c.dom.css.Rect;

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
        addCollisionLayer(Layers.PROJECTILES);
    }

    @Override
    public void tick() {
        super.tick();

        x += Math.round(getVelX() * Game.getInstance().getPhysicsSpeed());
        y += Math.round(getVelY() * Game.getInstance().getPhysicsSpeed());

        friction(0.99f);

        if (getVelX() < 0.1 && getVelX() > -0.1) {
            destroy();
        }

        if (getVelY() < 0.1 && getVelY() > -0.1) {
            destroy();
        }

        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            if (Point.distance(x, y, player.getX(), player.getY()) > 25_000) {
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

    protected abstract Color getColor();

    protected abstract int getSize();

    @Override
    public boolean collidesWith(GameObject o) {
        return o.getBounds().intersectsLine(x, y, (int) (x + getVelX()), (int) (y + getVelY()));
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        Color color = getColor();
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                (int) (255 * Math.min(Math.sqrt(getVelX() * getVelX() + getVelY() * getVelY()) / 3f, 1))));
        g.fillOval(x, y, getSize(), getSize());
    }

    @Override
    public Rectangle getBounds() {
        int fx = (int) (x + getVelX());
        int fy = (int) (y + getVelY());
        int w = Math.abs(fx - x);
        int h = Math.abs(fy - y);
        int x = Math.min(fx, this.x);
        int y = Math.min(fy, this.y);
        return new Rectangle(x, y, w, h);
    }

    @Override
    public void collision(GameObject other) {
        for (ImpactEffect effect : impactEffects) {
            effect.apply(this, other);
        }
    }

    protected Set<ImpactEffect> getImpactEffects() {
        return impactEffects;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
