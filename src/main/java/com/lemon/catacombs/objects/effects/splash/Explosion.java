package com.lemon.catacombs.objects.effects.splash;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.particles.FireParticle;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Explosion extends GameObject {
    private final int damage;
    private final int radius;
    private int currentRadius;
    public Explosion(int x, int y, int damage, int radius) {
        super(x, y, ID.SplashEffect);
        addCollisionLayer(Layers.SPLASH);
        addCollisionMask(Layers.PLAYER);
        addCollisionMask(Layers.ENEMY);
        this.damage = damage;
        this.radius = radius;
        currentRadius = 1;
        Game.getInstance().getAudioHandler().playSound("/sounds/explode" + (int) Utils.range(1, 3) + ".wav");
    }

    /*
    -X-
    XXX
    -X-

    --X--
    -XXX-
    XXXXX
    -XXX-
    --X--

    ---X---
    --XXX--
    -XXXXX-
    XXXXXXX
    -XXXXX-
    --XXX--
    ---X---

    5
    13
    25
    41
    f(x) = 2(x+1)(x+2) + 1
    f(x) = 2x ^ 2 + 6x + 5


     */

    @Override
    public void tick() {
        super.tick();

        Game.getInstance().getCamera().setShake(1 + currentRadius / 2f);
        int points = 2 * currentRadius * (currentRadius - 1) + 1;
        for (int i = 0; i < points; i++) {
            double r = Math.random() * currentRadius * 32;
            double theta = Math.random() * 2 * Math.PI;
            int x = (int) (r * Math.cos(theta));
            int y = (int) (r * Math.sin(theta));
            Game.getInstance().getWorld().addParticle(new FireParticle(this.x + x, this.y + y));
        }

        Shape ellipse = new Ellipse2D.Double(x - currentRadius * 32, y - currentRadius * 32,
                currentRadius * 64, currentRadius * 64);
        for (GameObject object : Game.objectsIn(ellipse, getCollisionMask())) {
            if (object instanceof Damageable) {
                ((Damageable) object).damage(damage, this);
            }
        }

        if (currentRadius < radius) {
            currentRadius++;
        } else {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }

    @Override
    public void collision(GameObject other) {

    }
}
