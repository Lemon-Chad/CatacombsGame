package com.lemon.catacombs.items.guns;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.engine.render.Spriteable;
import com.lemon.catacombs.items.MeleeRange;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class Gun implements Weapon {
    protected int damage;
    protected double bloom;

    protected int ammo;

    protected int cooldown;
    protected int fireRate;

    protected double currentRecoil;
    protected double recoil;

    protected final Set<BulletEffect> effects = new HashSet<>();

    public Gun(int minDMG, int maxDMG, double minBloom, double maxBloom, int minFireRate, int maxFireRate,
               double minRecoil, double maxRecoil, int minAmmo, int maxAmmo) {
        damage = Utils.intRange(minDMG, maxDMG);
        bloom = Utils.range(minBloom, maxBloom);
        fireRate = Utils.intRange(minFireRate, maxFireRate);
        recoil = Utils.range(minRecoil, maxRecoil);
        ammo = Utils.intRange(minAmmo, maxAmmo);
    }

    public Gun() {}

    @Override
    public double getBloom() {
        return bloom;
    }

    @Override
    public double getRecoil() {
        return currentRecoil;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public int getAmmo() {
        return ammo;
    }

    @Override
    public double getLeverTurn() {
        if (isLever() && cooldown > 0) {
            float x = (float) (fireRate - cooldown) / fireRate;
            float y = 1 / (1 + (float) Math.exp(5 - 10 * x));
            return y * Math.PI * 2;
        }
        return 0;
    }

    @Override
    public void tick() {
        cooldown--;
        currentRecoil *= 0.9;
    }

    @Override
    public void shoot(Player player) {
        if (cooldown > 1 || ammo == 0) {
            return;
        }
        onShoot(player);
        Game.playSound(audioPath() + "fire" + Utils.intRange(1, 3) + ".wav");
        if (isDual()) {
            Game.later(60, () -> Game.playSound(audioPath() + "fire" + Utils.intRange(1, 3) + ".wav"));
        }
        if (isLever()) {
            // Play sound after delay
            Game.later(40, () -> Game.playSound(audioPath() + "lever" + Utils.intRange(1, 3) + ".wav"));
        }
        cooldown = fireRate;
        currentRecoil += recoil;
        ammo--;
    }

    protected abstract void onShoot(Player player);

    protected Bullet shoot(Player player, float speed, int damage, double bloom, Bullet bullet) {
        return applyEffects(player, player.shoot(speed, damage, bloom, bullet));
    }

    protected Bullet shoot(Player player, float speed, int damage, double bloom) {
        return applyEffects(player, player.shoot(speed, damage, bloom));
    }

    private Bullet applyEffects(Player player, Bullet bullet) {
        for (BulletEffect effect : effects)
            effect.apply(player, bullet);
        return bullet;
    }

    @Override
    public abstract Sprite getSprite();

    @Override
    public int meleeDamage() {
        return 0;
    }

    @Override
    public MeleeRange meleeRange() {
        return new MeleeRange(1, 1);
    }

    @Override
    public double getDurability() {
        return 1;
    }

    @Override
    public boolean isMelee() {
        return false;
    }

    public int getFireRate() {
        return fireRate;
    }

    @Override
    public Spriteable getSpriteable() {
        return getSprite();
    }

    public boolean canFire() {
        return cooldown <= 0 && ammo > 0;
    }

    @Override
    public float getScale() {
        return 1;
    }

    @Override
    public boolean isBroken() {
        return false;
    }

    @Override
    public int throwDamage() {
        return -1;
    }

    @Override
    public boolean breaksOnThrow() {
        return true;
    }

    @Override
    public Weapon addEffect(BulletEffect effect) {
        effects.add(effect);
        return this;
    }
}
