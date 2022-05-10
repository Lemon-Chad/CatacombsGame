package com.lemon.catacombs.items;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.engine.render.Spriteable;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.particles.FireParticle;
import com.lemon.catacombs.objects.particles.Particle;

public class GoldenRevolver extends Gun {
    private final Animation animation = Animation.LoadSpriteSheet("/sprites/guns/revolver_gold.png", 19, 32, 32);
    /*
    * __REVOLVER__
    * - High damage
    * - High accuracy
    * - Low rate of fire
    * - High recoil
    * - Low clip size
    * - Medium bullet speed
     */
    public GoldenRevolver() {
        super(90, 120, 0.025, 0.05, 10, 20, 0.5,
                0.75, 6, 24);
        animation.start(1000 / 12);
    }

    @Override
    void onShoot(Player player) {
        bullet(player);
        Game.later(100, () -> bullet(player));
    }

    private void bullet(Player player) {
        player.shoot(20f, getDamage(), getBloom()).addEffect(bullet -> {
            for (int i = 0; i < 5; i++) {
                Particle fire = new FireParticle(bullet.getX() + 4 + (int) (Math.random() * 8), bullet.getY() + 4 + (int) (Math.random() * 8));
                Game.getInstance().getWorld().addObject(fire);
            }
        });
        Game.getInstance().getCamera().setZoom(1.02f);
        Game.getInstance().getCamera().setShake(1.2f);
    }

    @Override
    public int meleeDamage() {
        return 60;
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
        return true;
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
    public Sprite getSprite() {
        animation.update();
        return animation.getFrame();
    }

    @Override
    public Spriteable getSpriteable() {
        return animation;
    }

    @Override
    public void startFire() {

    }

    @Override
    public void stopFire() {

    }
}
