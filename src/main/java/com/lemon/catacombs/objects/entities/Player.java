package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.items.Shotgun;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.projectiles.PlayerBullet;
import com.lemon.catacombs.objects.projectiles.ThrownLeverShotgun;
import com.lemon.catacombs.objects.projectiles.ThrownWeapon;
import com.lemon.catacombs.objects.ui.FadeOut;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.Timer;

public class Player extends Damageable {

    private boolean up = false, down = false, left = false, right = false;
    private boolean dash = false;

    private boolean moving;

    private static final int MAX_SPEED = 7;
    private static final float FRICTION = 0.8f;

    private float maxSpeed = MAX_SPEED;

    private static final Point leftHandDefault = new Point(-16, 16);
    private static final Point rightHandDefault = new Point(16, 16);

    private final Point leftHand = new Point(leftHandDefault);
    private final Point rightHand = new Point(rightHandDefault);

    private static final int MAX_STAMINA = 100;
    private int stamina = MAX_STAMINA;

    private final Weapon[] weapons = new Weapon[3];
    private int currentWeapon = 0;
    private Weapon equipped = weapons[currentWeapon];

    private int interacting = 0;

    public Player(int x, int y) {
        super(x, y, ID.Player, new int[]{ ID.Block }, 100);

        Game.getInstance().setPlayer(this);

        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);

        Game.onKeyPressed(KeyEvent.VK_W, event -> up = true);
        Game.onKeyPressed(KeyEvent.VK_S, event -> down = true);
        Game.onKeyPressed(KeyEvent.VK_A, event -> left = true);
        Game.onKeyPressed(KeyEvent.VK_D, event -> right = true);

        Game.onKeyPressed(KeyEvent.VK_SPACE, event -> dash = true);

        Game.onKeyReleased(KeyEvent.VK_W, event -> up = false);
        Game.onKeyReleased(KeyEvent.VK_S, event -> down = false);
        Game.onKeyReleased(KeyEvent.VK_A, event -> left = false);
        Game.onKeyReleased(KeyEvent.VK_D, event -> right = false);

        Game.onKeyPressed(KeyEvent.VK_1, event -> swapWeapons(0));
        Game.onKeyPressed(KeyEvent.VK_2, event -> swapWeapons(1));
        Game.onKeyPressed(KeyEvent.VK_3, event -> swapWeapons(2));

        Game.onKeyPressed(KeyEvent.VK_E, event -> interacting = 2);

        Game.onMouseEvent(MouseEvents.MousePressed, this::fireWeapon);
        Game.onMouseEvent(MouseEvents.MouseReleased, this::mouseReleased);

