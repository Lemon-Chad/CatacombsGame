package com.lemon.catacombs.items.guns.rifles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.ImpactGrenade;

public class ThumperRifle extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/grenadegun.png").originFromUV();
    private boolean firing = false;
    private final int cookTime;
    private final int radius;
    /*
    * __Thumper Rifle__
    * - Explosive damage
    * - Medium accuracy
    * - Low rate of fire
    * - High recoil
    * - Low clip size
    * - Low bullet speed
     */
    public ThumperRifle() {
        super(50, 80, 0.5, 0.9, 45, 75, 0.5,
                1.5, 3, 6);
        cookTime = Utils.intRange(30, 60);
        radius = Utils.intRange(5, 15);
    }

    @Override
    protected void onShoot(Player player) {
        shoot(player, 10f, getDamage(), getBloom(), new ImpactGrenade(player.getX() + 16, player.getY() + 16,
                cookTime, getDamage(), radius));
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
        return "/sounds/thumper/";
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
        return 1.5f;
    }
}
