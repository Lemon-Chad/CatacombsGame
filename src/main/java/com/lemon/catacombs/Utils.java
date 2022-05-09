package com.lemon.catacombs;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.objects.particles.BloodParticle;

public class Utils {
    public static double approachZero(double n, double dn) {
        return Math.max(0, Math.abs(n) - dn) * Math.signum(n);
    }

    public static double approach(double n, double t, double dn) {
        return t + approachZero(n - t, dn);
    }

    public static void bloodsplosion(double x, double y, double amount, double min, double max) {
        for (int i = 0; i < Math.ceil(amount / 4); i++) {
            double angle = Math.random() * Math.PI * 2;
            double force = Math.random() * (max - min) + min;

            double dx = Math.cos(angle) * force;
            double dy = Math.sin(angle) * force;

            double lowerSize = Math.ceil(Math.pow(i, 0.25));
            double upperSize = Math.ceil(2 * Math.pow(i, 0.33));
            double size = Math.random() * (upperSize - lowerSize) + lowerSize;

            BloodParticle p = new BloodParticle((int) x, (int) y, (int) size);
            p.setVelX((float) dx);
            p.setVelY((float) dy);

            Game.getInstance().getWorld().addObject(p);
        }
    }

    public static double range(double min, double max) {
        return Math.random() * (max - min) + min;
    }
}
