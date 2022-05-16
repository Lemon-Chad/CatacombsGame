package com.lemon.catacombs.objects.entities.enemies;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.AnimationSpace;
import com.lemon.catacombs.engine.render.BlendSpace;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Collectable;
import com.lemon.catacombs.objects.entities.PathingObject;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.EnemyBullet;
import com.lemon.catacombs.objects.ui.DMGNumber;
import com.lemon.catacombs.ui.Stats;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends PathingObject {
    private @Nullable DMGNumber dmgNumber;
    private int stateDuration;
    private double launchAngle;
    private double evadeAngle;
    private int state = State.CHASE;
    private boolean cancelLoot = false;
    private final AnimationSpace animationSpace;
    private final BlendSpace<String> directions;

    public Enemy(int x, int y, int health) {
        super(x, y, ID.Enemy, new int[]{ID.Block}, health);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.PLAYER);

        addObstacle(Layers.BLOCKS);
        addCost(Layers.ENEMY, 64);

        addCollisionLayer(Layers.ENEMY);

        animationSpace = new AnimationSpace();
        animationSpace.addAnimation("left", Animation.LoadSpriteSheet("/sprites/enemy/left.png",
                1, 2).setSpeed(300));
        animationSpace.addAnimation("right", Animation.LoadSpriteSheet("/sprites/enemy/right.png",
                1, 2).setSpeed(300));
        animationSpace.addAnimation("up", Animation.LoadSpriteSheet("/sprites/enemy/up.png",
                1, 2).setSpeed(300));
        animationSpace.addAnimation("down", Animation.LoadSpriteSheet("/sprites/enemy/down.png",
                1, 2).setSpeed(300));

        directions = new BlendSpace<>();
        directions.add(-1, 0, "left");
        directions.add(1, 0, "right");
        directions.add(0, -0.9f, "up");
        directions.add(0, 0.9f, "down");
    }

    public void setState(int state, int duration) {
        state(state, duration);
    }

    public static class State {
        private static int states = 0;

        public static final int CHASE = newState();
        public static final int EVADE = newState();
        public static final int STUN = newState();

        private static final int MAX_STATE = states;

        public static int newState() {
            return states++;
        }
    }

    public int getState() {
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
        tick(32, 32);
    }

    protected abstract int getSize();
    protected abstract Color getColor();

    @Override
    public void render(Graphics g) {
        super.render(g);

        Vector velocity = new Vector(getVelX(), getVelY()).normalize();
        directions.set((float) velocity.x, (float) velocity.y);
        animationSpace.startAnimation(directions.get());

        double alpha = Math.min(1, Math.max(0, state == State.EVADE ? (15 - stateDuration) / 60.0 : 1.0));

        int width = getSize();
        int height = getSize();
        g.setColor(getColor(getColor()));
        g.fillRect(getX(), getY(), width, height);
    }

    protected void tick(int maxDepth, int stepSize) {
        super.tick();

        Player player = Game.getInstance().getPlayer();
        if (player != null) {
            if (Point.distance(x, y, player.getX(), player.getY()) > 10_000) {
                // Despawn
                destroy();
            }
        }

        stateDuration--;
        if (state == State.CHASE) {
            Vector pathVelocity = path(maxDepth, stepSize, getSpeed());
            setVelX((float) pathVelocity.x);
            setVelY((float) pathVelocity.y);
        } else if (state == State.EVADE) {
            if (stateDuration <= 0) {
                state = State.CHASE;
            }
            setVelX(15 * (float) Math.cos(evadeAngle));
            setVelY(15 * (float) Math.sin(evadeAngle));
        } else if (state == State.STUN) {
            if (stateDuration <= 0) {
                state = State.CHASE;
            }
            setVelX((float) Math.cos(launchAngle));
            setVelY((float) Math.sin(launchAngle));
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
        Stats.getStats().addKills(1);
        Game.getInstance().getCamera().setShake(2f);
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
        Game.getInstance().getAudioHandler().playSound("/sounds/pistol/fire" + (int) (1 + Math.random() * 2)
                + ".wav", 0.25f, false);
    }

    @Override
    public void invincible() {
        setInvincibility(10);
    }

    @Override
    public boolean damage(int damage) {
        if (getInvincibility() <= 0) {
            if (dmgNumber == null || dmgNumber.isDead()) {
                dmgNumber = new DMGNumber(damage, x, y, Color.RED, 32);
                Game.getInstance().getWorld().addObject(dmgNumber);
            } else {
                dmgNumber.stack(damage, x, y);
            }
            Stats.getStats().addDamage(damage);
        }
        boolean a = super.damage(damage);
        stateDuration = 10;
        state = State.STUN;
        return a;
    }

    @Override
    public boolean damage(int damage, GameObject source) {
        Game.playSound("/sounds/hit/hit" + (int) (1 + Math.random() * 2) + ".wav");
        launchAngle = Math.atan2(this.y + 16 - source.getY(), this.x + 16 - source.getX());
        return damage(damage);
    }

    @Override
    public Rectangle getBounds() {
        // 15x / 5 - 3x / 5
        return new Rectangle(x, y, getSize(), getSize());
    }

    protected abstract void onPlayerHit(Player player);

    protected void setEvadeAngle(double evadeAngle) {
        this.evadeAngle = evadeAngle;
    }

    protected void state(int state, int duration) {
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
