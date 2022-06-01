package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.physics.PhysicsObject;

abstract public class Damageable extends PhysicsObject {
    private int health;
    private int maxHealth;
    private int invincibility;
    private boolean bleed = true;

    public Damageable(int x, int y, int id, int[] solids, int health) {
        super(x, y, id, solids);
        this.health = health;
        this.maxHealth = health;
    }

    public void setBleeds(boolean bleed) {
        this.bleed = bleed;
    }

    public void setHealth(int health) {
        this.health = health;
        if (health <= 0) {
            onDeath();
        }
    }

    public int getHealth() {
        return health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    protected abstract void onDeath();

    @Override
    public void tick() {
        super.tick();
        invincibility--;
    }

    public void invincible() {
        invincibility = 60;
    }

    public boolean damage(int damage) {
        if (invincibility > 0) {
            return false;
        }
        health -= damage;
        if (health <= 0) {
            onDeath();
        }
        invincible();
        if (bleed)
            Utils.bloodsplosion(x, y, damage, 1, 5);
        return true;
    }

    public boolean damage(int damage, GameObject source) {
        if (invincibility > 0) {
            return false;
        }
        damage = Math.min(damage, health);
        damage(damage);
        double launchAngle = Math.atan2(this.y + 16 - source.getY(), this.x + 16 - source.getX());
        addFVel((float) Math.cos(launchAngle) * 3, (float) Math.sin(launchAngle) * 3);
        return true;
    }

    public void heal(int heal) {
        health = Math.min(health + heal, maxHealth);
        if (health <= 0) {
            onDeath();
        }
    }

    protected int getInvincibility() {
        return invincibility;
    }

    protected void setInvincibility(int invincibility) {
        this.invincibility = invincibility;
    }

    public void setInvulnerable(boolean b) {
        invincibility = b ? 60 : 0;
    }
}
