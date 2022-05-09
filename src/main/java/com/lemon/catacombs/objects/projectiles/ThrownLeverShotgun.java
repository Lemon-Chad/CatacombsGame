package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.items.Shotgun;

import java.awt.*;

public class ThrownLeverShotgun extends ThrownWeapon {
    private final Shotgun weapon;
    private int ammo;
    private int cooldown;
    private float rotations;

    public ThrownLeverShotgun(Shotgun shotgun, int x, int y, double throwAngle) {
        super(shotgun.getSprite(), x, y, throwAngle);
        weapon = shotgun;
        ammo = shotgun.getAmmo();
        cooldown = 10;

        setVelX(getVelX() / 2);
        setVelY(getVelY() / 2);
    }

    @Override
    public void tick() {
        super.tick();

        cooldown--;
        if (cooldown <= 0 && ammo > 0) {
            shoot();
            ammo--;
            cooldown = weapon.getFireRate() / 5;
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("" + ammo, getX(), getY() - 10);
    }

    private void shoot() {
        double facing = getSpin();
        int damage = weapon.getDamage();
        double bloom = weapon.getBloom();
        for (int i = 0; i < weapon.getPellets(); i++) {
            double angle = facing + Math.random() * bloom - bloom / 2;
            float speed = (float) Utils.range(22.5, 25);
            float velX = (float) Math.cos(angle) * speed;
            float velY = (float) Math.sin(angle) * speed;
            EnvironmentBullet bullet = new EnvironmentBullet(getX(), getY());
            bullet.setVelX(velX);
            bullet.setVelY(velY);
            bullet.setDamage(damage);
            Game.getInstance().getWorld().addObject(bullet);
        }

        Game.playSound(weapon.audioPath() + "fire" + (int) Utils.range(1, 3) + ".wav");
        Game.playSound(weapon.audioPath() + "lever" + (int) Utils.range(1, 3) + ".wav");
    }
}
