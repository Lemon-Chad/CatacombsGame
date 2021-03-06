package com.lemon.catacombs.items.guns.rifles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.effects.status.Web;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.ImpactGrenade;

public class WebRifle extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/webcannon.png").originFromUV();
    private boolean firing = false;
    private final int lifespan;
    private final int radius;
    /*
    * __Web Rifle__
    * - No damage
    * - High accuracy
    * - Medium rate of fire
    * - Low recoil
    * - Low clip size
    * - Low bullet speed
    * - Webs!
     */
    public WebRifle() {
        super(0, 0, 0.1, 0.7, 10, 15, 0.1,
                0.2, 30, 60);
        lifespan = Utils.intRange(300, 500);
        radius = Utils.intRange(15, 32);
    }

    @Override
    protected void onShoot(Player player) {
        shoot(player, 10f, getDamage(), getBloom(), new Web(player.getX() + 16, player.getY() + 16, radius,
                lifespan));
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
