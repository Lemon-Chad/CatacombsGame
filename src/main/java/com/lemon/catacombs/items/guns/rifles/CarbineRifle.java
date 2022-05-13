package com.lemon.catacombs.items.guns.rifles;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.entities.Player;

public class CarbineRifle extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/carbine.png");
    private boolean firing = false;
    /*
    * __Carbine Rifle__
    * - Medium damage
    * - Medium accuracy
    * - High rate of fire
    * - High recoil
    * - High clip size
    * - Medium bullet speed
     */
    public CarbineRifle() {
        super(20, 30, 0.5, 0.9, 5, 10, 0.1,
                0.7, 80, 160);
    }

    @Override
    protected void onShoot(Player player) {
        player.shoot(20f, getDamage(), getBloom());
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
        return true;
    }

    @Override
    public String audioPath() {
        return "/sounds/carbine/";
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void tick() {
        super.tick();
        if (firing) {
            shoot(Game.getInstance().getPlayer());
        }
    }

    @Override
    public void startFire() {
        firing = true;
    }

    @Override
    public void stopFire() {
        firing = false;
    }

    @Override
    public float getScale() {
        return 1.35f;
    }
}
