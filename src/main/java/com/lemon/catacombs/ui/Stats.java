package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class Stats {
    private static Stats instance;
    private int kills = 0;
    private int damage = 0;
    private double metersPerSecond = 0;
    private int mpsRefresh = 8;
    private int sinceLastKill;
    private int sinceLastHit;

    public double getMetersPerSecond() {
        return metersPerSecond;
    }

    public static Stats getStats() {
        if (instance == null) instance = new Stats();
        return instance;
    }

    public void reset() {
        kills = 0;
        damage = 0;
        metersPerSecond = 0;
        mpsRefresh = 8;
        sinceLastKill = 0;
        sinceLastHit = 0;
    }

    public void addKills(int amount) {
        kills += amount;
        sinceLastKill = 40;
    }

    public void addDamage(int amount) {
        damage += amount;
        sinceLastHit = 40;
    }

    public int getSinceLastHit() {
        return sinceLastHit;
    }

    public int getSinceLastKill() {
        return sinceLastKill;
    }

    public int getKills() {
        return kills;
    }

    public int getDamage() {
        return damage;
    }

    public void tick() {
        if (sinceLastKill > 0) sinceLastKill--;
        if (sinceLastHit > 0) sinceLastHit--;

        mpsRefresh--;
        if (mpsRefresh <= 0) {
            mpsRefresh = 8;

            Player player = Game.getInstance().getPlayer();
            if (player == null) metersPerSecond = 0;
            else metersPerSecond = new Point((int) player.getVelX(), (int) player.getVelY()).distance(0, 0) / 5;
        }
    }

    public int getEffectSlots() {
        if (getKills() <= 0) return 0;
        return (int) Math.floor(Math.log(getKills()) / Math.log(50));
    }
}
