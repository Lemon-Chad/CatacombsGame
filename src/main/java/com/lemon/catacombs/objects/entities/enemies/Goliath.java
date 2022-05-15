package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.items.guns.pistols.GoldenRevolver;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class Goliath extends Enemy {
    public static final int FIRE_RATE = 20;
    private int cooldown = 0;

    public Goliath(int x, int y) {
        super(x, y, (int) (Math.random() * 500 + 500));
    }

    @Override
    public void tick() {
        super.tick();

        cooldown--;

        Player player = Game.getInstance().getPlayer();
        if (player != null){
            setTarget(new Point(player.getX(), player.getY()));
            if (getState() == State.CHASE && cooldown <= 0) {
                cooldown = FIRE_RATE;
                shoot(player, 10, 10);
            }
        } else {
            clearTarget();
        }
    }

    @Override
    protected void onDeath() {
        cancelLoot();
        super.onDeath();
        dropLoot();
    }

    @Override
    protected void dropLoot() {
        Game.getInstance().getWorld().addObject(Weapon.dropWeapon(new GoldenRevolver(), x, y));
    }

    @Override
    protected int getSpeed() {
        return 2;
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        // Health bar
        g.setColor(Color.WHITE);
        int w = 96 * getHealth() / getMaxHealth();
        int barX = x + (96 - w) / 2;
        int barY = y - 16;
        g.fillRect(barX, barY, w, 8);

        g.setColor(getColor(Color.BLUE));
        g.fillRect(x, y, 96, 96);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 96, 96);
    }

    @Override
    protected void onPlayerHit(Player player) {

    }
}
