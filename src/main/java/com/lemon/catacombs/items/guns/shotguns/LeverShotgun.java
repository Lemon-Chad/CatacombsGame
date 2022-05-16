package com.lemon.catacombs.items.guns.shotguns;

import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.entities.Player;

public class LeverShotgun extends Shotgun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/leveraction.png").originFromUV();
    /*
    * __Lever Shotgun__
    * - Very high damage
    * - Medium accuracy
    * - Low rate of fire
    * - Medium recoil
    * - Very Low clip size
    * - Medium pellet count
    * - Low knockback
    * - Medium bullet speed
     */

    public LeverShotgun() {
        super(40, 60, 0.3, 0.8, 40, 60, 0.5,
                1, 5, 10, 3, 7, 5, 10);
    }

    @Override
    protected void onShoot(Player player) {
        fireBlast(player, 25f, 0.9f, getDamage(), getBloom(), getPellets());
        knockback(player, getKnockback());
    }

    @Override
    public boolean isDual() {
        return false;
    }

    @Override
    public boolean isLever() {
        return true;
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }
}
