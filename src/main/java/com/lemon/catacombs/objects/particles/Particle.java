package com.lemon.catacombs.objects.particles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;

abstract public class Particle extends GameObject {
    private final float friction;
    private final int decay;
    private int life = 0;

    public Particle(int x, int y, float friction, int decay) {
        super(x, y, ID.Particle);
        this.friction = friction;
        this.decay = decay * 2 + (int) (Math.random() * decay);
    }

    protected int getAlpha() {
        return Math.round((1 - Math.min(Math.max(0, (life - decay) / (float) decay), 1)) * 255);
    }

    protected double getFade() {
        return 1 - Math.min(Math.max(0, (life - decay) / (float) decay), 1);
    }

    @Override
    public void tick() {
        super.tick();

        setVelX((float) Utils.approachZero(getVelX(), friction));
        setVelY((float) Utils.approachZero(getVelY(), friction));

        x += Math.round(getVelX());
        y += Math.round(getVelY());

        life++;
        if (life >= decay * 2)
            destroy();
    }

    public int getLife() {
        return life;
    }

    public int getDecay() {
        return decay;
    }
}
