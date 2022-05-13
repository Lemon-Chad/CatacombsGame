package com.lemon.catacombs.items.melee;

import com.lemon.catacombs.engine.render.Spriteable;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Player;

public abstract class MeleeWeapon implements Weapon {
    private final int maxDurability;
    private int durability;

    public MeleeWeapon(int durability) {
        this.maxDurability = durability;
        this.durability = durability;
    }

    public int durability() {
        return durability;
    }

    public int maxDurability() {
        return maxDurability;
    }

    @Override
    public double getDurability() {
        return (double) durability / maxDurability;
    }

    public void damage(int amount) {
        durability -= amount;
    }

    @Override
    public double getBloom() {
        return 0;
    }

    @Override
    public double getRecoil() {
        return 0;
    }

    @Override
    public double getLeverTurn() {
        return 0;
    }

    @Override
    public boolean isLever() {
        return false;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public boolean isMelee() {
        return true;
    }

    @Override
    public int getAmmo() {
        return 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public void shoot(Player player) {

    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }

    @Override
    public Spriteable getSpriteable() {
        return getSprite();
    }

    @Override
    public boolean canFire() {
        return false;
    }

    @Override
    public String audioPath() {
        return "/sounds/knife/";
    }

    @Override
    public float getScale() {
        return 1.5f;
    }

    @Override
    public boolean isBroken() {
        return durability <= 0;
    }

    @Override
    public int throwDamage() {
        return -1;
    }

    @Override
    public boolean breaksOnThrow() {
        return true;
    }
}
