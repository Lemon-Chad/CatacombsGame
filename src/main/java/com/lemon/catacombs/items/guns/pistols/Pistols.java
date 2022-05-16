package com.lemon.catacombs.items.guns.pistols;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.entities.Player;

public class Pistols extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/pistol.png").originFromUV();
    /*
    * __Pistols__
    * - Medium damage
    * - Medium accuracy
    * - Medium rate of fire
    * - Medium recoil
    * - High clip size
    * - High bullet speed
     */
    public Pistols() {
        super(10, 20, 0.1, 0.3, 10, 25, 0.4,
                0.9, 30, 50);
    }

    @Override
    protected void onShoot(Player player) {
        player.shoot(48f, getDamage(), getBloom());
        player.shoot(48f, getDamage(), getBloom());
        Game.getInstance().getCamera().setZoom(1.02f);
    }

    @Override
    public int getAmmo() {
        return ammo * 2;
    }

    @Override
    public boolean isDual() {
        return true;
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
        return sprite;
    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }

    public String audioPath() {
        return "/sounds/pistol/";
    }
}
