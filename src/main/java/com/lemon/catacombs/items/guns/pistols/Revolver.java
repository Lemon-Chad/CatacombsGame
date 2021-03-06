package com.lemon.catacombs.items.guns.pistols;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.entities.Player;

public class Revolver extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/revolver.png").originFromUV();
    /*
    * __Revolver__
    * - High damage
    * - High accuracy
    * - Low rate of fire
    * - High recoil
    * - Low clip size
    * - Medium bullet speed
     */
    public Revolver() {
        super(45, 60, 0.05, 0.1, 20, 40, 1,
                1.5, 6, 24);
    }

    @Override
    protected void onShoot(Player player) {
        shoot(player, 20f, getDamage(), getBloom());
        Game.getInstance().getCamera().setZoom(1.02f);
        Game.getInstance().getCamera().setShake(1.2f);
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
        return "/sounds/revolver/";
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
