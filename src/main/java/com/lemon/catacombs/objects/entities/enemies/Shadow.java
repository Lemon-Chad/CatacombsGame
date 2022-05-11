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
    public void render(Graphics g) {
        Color c = getColor(new Color(75, 0, 120));
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 2));
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public void damage(int damage, GameObject source) {
        if (evadeCooldown > 0) {
            super.damage(damage, source);
            return;
        }
        Point target = new Point(source.getX(), source.getY());
        double angle = Math.atan2(y - target.y, x - target.x);
        // Evade perpendicular to the angle
        angle += Math.PI / 2;
        setEvadeAngle(angle);
        evadeCooldown = EVADE_SPEED;
        state(State.EVADE, 30);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    @Override
    protected void onPlayerHit(Player player) {
        player.damage(15, this);
    }
}
