package com.lemon.catacombs.items;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.Sprite;
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

    static Weapon generateWeapon() {
        int type = (int) (Math.random() * 2);
        // Will add more in future
        //noinspection SwitchStatementWithTooFewBranches
        switch (type) {
            case 1:
                // Shotgun
                return generateShotgun();
            default:
                // Pistol
                return generatePistol();
        }
    }

    static Collectable dropWeapon(Weapon weapon, int x, int y) {
        return new Collectable(
                weapon.getSprite(),
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

    boolean canFire();
}
