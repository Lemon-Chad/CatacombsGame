package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class Shadow extends Enemy {
    public static final int EVADE_SPEED = 200;
    private int evadeCooldown;

    public Shadow(int x, int y) {
        super(x, y, (int) (Math.random() * 30 + 30));
    }

    @Override
    protected int getSpeed() {
        return 8;
    }

    @Override
    public void tick() {
        if (getState() == State.EVADE) {
            setVelX(15 * (float) Math.cos(getEvadeAngle()));
            setVelY(15 * (float) Math.cos(getEvadeAngle()));
        }

        super.tick();

        evadeCooldown--;
        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            setTarget(new Point(player.getX(), player.getY()));
        } else {
            clearTarget();
        }
    }

    @Override
    protected int getSize() {
        return 32;
    }

    @Override
    protected Color getColor() {
        return new Color(75, 0, 120, 45);
    }

    @Override
    public boolean damage(int damage, GameObject source) {
        if (evadeCooldown > 0) {
            return super.damage(damage, source);
        }
        Point target = new Point(source.getX(), source.getY());
        double angle = Math.atan2(y - target.y, x - target.x);
        // Evade perpendicular to the angle
        angle += Math.PI / 2;
        setEvadeAngle(angle);
        evadeCooldown = EVADE_SPEED;
        state(State.EVADE, 30);
        return true;
    }

    @Override
    protected void onPlayerHit(Player player) {
        player.damage(10, this);
    }
}
