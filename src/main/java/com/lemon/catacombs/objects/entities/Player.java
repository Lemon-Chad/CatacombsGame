package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.AnimationSpace;
import com.lemon.catacombs.engine.render.BlendSpace;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.items.MeleeRange;
import com.lemon.catacombs.items.guns.shotguns.Shotgun;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.items.melee.MeleeWeapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.enemies.Enemy;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.objects.particles.PickupParticle;
import com.lemon.catacombs.objects.projectiles.Bullet;
import com.lemon.catacombs.objects.projectiles.PlayerBullet;
import com.lemon.catacombs.objects.projectiles.ThrownLeverShotgun;
import com.lemon.catacombs.objects.projectiles.ThrownWeapon;
import com.lemon.catacombs.objects.ui.FadeOut;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Player extends Damageable {

    private boolean up = false, down = false, left = false, right = false;

    private boolean moving;

    private static final double JUMP_SPEED = 0.07f;

    private int prejump;
    private double jump;
    private boolean jumping;
    private int bHopDelay;

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

    private final AnimationSpace idleSpace;
    private final AnimationSpace walkSpace;
    private final BlendSpace<String> directions;

    public Player(int x, int y) {
        super(x, y, ID.Player, new int[]{ ID.Block }, 100);

        Game.getInstance().setPlayer(this);

        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);

        Game.onKeyPressed(KeyEvent.VK_W, event -> up = true);
        Game.onKeyPressed(KeyEvent.VK_S, event -> down = true);
        Game.onKeyPressed(KeyEvent.VK_A, event -> left = true);
        Game.onKeyPressed(KeyEvent.VK_D, event -> right = true);

        Game.onKeyPressed(KeyEvent.VK_SHIFT, event -> useStamina(50, this::dash));
        Game.onKeyPressed(KeyEvent.VK_SPACE, event -> prejump = 8);

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

        idleSpace = new AnimationSpace();
        idleSpace.addAnimation("left", Animation.LoadSpriteSheet("/sprites/player/idle/left.png",
                2, 32, 32).setSpeed(600));
        idleSpace.addAnimation("right", Animation.LoadSpriteSheet("/sprites/player/idle/right.png",
                2, 32, 32).setSpeed(600));
        idleSpace.addAnimation("up", Animation.LoadSpriteSheet("/sprites/player/idle/up.png",
                2, 32, 32).setSpeed(600));
        idleSpace.addAnimation("down", Animation.LoadSpriteSheet("/sprites/player/idle/down.png",
                2, 32, 32).setSpeed(600));

        walkSpace = new AnimationSpace();
        walkSpace.addAnimation("left", Animation.LoadSpriteSheet("/sprites/player/walk/left.png",
                8, 32, 32).setSpeed(100));
        walkSpace.addAnimation("right", Animation.LoadSpriteSheet("/sprites/player/walk/right.png",
                8, 32, 32).setSpeed(100));
        walkSpace.addAnimation("up", Animation.LoadSpriteSheet("/sprites/player/walk/up.png",
                8, 32, 32).setSpeed(100));
        walkSpace.addAnimation("down", Animation.LoadSpriteSheet("/sprites/player/walk/down.png",
                8, 32, 32).setSpeed(100));

        idleSpace.startAnimation("down");

        directions = new BlendSpace<>();
        directions.add(-1, 0, "left");
        directions.add(1, 0, "right");
        directions.add(0, -0.9f, "up");
        directions.add(0, 0.9f, "down");
    }

    @Override
    public void tick() {
        super.tick();
        interacting--;
        bHopDelay--;

        regulateHands();

        prejump--;
        if (prejump > 0 && !jumping) {
            useStamina(10, this::startJump);
            prejump = 0;
        }

        // Friction
        setVelX((float) Utils.approachZero(getVelX(), FRICTION));
        setVelY((float) Utils.approachZero(getVelY(), FRICTION));

        if (!jumping && bHopDelay <= 0) {
            maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, FRICTION);
            stamina = Math.min(stamina + 1, MAX_STAMINA);
            if (bHopDelay > -10) {
                maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, FRICTION * 2);
            }
            normalizeVelocity(maxSpeed);
        } else {
            maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, FRICTION / 32);
        }

        boolean moving = getVelX() != 0 || getVelY() != 0;
        if (this.moving != moving)
            this.moving = moving;

        if (up) addSpeed(0, -1.5f);
        else if (down) addSpeed(0, 1.5f);

        if (left) addSpeed(-1.5f, 0);
        else if (right) addSpeed(1.5f, 0);

        if (equipped != null) {
            equipped.tick();
        }

        jumpTick();
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
        Game.getInstance().getCamera().setZoom(Game.getInstance().getCamera().getZoom() * 1.2f);
        extendSpeed(getVelX() * 2, getVelY() * 2);
        maxSpeed = Math.max(maxSpeed * 1.3f, MAX_SPEED * 3);
        setInvincibility(30);
    }

    private void startJump() {
        jumping = true;
        stamina -= 10;

        removeCollisionLayer(Layers.PLAYER);
    }

    private void jumpTick() {
        if (!jumping) return;
        jump += JUMP_SPEED;
        if (jump >= 2) {
            stopJump();
        }
    }

    private void stopJump() {
        jumping = false;
        jump = 0;
        bHopDelay = 7;
        addCollisionLayer(Layers.PLAYER);
    }

    private double getJumpArc() {
        return Math.sin(jump * Math.PI / 2);
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

    public Weapon getEquipped() {
        return equipped;
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
        if (getVelX() + x < maxSpeed && getVelX() + x  > -maxSpeed) addVelX(x);
        if (getVelY() + y < maxSpeed && getVelY() + y > -maxSpeed) addVelY(y);
    }

    public void extendSpeed(float x, float y) {
        addVelX(x);
        addVelY(y);
    }

    private Color getColor() {
        if (getInvincibility() > 0 && getInvincibility() % 2 == 1) return Color.WHITE;
        return Color.BLACK;
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
        // Update animations
        directions.set((float) Math.cos(mouseAngle), (float) Math.sin(mouseAngle));
        String k = directions.get();
        idleSpace.startAnimation(k);
        idleSpace.update();
        walkSpace.startAnimation(k);
        walkSpace.update();

        int y = this.y - (int) (48 * getJumpArc());

        // Render jump shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(x + 16 - (int) (24 * getJumpArc()), this.y + 64 - (int) (12 * getJumpArc()), (int) (48 * getJumpArc()), (int) (24 * getJumpArc()));

        // Render hands if in back
        if (k.equals("up")) {
            drawLeftHand(g, y, 32, 0);
            drawRightHand(g, y, -32, 0);
        }
        if (k.equals("right")) {
            drawRightHand(g, y, -16, 0);
        }
        if (k.equals("left")) {
            drawLeftHand(g, y, 16, 0);
        }


        // Render body
        BufferedImage sprite = (moving ? walkSpace.getFrame() : idleSpace.getFrame()).getImage();
        g.drawImage(Utils.scale(getColor() == Color.WHITE ? Utils.flash(sprite) : sprite, 128, 128), x - 48,
                y - 48, null);

        // Render hands if in front
        if (k.equals("down")) {
            drawLeftHand(g, y, 0, 0);
            drawRightHand(g, y, 0, 0);
        }
        if (k.equals("right")) {
            drawLeftHand(g, y, 16, 0);
        }
        if (k.equals("left")) {
            drawRightHand(g, y, -16, 0);
        }

        Rectangle bounds = getBounds();
        g.setColor(Color.RED);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawRightHand(Graphics g, int y, int xOff, int yOff) {
        int x = this.x + xOff;
        y += yOff;
        g.setColor(getHandColor());
        if (equipped != null) {
            drawWeapon(g, x + 8 + rightHand.x - (int) getVelX(), y + rightHand.y - (int) getVelY());
            return;
        }
        g.fillRect(x + 8 + rightHand.x - (int) getVelX(), y + 8 + rightHand.y - (int) getVelY(), 16, 16);
    }

    private void drawLeftHand(Graphics g, int y, int xOff, int yOff) {
        int x = this.x + xOff;
        y += yOff;
        g.setColor(getHandColor());
        if (equipped != null && equipped.isDual()) {
            drawWeapon(g, x + 8 + leftHand.x - (int) getVelX(), y + leftHand.y - (int) getVelY());
            return;
        }
        g.fillRect(x + 8 + leftHand.x - (int) getVelX(), y + 8 + leftHand.y - (int) getVelY(), 16, 16);
    }

    private Point crosshairPosition(double radius) {
        double mouseAngle = crosshair();
        return new Point(x + 12 + (int) (radius * Math.cos(mouseAngle)), y + 12 + (int) (radius * Math.sin(mouseAngle)));
    }

    private void drawWeapon(Graphics g, int xPos, int yPos) {
        BufferedImage sprite = equipped.getSprite().getImage();

        // Rotate image
        Point crosshairPosition = crosshairPosition(64);
        double angle = equipped.isMelee() ? crosshair() : Math.atan2(crosshairPosition.y - yPos, crosshairPosition.x - xPos);
        boolean flip = Math.floor(angle / Math.PI + 0.5) != 0 && !equipped.isMelee();
        if (flip) angle += Math.PI;
        if (equipped.isMelee()) angle += Math.PI / 2;
        int flipX = flip? -1 : 1;

        int width = (int) (sprite.getWidth() * 1.65);
        int height = (int) (sprite.getHeight() * 1.65);
        int swidth = (int) (width * equipped.getScale());
        int sheight = (int) (height * equipped.getScale());

        int xOffset = (int) (sprite.getWidth() > sprite.getHeight() ? -17.5 * (sprite.getWidth() / sprite.getHeight() - 1) : 0);
        int yOffset = (int) (sprite.getWidth() < sprite.getHeight() ? -17.5 * (sprite.getHeight() / sprite.getWidth() - 1) : 0);

        int x = xOffset + xPos - (swidth - width) / 2 - 20;
        int y = yOffset + yPos - (sheight - height) / 2 - 20;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.rotate(angle + equipped.getLeverTurn(), x + swidth / 2f + 10, y + sheight / 2f + 10);
        g2d.drawImage(sprite, x + (flip ? swidth : 0) + 10, y + 10,
                swidth * flipX, sheight, null);
        g2d.dispose();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y - 32, 32, 96);
    }

    private void fireWeapon(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (equipped != null && !equipped.isMelee()) {
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
            ThrownWeapon left = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle - 0.2,
                    equipped.throwDamage(), equipped.breaksOnThrow());
            ThrownWeapon right = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle + 0.2,
                    equipped.throwDamage(), equipped.breaksOnThrow());

            Game.getInstance().getWorld().addObject(left);
            Game.getInstance().getWorld().addObject(right);
        } else if (equipped.isLever() && equipped instanceof Shotgun) {
            Shotgun shotgun = (Shotgun) equipped;
            ThrownLeverShotgun leverShotgun = new ThrownLeverShotgun(shotgun, x + 16, y + 16, throwAngle);
            Game.getInstance().getWorld().addObject(leverShotgun);
        } else {
            ThrownWeapon thrown = new ThrownWeapon(equipped.getSprite(), x + 16, y + 16, throwAngle,
                    equipped.throwDamage(), equipped.breaksOnThrow());
            Game.getInstance().getWorld().addObject(thrown);
        }
        weapons[currentWeapon] = null;
        swapWeapons(currentWeapon);
    }

    private double crosshair() {
        return crosshair(this.x + 16, this.y + 16);
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

    @Override
    public int getYSort() {
        return super.getYSort() + (int) (16 * getJumpArc());
    }

    private void punch() {
        if (equipped != null && equipped.isMelee() && !equipped.isBroken()) {
            MeleeWeapon melee = (MeleeWeapon) equipped;
            melee.damage(1);
        }
        boolean left = Math.random() > 0.5 && (equipped == null || (!equipped.isMelee() || equipped.isDual()));
        Point hand = left ? leftHand : rightHand;
        double angle = crosshair();
        Point location = new Point((int)(Math.cos(angle) * 32), (int)(Math.sin(angle) * 32));
        Game.getInstance().getWorld().addObject(new Punch(x + 8 + location.x, y + 8 + location.y));
        hand.x = location.x;
        hand.y = location.y;
        if (equipped != null && equipped.isBroken()) {
            weapons[currentWeapon] = null;
            swapWeapons(currentWeapon);
            Game.getInstance().getAudioHandler().playSound("/sounds/break.wav");
            breakParticles(x + hand.x + 8, y  + hand.y + 8);
        }
    }

    private void breakParticles(int x, int y) {
        for (int i = 0; i < Utils.range(5, 15); i++) {
            Particle particle = new PickupParticle(x, y);
            Game.getInstance().getWorld().addObject(particle);
        }
    }

    public Bullet shoot(float speed, int damage, double bloom, Bullet bullet) {
        double angle = crosshair() + Math.random() * bloom - bloom / 2;
        float velX = (float) Math.cos(angle) * speed;
        float velY = (float) Math.sin(angle) * speed;
        bullet.setVelX(velX);
        bullet.setVelY(velY);
        bullet.setDamage(damage);
        Game.getInstance().getWorld().addObject(bullet);
        return bullet;
    }

    public Bullet shoot(float speed, int damage, double bloom) {
        PlayerBullet playerBullet = new PlayerBullet(this.x + 16, this.y + 16);
        return shoot(speed, damage, bloom, playerBullet);
    }

    @Override
    protected void onDeath() {
        Game.getInstance().setPlayer(null);
        Game.playSound("/sounds/hit/kill.wav");
        Utils.bloodsplosion(x, y, 1500, 3, 15);
        destroy();
        Game.later(1000, () -> Game.getInstance().getWorld().addObject(new FadeOut(100,
                () -> Game.getInstance().menu()
        )));
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
    public boolean damage(int damage) {
        super.damage(damage);
        if (getInvincibility() > 0) return false;
        Game.playSound("/sounds/hit/hit" + (int) Utils.range(1, 3) + ".wav");
        return true;
    }

    private class Punch extends GameObject {
        private final int damage;
        private final MeleeRange range;
        private final double angle;
        private int life = 2;

        public Punch(int x, int y) {
            super(x, y, ID.PlayerProjectile);
            addCollisionLayer(Layers.PLAYER_PROJECTILES);
            addCollisionMask(Layers.ENEMY);
            damage = (equipped == null || equipped.meleeDamage() == 0) ? 20 : equipped.meleeDamage();
            MeleeRange unshiftedRange = (equipped == null ? new MeleeRange(1, 1) : equipped.meleeRange());
            range = new MeleeRange(unshiftedRange.getWidth() * 32, unshiftedRange.getHeight() * 32);
            angle = crosshair();
        }

        @Override
        public void tick() {
            super.tick();
            life--;
            if (life <= 0) destroy();
        }

        @Override
        public int getYSort() {
            return 99999;
        }

        @Override
        public void render(Graphics g) {
            super.render(g);
        }

        private Polygon getShape() {
            Point origin = new Point(x + 8, y + 8);
            Point A = new Point(origin.x, origin.y - (int) (range.getHeight() / 2));
            Point B = new Point(origin.x + (int) range.getWidth(), origin.y - (int) (range.getHeight() / 2));
            Point C = new Point(origin.x + (int) range.getWidth(), origin.y + (int) (range.getHeight() / 2));
            Point D = new Point(origin.x, origin.y + (int) (range.getHeight() / 2));
            A = Utils.rotate(A, angle, origin);
            B = Utils.rotate(B, angle, origin);
            C = Utils.rotate(C, angle, origin);
            D = Utils.rotate(D, angle, origin);
            return new Polygon(new int[] {A.x, B.x, C.x, D.x}, new int[] {A.y, B.y, C.y, D.y}, 4);
        }

        @Override
        public Rectangle getBounds() {
            return getShape().getBounds();
        }

        @Override
        public boolean collidesWith(GameObject o) {
            return getShape().intersects(o.getBounds());
        }

        @Override
        public void collision(GameObject other) {
            if (other.getId() == ID.Enemy) {
                Enemy enemy = (Enemy) other;
                enemy.damage(damage, getThis());
                Game.getInstance().getCamera().setZoom(1.1f);
            }
        }
    }
}
