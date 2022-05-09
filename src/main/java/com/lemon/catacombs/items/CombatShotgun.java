package com.lemon.catacombs.items;

import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.entities.Player;

public class CombatShotgun extends Shotgun {
    /*
    * __Combat Shotgun__
    * - High damage
    * - Low accuracy
    * - Low rate of fire
    * - High recoil
    * - Low clip size
    * - Large pellet count
    * - High knockback
    * - Medium bullet speed
     */
    public CombatShotgun() {
        super(20, 40, 0.4, 1.2, 20, 40, 1, 2,
                7, 15, 10, 20, 15, 30);
    }

    @Override
    void onShoot(Player player) {
        fireBlast(player, 20f, 0.6f, getDamage(), getBloom(), getPellets());
        knockback(player, getKnockback());
    }

    @Override
    public boolean isDual() {
        return false;
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
    public Sprite getSprite() {
        return Sprite.LoadSprite("/sprites/guns/shotgun.png");
    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }
}
