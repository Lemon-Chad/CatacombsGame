package com.lemon.catacombs.items.guns.rifles;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.guns.Gun;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.PlayerBullet;

import java.awt.*;

public class FrostbiteRifle extends Gun {
    private static final Sprite sprite = Sprite.LoadSprite("/sprites/guns/sniper.png").originFromUV();
    private boolean shooting;
    /*
     * __Frostbite Rifle__
     * - High damage
     * - High accuracy
     * - Low rate of fire
     * - High recoil
     * - Medium clip size
     * - Very high bullet speed
     */
    public FrostbiteRifle() {
        super(50, 70, 0.03, 0.1, 10, 25,
                1, 1.5, 30, 40);
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
        return "/sounds/sniper/";
    }

    @Override
    public void startFire() {
        shooting = true;
    }

    @Override
    public void stopFire() {
        shooting = false;
    }

    @Override
    protected void onShoot(Player player) {
        Bullet tracerBullet = new PlayerBullet(player.getX() + 16, player.getY() + 16) {
            int lastX = x, lastY = y;

            @Override
            public void tick() {
                lastX = x;
                lastY = y;
                super.tick();
            }

            @Override
            public void render(Graphics g) {
                g.setColor(new Color(0, 200, 255, 100));
                Point A = new Point(lastX + getSize() / 2, lastY + getSize() / 2);
                Point B = new Point(x + getSize() / 2, y);
                Point C = new Point(x + getSize() / 2, y + getSize());
                Polygon polygon = new Polygon();
                polygon.addPoint(A.x, A.y);
                polygon.addPoint(B.x, B.y);
                polygon.addPoint(C.x, C.y);
                g.fillPolygon(polygon);
                super.render(g);
            }

            @Override
            public void collision(GameObject other) {
                for (ImpactEffect effect : getImpactEffects()) {
                    effect.apply(this, other);
                }
                if (other.getId() == ID.Enemy) {
                    Enemy enemy = (Enemy) other;
                    if (enemy.getState() == Enemy.State.STUN) return;
                    if (enemy.damage(getDamage(), this)) {
                        setVelX(getVelX() * 0.8f);
                        setVelY(getVelY() * 0.8f);
                    }
                    return;
                }
                destroy();
            }
        };
        player.shoot(128f, getDamage(), getBloom(), tracerBullet);
    }

    @Override
    public void tick() {
        super.tick();
        if (shooting && ammo > 0) {
            shoot(Game.getInstance().getPlayer());
        }
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }
}