        Game.onKeyPressed(KeyEvent.VK_F, this::throwWeapon);
    }

    @Override
    public void tick() {
        super.tick();
        interacting--;

        stamina = Math.min(stamina + 1, MAX_STAMINA);

        regulateHands();

        // Friction
        setVelX((float) Utils.approachZero(getVelX(), FRICTION));
        setVelY((float) Utils.approachZero(getVelY(), FRICTION));

        maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, FRICTION);

        boolean moving = getVelX() != 0 || getVelY() != 0;
        if (this.moving != moving)
            this.moving = moving;

        if (up) addSpeed(0, -1.5f);
        else if (down) addSpeed(0, 1.5f);

        if (left) addSpeed(-1.5f, 0);
        else if (right) addSpeed(1.5f, 0);

        if (dash) {
            useStamina(50, this::dash);
            dash = false;
        }

        normalizeVelocity(maxSpeed);

        if (equipped != null) {
            equipped.tick();
        }
    }

    public void swapWeapons(int index) {
        if (equipped != null && equipped.isAutomatic()) {
            equipped.stopFire();
        }
        currentWeapon = index;
        Weapon newWeapon = weapons[currentWeapon];
        if (newWeapon != equipped && newWeapon != null) {
            Game.playSound(newWeapon.audioPath() + "equip" + (int) Utils.range(1, 3) + ".wav");
        }
        equipped = newWeapon;
    }

    private void regulateHands() {
        // Make hands approach origin
        leftHand.x = (int) Utils.approach(leftHand.x, leftHandDefault.x, 1);
        leftHand.y = (int) Utils.approach(leftHand.y, leftHandDefault.y, 1);
        rightHand.x = (int) Utils.approach(rightHand.x, rightHandDefault.x, 1);
        rightHand.y = (int) Utils.approach(rightHand.y, rightHandDefault.y, 1);
    }

    private void dash() {
        dash = false;
        extendSpeed(getVelX() * 2, getVelY() * 2);
        maxSpeed = Math.max(maxSpeed * 1.3f, MAX_SPEED * 3);
        setInvincibility(30);
    }

    public boolean isInteracting() {
        return interacting > 0;
    }

    public void addWeapon(Weapon weapon) {
        for (int i = 0; i < weapons.length; i++) {
            if (weapons[i] == null) {
                weapons[i] = weapon;
                swapWeapons(i);
                return;
            }
        }
        weapons[currentWeapon] = weapon;
        swapWeapons(currentWeapon);
    }

    public Weapon getWeapon(int i) {
        return weapons[i];
    }

    public int getSelected() {
        return currentWeapon;
    }

    interface StaminaAction {
        void execute();
    }

    private void useStamina(int amount, StaminaAction action) {
        if (stamina < amount) return;
        stamina -= amount;
        action.execute();
    }

    public void addSpeed(float x, float y) {
        if (getVelX() + x < MAX_SPEED && getVelX() + x  > -MAX_SPEED) addVelX(x);
        if (getVelY() + y < MAX_SPEED && getVelY() + y > -MAX_SPEED) addVelY(y);
    }

    public void extendSpeed(float x, float y) {
        addVelX(x);
        addVelY(y);
    }

    private Color getColor() {
        if (getInvincibility() > 0 && getInvincibility() % 2 == 1) return Color.WHITE;
        double health = (double) getHealth() / getMaxHealth();
        double hue = health / 3;
        return Color.getHSBColor((float) hue, 1, 1);
    }

    private Color getHandColor() {
        Color c = getColor();
        float factor = 0.816f;
        return new Color((int) (c.getRed() * factor), (int) (c.getGreen() * factor), (int) (c.getBlue() * factor),
                c.getAlpha());
    }

    @Override
    public void render(Graphics g) {
        double mouseAngle = crosshair();
        Point position = crosshairPosition(32);

        // Render body
        g.setColor(getColor());
        g.fillRect(x, y, 32, 32);
        // Render hands
        g.setColor(getHandColor());
        if (equipped != null) {
            drawWeapon(g, x + 8 + rightHand.x - (int) getVelX(), y - 8 + rightHand.y - (int) getVelY());

            if (equipped.isDual()) {
                drawWeapon(g, x + 8 + leftHand.x - (int) getVelX(), y - 8 + leftHand.y - (int) getVelY());
            } else {
                g.fillRect(x + 8 + leftHand.x - (int) getVelX(), y + 8 + leftHand.y - (int) getVelY(), 16, 16);
            }

            // Bloom arc
            double bloom = equipped.getBloom() * 180 / Math.PI;
            if (bloom > 0) {
                g.setColor(Color.WHITE);
                // Change arc thickness based on bloom
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(3));
                g.drawArc(x - 32, y - 32, 96, 96, (int) (-mouseAngle * 180 / Math.PI - bloom / 2), (int) (bloom));
            }
        } else {
            g.fillRect(x + 8 + rightHand.x - (int) getVelX(), y + 8 + rightHand.y - (int) getVelY(), 16, 16);
            g.fillRect(x + 8 + leftHand.x - (int) getVelX(), y + 8 + leftHand.y - (int) getVelY(), 16, 16);
        }
        // Render crosshair
        g.setColor(Color.WHITE);
        g.fillOval(position.x, position.y, 8, 8);
    }

    private Point crosshairPosition(double radius) {
        double mouseAngle = crosshair();
        return new Point(x + 12 + (int) (radius * Math.cos(mouseAngle)), y + 12 + (int) (radius * Math.sin(mouseAngle)));
    }

    private void drawWeapon(Graphics g, int xPos, int yPos) {
        BufferedImage sprite = equipped.getSprite().getImage();

        // Rotate image
        Point crosshairPosition = crosshairPosition(64);
        double angle = Math.atan2(crosshairPosition.y - yPos, crosshairPosition.x - xPos);
        boolean flip = Math.floor(angle / Math.PI + 0.5) != 0;
        if (flip) angle += Math.PI;
        int flipX = flip ? -1 : 1;

        int width = (int) (sprite.getWidth() * 1.5);
        int height = (int) (sprite.getHeight() * 1.5);
        BufferedImage rotated = new BufferedImage(width, height, sprite.getType());

        Graphics2D g2d = rotated.createGraphics();
        g2d.rotate(angle + equipped.getLeverTurn(), width / 2f, height / 2f);
        g2d.drawImage(sprite, flip ? width : 0, 0, width * flipX, height, null);
        g2d.dispose();

        g.drawImage(rotated, xPos, yPos, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    private void fireWeapon(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (equipped != null) {
                if (equipped.getAmmo() == 0) {
                    Game.playSound(equipped.audioPath() + "empty" + (int) Utils.range(1, 3) + ".wav");
                    return;
                }

                if (equipped.isAutomatic()) {
                    equipped.startFire();
                } else {
                    equipped.shoot(this);
                }
            }
            else punch();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            punch();
        }
    }

    private void mouseReleased(MouseEvent e) {
        if (equipped != null && equipped.isAutomatic()) {
            equipped.stopFire();
        }
    }

    private void throwWeapon(KeyEvent keyEvent) {
        if (equipped == null) return;
        double throwAngle = crosshair();
        if (equipped.isDual()) {
            ThrownWeapon left = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle - 0.2);
            ThrownWeapon right = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle + 0.2);

            Game.getInstance().getWorld().addObject(left);
            Game.getInstance().getWorld().addObject(right);
        } else if (equipped.isLever()) {
            Shotgun shotgun = (Shotgun) equipped;
            ThrownLeverShotgun leverShotgun = new ThrownLeverShotgun(shotgun, x + 16, y + 16, throwAngle);
            Game.getInstance().getWorld().addObject(leverShotgun);
        } else {
            ThrownWeapon thrown = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle);
            Game.getInstance().getWorld().addObject(thrown);
        }
        weapons[currentWeapon] = null;
        swapWeapons(currentWeapon);
    }

    private double crosshair() {
        return crosshair(this.x - 16, this.y - 16);
    }

    private double crosshair(int ox, int oy) {
        Game instance = Game.getInstance();
        Point mouse = instance.getMousePosition();
        if (mouse == null) return 0;
        double x = mouse.getX() + instance.getCamera().getX();
        double y = mouse.getY() + instance.getCamera().getY();
        double a = Math.atan2(y - oy, x - ox);
        return a - (equipped != null ? equipped.getRecoil() : 0) * (Math.floor(a / Math.PI + 0.5) != 0 ? -1 : 1);
    }

    private void punch() {
        boolean left = Math.random() > 0.5;
        Point hand = left ? leftHand : rightHand;
        double angle = crosshair();
        Point location = new Point((int)(Math.cos(angle) * 32), (int)(Math.sin(angle) * 32));
        Game.getInstance().getWorld().addObject(new Punch(x + 8 + location.x, y + 8 + location.y));
        hand.x = location.x;
        hand.y = location.y;
    }

    public void shoot(float speed, int damage, double bloom) {
        double angle = crosshair() + Math.random() * bloom - bloom / 2;
        float velX = (float) Math.cos(angle) * speed;
        float velY = (float) Math.sin(angle) * speed;
        PlayerBullet playerBullet = new PlayerBullet(this.x + 16, this.y + 16);
        playerBullet.setVelX(velX);
        playerBullet.setVelY(velY);
        playerBullet.setDamage(damage);
        Game.getInstance().getWorld().addObject(playerBullet);
    }

    @Override
    protected void onDeath() {
        Game.getInstance().setPlayer(null);
        Game.playSound("/sounds/hit/kill.wav");
        Utils.bloodsplosion(x, y, 1500, 3, 15);
        destroy();
        Timer timer = new Timer(1000, e -> Game.getInstance().getWorld().addObject(new FadeOut(100,
                () -> Game.getInstance().menu()
        )));
        timer.setRepeats(false);
        timer.start();
    }

    private Player getThis() {
        return this;
    }

    public void knockback(double recoil) {
        double angle = crosshair();
        double x = Math.cos(angle) * -recoil;
        double y = Math.sin(angle) * -recoil;
        extendSpeed((float) x, (float) y);
        maxSpeed += recoil;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return MAX_STAMINA;
    }

    @Override
    public void damage(int damage) {
        super.damage(damage);
        if (getInvincibility() > 0) return;
        Game.playSound("/sounds/hit/hit" + (int) Utils.range(1, 3) + ".wav");
    }

    private class Punch extends GameObject {
        private int life = 2;

        public Punch(int x, int y) {
            super(x, y, ID.PlayerProjectile);
            addCollisionLayer(Layers.PLAYER_PROJECTILES);
            addCollisionMask(Layers.ENEMY);
        }

        @Override
        public void tick() {
            life--;
            if (life <= 0) destroy();
        }

        @Override
        public void render(Graphics g) {}

        @Override
        public Rectangle getBounds() {
            return new Rectangle(x - 8, y - 8, 32, 32);
        }

        @Override
        public void collision(GameObject other) {
            if (other.getId() == ID.Enemy) {
                Enemy enemy = (Enemy) other;
                enemy.damage(30, getThis());
                Game.getInstance().getCamera().setZoom(1.1f);
            }
        }
    }
}
