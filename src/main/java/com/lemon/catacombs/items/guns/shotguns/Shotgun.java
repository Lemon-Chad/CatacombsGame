package com.lemon.catacombs.items.guns.shotguns;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Camera;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.entities.Player;

public abstract class Shotgun extends Gun {
    protected int pelletCount;
    protected double knockback;

    public Shotgun(int minDMG, int maxDMG, double minBloom, double maxBloom, int minFireRate, int maxFireRate,
                   double minRecoil, double maxRecoil, int minAmmo, int maxAmmo, int minPellets, int maxPellets,
                   double minKnockback, double maxKnockback) {
        super(minDMG, maxDMG, minBloom, maxBloom, minFireRate, maxFireRate, minRecoil, maxRecoil, minAmmo, maxAmmo);
        pelletCount = (int) Utils.range(minPellets, maxPellets);
        knockback = Utils.range(minKnockback, maxKnockback);
    }

    protected void fireBlast(Player player, float speed, float speedDecay, int dmg, double bloom, int pellets) {
        for (int i = 0; i < pellets; i++) {
            player.shoot((float) Utils.range(speedDecay * speed, speed), dmg, bloom);
        }
    }

    protected void knockback(Player player, double recoil) {
        player.knockback(recoil);
        Camera cam = Game.getInstance().getCamera();
        cam.setShake(1 + (float) recoil / 10f);
        cam.setZoom(1 + (float) recoil / 50f);
    }

    public int getPellets() {
        return pelletCount;
    }

    public double getKnockback() {
        return knockback;
    }

    public String audioPath() {
        return "/sounds/shotgun/";
    }

    @Override
    public float getScale() {
        return 1.23f;
    }
}
