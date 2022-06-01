package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
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
import com.lemon.catacombs.objects.rooms.Pit;
import com.lemon.catacombs.objects.ui.FadeOut;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Player extends Damageable {

    private boolean up = false, down = false, left = false, right = false;

    private boolean moving;
    private boolean scanning;

    private boolean falling;
    private int fallTimer;

    private static final double JUMP_SPEED = 0.1f;
    private static final int JUMP_HEIGHT = 40;

    private int prejump;
    private double jump;
    private boolean jumping;
    private int bHopDelay;

    private static final int MAX_SPEED = 7;
    private static final float ACCEL = 1.5f;
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
        super(x, y, ID.Player, new int[]{ ID.Block, ID.Door }, 100);

        Game.getInstance().setPlayer(this);

        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.PIT);

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

        Game.onKeyPressed(KeyEvent.VK_Q, event -> scanning = true);
        Game.onKeyReleased(KeyEvent.VK_Q, event -> scanning = false);

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
        if (falling) {
            fallTimer++;
            if (fallTimer >= Pit.DEPTH) {
                destroy();
            }
            return;
        }
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
        setVelX((float) Utils.approachZero(getVelX(), getFriction(FRICTION)));
        setVelY((float) Utils.approachZero(getVelY(), getFriction(FRICTION)));

        if (!jumping && bHopDelay <= 0) {
            maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, getFriction(FRICTION));
            stamina = Math.min(stamina + 1, MAX_STAMINA);
            if (bHopDelay > -10) {
                maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, getFriction(FRICTION * 2));
            }
            normalizeVelocity(maxSpeed);
        } else {
            maxSpeed = (float) Utils.approach(maxSpeed, MAX_SPEED, getFriction(FRICTION / 32));
        }

        boolean moving = getVelX() != 0 || getVelY() != 0;
        if (this.moving != moving)
            this.moving = moving;

        if (up) addSpeed(0, -ACCEL);
        else if (down) addSpeed(0, ACCEL);

        if (left) addSpeed(-ACCEL, 0);
        else if (right) addSpeed(ACCEL, 0);

        if (equipped != null) {
            equipped.tick();
        }

        if (scanning) scan();

        jumpTick();
    }

    public void swapWeapons(int index) {
        if (equipped != null && equipped.isAutomatic()) {
            equipped.stopFire();
        }
        currentWeapon = index;
        Weapon newWeapon = weapons[currentWeapon];
        if (newWeapon != equipped && newWeapon != null) {
            Game.playSound(newWeapon.audioPath() + "equip" + Utils.intRange(1, 3) + ".wav");
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

    private void scan() {
        Game.getInstance().setPhysicsSpeed(0.25f);
    }

    private void startJump() {
        jumping = true;
        stamina -= 10;

        removeCollisionLayer(Layers.PLAYER);
        removeCollisionMask(Layers.PIT);
    }

    private void jumpTick() {
        if (!jumping) return;
        jump += JUMP_SPEED * Game.getInstance().getPhysicsSpeed();
        if (jump >= 2) {
            stopJump();
        }
    }

    private void stopJump() {
        jumping = false;
        jump = 0;
        bHopDelay = 7;
        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.PIT);
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

    private String updateAnimations() {
        double mouseAngle = crosshair();

        directions.set((float) Math.cos(mouseAngle), (float) Math.sin(mouseAngle));
        String k = directions.get();
        idleSpace.startAnimation(k);
        idleSpace.update();
        walkSpace.startAnimation(k);
        walkSpace.update();

        Vector speed = new Vector(getVelX(), getVelY()).normalize();
        directions.set((float) speed.x, (float) speed.y);
        String v = directions.get();
        if (!k.equals(v)) {
            idleSpace.getAnimation().reverse();
            walkSpace.getAnimation().reverse();
        } else {
            idleSpace.getAnimation().forwards();
            walkSpace.getAnimation().forwards();
        }

        return k;
    }

    @Override
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        float progress = 1f - (float) fallTimer / Pit.DEPTH;
        double cx = getBounds().getCenterX(), cy = getBounds().getCenterY();
        if (falling) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, progress));
            g2d.rotate(Math.PI * 2 * progress, cx, cy);
            Utils.scale(g2d, progress, cx, cy);
        }
        fallRender(g2d);
        g2d.dispose();
    }

    private void fallRender(Graphics g) {
        super.render(g);
        // Update animations
        String k = updateAnimations();

        int y = this.y - (int) (JUMP_HEIGHT * getJumpArc());

        // Render jump shadow
        g.setColor(new Color(0, 0, 0, 100));
        g.fillOval(x + 16 - (int) (24 * getJumpArc()), this.y + 40 - (int) (12 * getJumpArc()), (int) (48 * getJumpArc()), (int) (24 * getJumpArc()));

        // Render hands if in back
        if (k.equals("up")) {
            drawLeftHand(g, y, 0, true);
            drawRightHand(g, y, 0, true);
        }
        if (k.equals("right")) {
            drawRightHand(g, y, -16, false);
        }
        if (k.equals("left")) {
            drawLeftHand(g, y, 16, false);
        }


        // Render body
        BufferedImage sprite = (moving ? walkSpace.getFrame() : idleSpace.getFrame()).getImage();
        g.drawImage(Utils.scale(getColor() == Color.WHITE ? Utils.flash(sprite) : sprite, 64, 64), x - 16,
                y - 16, null);

        // Render hands if in front
        if (k.equals("down")) {
            drawLeftHand(g, y, 0, false);
            drawRightHand(g, y, 0, false);
        }
        if (k.equals("right")) {
            drawLeftHand(g, y, 16, false);
        }
        if (k.equals("left")) {
            drawRightHand(g, y, -16, false);
        }
    }

    private void drawRightHand(Graphics g, int y, int xOff, boolean swapped) {
        int x = this.x + xOff;
        g.setColor(getHandColor());
        g.fillRect(x + 8 + rightHand.x - (int) getVelX(), y + 8 + rightHand.y - (int) getVelY(), 16, 16);
        if (equipped != null && (!swapped || equipped.isDual())) {
            drawWeapon(g, x + 16 + rightHand.x - (int) getVelX(), y + 16 + rightHand.y - (int) getVelY());
        }
    }

    private void drawLeftHand(Graphics g, int y, int xOff, boolean swapped) {
        int x = this.x + xOff;
        g.setColor(getHandColor());
        g.fillRect(x + 8 + leftHand.x - (int) getVelX(), y + 8 + leftHand.y - (int) getVelY(), 16, 16);
        if (equipped != null && (swapped || equipped.isDual())) {
            drawWeapon(g, x + 16 + leftHand.x - (int) getVelX(), y + 16 + leftHand.y - (int) getVelY());
        }
    }

    private Point crosshairPosition(double radius) {
        double mouseAngle = crosshair();
        return new Point(x + 12 + (int) (radius * Math.cos(mouseAngle)), y + 12 + (int) (radius * Math.sin(mouseAngle)));
    }

    private void drawWeapon(Graphics g, int xPos, int yPos) {
        Sprite sprite = equipped.getSprite();
        BufferedImage img = sprite.getImage();

        // Rotate image
        Point crosshairPosition = crosshairPosition(64);
        double angle = equipped.isMelee() ? crosshair() : Math.atan2(crosshairPosition.y - yPos + 8, crosshairPosition.x - xPos + 8);
        boolean flip = Math.floor(angle / Math.PI + 0.5) != 0 && !equipped.isMelee();
        if (flip) angle += Math.PI;
        if (equipped.isMelee()) angle += Math.PI / 2;
        int flipX = flip? -1 : 1;

        int width = (int) (img.getWidth() * 1.3);
        int height = (int) (img.getHeight() * 1.3);
        int swidth = (int) (width * equipped.getScale());
        int sheight = (int) (height * equipped.getScale());

        int originX = (int) (sprite.getOriginX() * ((float) swidth / img.getWidth()));
        int originY = (int) (sprite.getOriginY() * ((float) sheight / img.getHeight()));

        sprite.render(g, xPos - flipX * originX, yPos - originY, swidth * flipX, sheight,
                angle + equipped.getLeverTurn());
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - 8, y - 8, 48, 48);
    }

    private void fireWeapon(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (equipped != null && !equipped.isMelee()) {
                if (equipped.getAmmo() == 0) {
                    Game.playSound(equipped.audioPath() + "empty" + Utils.intRange(1, 3) + ".wav");
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
        return super.getYSort() + (int) (JUMP_HEIGHT * getJumpArc());
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
            Game.getInstance().getWorld().addParticle(particle);
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
        onDeath(true);
    }

    protected void onDeath(boolean explode) {
        Game.getInstance().setPlayer(null);
        Game.playSound("/sounds/hit/kill.wav");
        if (explode) {
            Utils.bloodsplosion(x, y, 1500, 3, 15);
            destroy();
        }
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
    public void collision(GameObject other) {
        super.collision(other);
        if (other.getId() == ID.Pit && other.getBounds().contains(getBounds())) {
            onDeath(false);
            startFall();
        }
    }

    public void startFall() {
        // Remove collisions
        removeCollisionLayer(Layers.PLAYER);
        removeCollisionMask(Layers.BLOCKS);
        removeCollisionMask(Layers.PIT);
        // Stop movement
        setVelX(0);
        setVelY(0);
        // Start falling
        falling = true;
        fallTimer = 0;
        moving = false;
    }

    @Override
    public boolean damage(int damage) {
        super.damage(damage);
        if (getInvincibility() > 0) return false;
        Game.playSound("/sounds/hit/hit" + Utils.intRange(1, 3) + ".wav");
        return true;
    }

    public class Punch extends GameObject {
        private final int damage;
        private final MeleeRange range;
        private final double angle;
        private int life = 2;

        private Punch(int x, int y) {
            super(x, y, ID.PlayerProjectile);
            addCollisionLayer(Layers.PLAYER_PROJECTILES);
            addCollisionMask(Layers.ENEMY);
            addCollisionMask(Layers.DOORS);
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
            if (other instanceof Damageable) {
                Damageable enemy = (Damageable) other;
                enemy.damage(damage, getThis());
                Game.getInstance().getCamera().setZoom(1.1f);
            }
        }
    }

    public static double getFriction(double f) {
        return f * Game.getInstance().getPhysicsSpeed();
    }
}
