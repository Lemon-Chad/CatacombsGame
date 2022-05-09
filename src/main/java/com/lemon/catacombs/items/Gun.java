package com.lemon.catacombs.items;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.entities.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class Gun implements Weapon {
    protected int damage;
    protected double bloom;

    protected int ammo;

    protected int cooldown;
    protected int fireRate;

    protected double currentRecoil;
    protected double recoil;

    public Gun(int minDMG, int maxDMG, double minBloom, double maxBloom, int minFireRate, int maxFireRate,
               double minRecoil, double maxRecoil, int minAmmo, int maxAmmo) {
        damage = (int) Utils.range(minDMG, maxDMG);
        bloom = Utils.range(minBloom, maxBloom);
        fireRate = (int) Utils.range(minFireRate, maxFireRate);
        recoil = Utils.range(minRecoil, maxRecoil);
        ammo = (int) Utils.range(minAmmo, maxAmmo);
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
        Game.playSound(audioPath() + "fire" + (int) Utils.range(1, 3) + ".wav");
        if (isDual()) {
            Timer timer = new Timer(60, e -> Game.playSound(audioPath() + "fire" + (int) Utils.range(1, 3) + ".wav"));
            timer.setRepeats(false);
            timer.start();
        }
        if (isLever()) {
            // Play sound after delay
            Timer timer = new Timer(40, e -> Game.playSound(audioPath() + "lever" + (int) Utils.range(1, 3) + ".wav"));
            timer.setRepeats(false);
            timer.start();
        }
        cooldown = fireRate;
        currentRecoil += recoil;
        ammo--;
    }

    abstract void onShoot(Player player);

    @Override
    public abstract Sprite getSprite();

    public int getFireRate() {
        return fireRate;
    }

    public boolean canFire() {
        return cooldown <= 0 && ammo > 0;
    }
}
