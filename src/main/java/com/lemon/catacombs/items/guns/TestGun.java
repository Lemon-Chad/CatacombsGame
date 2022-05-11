package com.lemon.catacombs.items.guns;

import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.entities.Player;

public class TestGun extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/pistol.png");
    /*
    * __Test Gun__
    * Hell.
     */
    public TestGun() {
        super(30, 30, 0, 0,
                0, 0, 0, 0,
                1000, 9999);
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
    public String audioPath() {
        return "/sounds/gold_revolver/";
    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }

    @Override
    protected void onShoot(Player player) {
        player.shoot(64f, getDamage(), getBloom());
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
