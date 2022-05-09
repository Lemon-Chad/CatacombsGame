package com.lemon.catacombs.objects.projectiles;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.AudioHandler;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.enemies.Enemy;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ThrownWeapon extends GameObject {
    private double spin;
    private final Sprite sprite;
    private final int width;
    private final int height;
    private final AudioHandler.Sound sound;
    private int life = 80;

    public ThrownWeapon(Sprite sprite, int x, int y, double throwAngle) {
        super(x, y, ID.PlayerProjectile);
        this.spin = Math.random();
        this.sprite = sprite;
        width = (int) (sprite.getImage().getWidth() * 1.5);
        height = (int) (sprite.getImage().getHeight() * 1.5);
        setVelX((float) Math.cos(throwAngle) * 20);
        setVelY((float) Math.sin(throwAngle) * 20);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.ENEMY);
        addCollisionLayer(Layers.PLAYER_PROJECTILES);
        sound = Game.playSound("/sounds/throw.wav");
    }

    @Override
    public void tick() {
        this.spin += 0.5;
        this.life--;
        if (life <= 0) {
            destroy();
        }

        x += getVelX();
        y += getVelY();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void render(Graphics g) {
        BufferedImage sprite = this.sprite.getImage();


        BufferedImage rotated = new BufferedImage(width, height, sprite.getType());

        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(spin, width / 2f, height / 2f);
        g2d.drawImage(sprite, 0, 0, width, height, null);
        g2d.dispose();

        g.drawImage(rotated, x, y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public void collision(GameObject other) {
        if (other.getId() == ID.Enemy) {
            Enemy enemy = (Enemy) other;
            enemy.cancelLoot();
            enemy.damage(enemy.getHealth(), this);
            Utils.bloodsplosion(x, y, enemy.getMaxHealth() * 10, 1, 10);
        }
        destroy();
    }

    public double getSpin() {
        return spin;
    }

    @Override
    public void destroy() {
        super.destroy();
        sound.stop();
    }
}
