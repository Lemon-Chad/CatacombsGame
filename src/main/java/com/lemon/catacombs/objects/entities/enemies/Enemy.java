package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.AudioHandler;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Collectable;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.entities.PathingObject;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.EnemyBullet;
import com.lemon.catacombs.objects.ui.DMGNumber;
import com.lemon.catacombs.ui.PlayerHUD;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public abstract class Enemy extends PathingObject {
    private @Nullable DMGNumber dmgNumber;
    private int stateDuration;
    private double launchAngle;
    private double evadeAngle;
    private State state = State.CHASE;
    private boolean cancelLoot = false;

    public Enemy(int x, int y, int health) {
        super(x, y, ID.Enemy, new int[]{ID.Block}, health);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.ENEMY);
        addCollisionMask(Layers.PLAYER);

        addObstacle(Layers.BLOCKS);
        addCost(Layers.ENEMY, 64);

        addCollisionLayer(Layers.ENEMY);
    }

    public enum State {
        CHASE, STUN, EVADE
    }

    public State getState() {
        return state;
    }

    public int getBlood() {
        return getMaxHealth() * 2;
    }

    protected Color getColor(Color desired) {
        Color color = state == State.STUN ? Color.WHITE : desired;
        double alpha = Math.min(1, Math.max(0, state == State.EVADE ? (15 - stateDuration) / 60.0 : 1.0));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255));
    }

    protected abstract int getSpeed();

    @Override
    public void tick() {
        super.tick();

        stateDuration--;
        switch (state) {
            case CHASE: {
                Vector pathVelocity = path(32, 32, getSpeed());
                setVelX((float) pathVelocity.x);
                setVelY((float) pathVelocity.y);
                break;
            }

            case STUN: {
                if (stateDuration <= 0) {
                    state = State.CHASE;
                }
                setVelX((float) Math.cos(launchAngle));
                setVelY((float) Math.sin(launchAngle));
                break;
            }

            case EVADE: {
                if (stateDuration <= 0) {
                    state = State.CHASE;
                }
                setVelX(15 * (float) Math.cos(evadeAngle));
                setVelY(15 * (float) Math.sin(evadeAngle));
            }
        }
    }

    protected double getEvadeAngle() {
        return evadeAngle;
    }

    public void explode() {
        int minForce = 1;
        int maxForce = 5;
        if (getMaxHealth() >= 1500) {
            maxForce = 20;
        } else if (getMaxHealth() >= 1000) {
            maxForce = 10;
        } else if (getMaxHealth() >= 500) {
            maxForce = 7;
        }
        Utils.bloodsplosion(x, y, getBlood(), minForce, maxForce);
    }

    @Override
    protected void onDeath() {
        PlayerHUD.kills++;
        destroy();
        explode();

        if (!cancelLoot)
            dropLoot();

        Game.playSound("/sounds/hit/kill.wav");
    }

    public void cancelLoot() {
        cancelLoot = true;
    }

    protected void dropLoot() {
        if (Math.random() < 0.5)
            return;

        Collectable loot = Math.random() < 0.25 ? dropHealth() : dropWeapon();
        Game.getInstance().getWorld().addObject(loot);
    }

    protected Collectable dropHealth() {
        return new Collectable(
                Sprite.LoadSprite("/sprites/consumables/syringe.png"),
                x, y, 3f, 500, false,
                (player, collectable) -> player.heal(10)
        );
    }

    protected Collectable dropWeapon() {
        return Weapon.dropWeapon(Weapon.generateWeapon(), x, y);
    }

    protected void shoot(Player player, int damage, float speed) {
        double angleToPlayer = Math.atan2(player.getY() - y, player.getX() - x);
        float velocityX = (float) (Math.cos(angleToPlayer) * speed);
        float velocityY = (float) (Math.sin(angleToPlayer) * speed);

        Bullet bullet = new EnemyBullet(x + 32, y + 32, damage * 2 + 6);
        bullet.setVelX(velocityX);
        bullet.setVelY(velocityY);
        bullet.setDamage(damage);

        Game.getInstance().getWorld().addObject(bullet);
        AudioHandler.Sound sound = Game.getInstance().getAudioHandler().sound("/sounds/pistol/fire" + (int) (1 + Math.random() * 2) + ".wav");
        sound.setVolume(0.25f);
        sound.play();
    }

    @Override
    public void invincible() {
        setInvincibility(10);
    }

    @Override
    public void damage(int damage) {
        if (getInvincibility() <= 0) {
            if (dmgNumber == null || dmgNumber.isDead()) {
                dmgNumber = new DMGNumber(damage, x, y, Color.RED, 32);
                Game.getInstance().getWorld().addObject(dmgNumber);
            } else {
                dmgNumber.stack(damage, x, y);
            }
            PlayerHUD.damage += damage;
        }
        super.damage(damage);
        stateDuration = 10;
        state = State.STUN;
    }

    @Override
    public void damage(int damage, GameObject source) {
        damage(damage);
        Game.playSound("/sounds/hit/hit" + (int) (1 + Math.random() * 2) + ".wav");
        launchAngle = Math.atan2(this.y + 16 - source.getY(), this.x + 16 - source.getX());
    }

    @Override
    public abstract void render(Graphics g);

    @Override
    public abstract Rectangle getBounds();

    protected abstract void onPlayerHit(Player player);

    protected void setEvadeAngle(double evadeAngle) {
        this.evadeAngle = evadeAngle;
    }

    protected void state(State state, int duration) {
        this.state = state;
        this.stateDuration = duration;
    }

    protected void evade(Player player, int duration) {
        evadeAngle = Math.atan2(this.y + 16 - player.getY(), this.x + 16 - player.getX()) + Math.random();
        stateDuration = duration;
    }

    @Override
    public void collision(GameObject other) {
        super.collision(other);
        if (other.getId() == ID.Player && state == State.CHASE) {
            Player player = (Player) other;
            onPlayerHit(player);
            evade(player, 15);
            state = State.EVADE;
        }
    }
}
