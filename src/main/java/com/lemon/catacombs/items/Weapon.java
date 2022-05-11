package com.lemon.catacombs.items;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.engine.render.Spriteable;
import com.lemon.catacombs.items.guns.pistols.MachinePistol;
import com.lemon.catacombs.items.guns.pistols.Pistols;
import com.lemon.catacombs.items.guns.pistols.Revolver;
import com.lemon.catacombs.items.guns.rifles.CarbineRifle;
import com.lemon.catacombs.items.guns.rifles.ThumperRifle;
import com.lemon.catacombs.items.guns.rifles.WebRifle;
import com.lemon.catacombs.items.guns.shotguns.CombatShotgun;
import com.lemon.catacombs.items.guns.shotguns.LeverShotgun;
import com.lemon.catacombs.objects.entities.Collectable;
import com.lemon.catacombs.objects.entities.Player;

public interface Weapon {
    static Weapon generatePistol() {
        int type = (int) (Math.random() * 3);
        switch (type) {
            case 1:
                // Revolver
                return new Revolver();
            case 2:
                // Uzi
                return new MachinePistol();
            default:
                // Dual Pistols
                return new Pistols();
        }
    }

    static Weapon generateShotgun() {
        int type = (int) (Math.random() * 2);
        // Will add more in future
        //noinspection SwitchStatementWithTooFewBranches
        switch (type) {
            case 1:
                // Lever Shotgun
                return new LeverShotgun();
            default:
                // Combat Shotgun
                return new CombatShotgun();
        }
    }

    static Weapon generateRifle() {
        int type = (int) (Math.random() * 2);
        // Will add more in future
        switch (type) {
            case 1:
                // Thumper Grenadier Rifle
                return new ThumperRifle();
            case 2:
                // Web Rifle
                return new WebRifle();
            default:
                // Carbine Rifle
                return new CarbineRifle();
        }
    }

    static Weapon generateWeapon() {
        int type = (int) (Math.random() * 3);
        // Will add more in future
        switch (type) {
            case 1:
                // Shotgun
                return generateShotgun();
            case 2:
                // Rifle
                return generateRifle();
            default:
                // Pistol
                return generatePistol();
        }
    }

    static Collectable dropWeapon(Weapon weapon, int x, int y) {
        return new Collectable(
                weapon.getSpriteable(),
                x, y, 1.5f,1_000, true,
                (player, collectable) -> {
                    player.addWeapon(weapon);
                    Game.playSound(weapon.audioPath() + "equip" + (int) (1 + Math.random() * 2) + ".wav");
                }
        );
    }

    double getBloom();
    double getRecoil();
    double getLeverTurn();

    int meleeDamage();

    boolean isDual();
    boolean isLever();
    boolean isAutomatic();

    String audioPath();

    int getAmmo();

    void tick();
    int getDamage();
    void shoot(Player player);
    Sprite getSprite();

    void startFire();
    void stopFire();

    Spriteable getSpriteable();

    boolean canFire();
}
