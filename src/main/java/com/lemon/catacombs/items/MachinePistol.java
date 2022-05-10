package com.lemon.catacombs.items;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.entities.Player;

public class MachinePistol extends Gun {
    private boolean firing = false;
    private int fireTime = 0;
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/machinepistol.png");

    /*
    * __Machine Pistol__
    * - Low damage
    * - Low accuracy
    * - High rate of fire
    * - Medium recoil
    * - High clip size
    * - High bullet speed
     */

    public MachinePistol() {
        super(10, 20, 0.4, 1.2, 5, 10, 0.2, 0.5, 60, 120);
    }

    @Override
    void onShoot(Player player) {
        Game.playSound(audioPath() + "fire" + (int) Utils.range(1, 3) + ".wav");
        player.shoot(30, getDamage(), getBloom());
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
        return "/sounds/uzi/";
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void tick() {
        super.tick();
        if (firing && ammo > 0) {
            fireTime++;
            Camera cam = Game.getInstance().getCamera();
            cam.setShake(1 + fireTime / 1_000.0f);
            cam.setZoom(1 + fireTime / 3_000.0f);
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
        fireTime = 0;
    }
}
