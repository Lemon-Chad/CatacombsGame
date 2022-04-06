package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.physics.CollisionObject;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.AnimationSpace;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Player extends CollisionObject {

    private boolean up = false, down = false, left = false, right = false;

    private boolean moving;
    private final AnimationSpace idle;
    private final AnimationSpace walk;

    public Player(int x, int y) {
        super(x, y, ID.Player, new int[]{ ID.Block });

        Game.getInstance().player = this;

        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);

        idle = new AnimationSpace();
        idle.addAnimation("left", Animation.LoadSpriteSheet("/sprites/player/idle_left.png", 4, 1).setSpeed(100));
        idle.addAnimation("right", Animation.LoadSpriteSheet("/sprites/player/idle_right.png", 4, 1).setSpeed(100));
        idle.addAnimation("up", Animation.LoadSpriteSheet("/sprites/player/idle_up.png", 4, 1).setSpeed(100));
        idle.addAnimation("down", Animation.LoadSpriteSheet("/sprites/player/idle_down.png", 4, 1).setSpeed(100));

        walk = new AnimationSpace();
        walk.addAnimation("left", Animation.LoadSpriteSheet("/sprites/player/walk_left.png", 4, 1).setSpeed(100));
        walk.addAnimation("right", Animation.LoadSpriteSheet("/sprites/player/walk_right.png", 4, 1).setSpeed(100));
        walk.addAnimation("up", Animation.LoadSpriteSheet("/sprites/player/walk_up.png", 4, 2).setSpeed(100));
        walk.addAnimation("down", Animation.LoadSpriteSheet("/sprites/player/walk_down.png", 4, 2).setSpeed(100));

        setAnimation("down");

        Game.onKeyPressed(KeyEvent.VK_W, event -> up = true);
        Game.onKeyPressed(KeyEvent.VK_S, event -> down = true);
        Game.onKeyPressed(KeyEvent.VK_A, event -> left = true);
        Game.onKeyPressed(KeyEvent.VK_D, event -> right = true);

        Game.onKeyReleased(KeyEvent.VK_W, event -> up = false);
        Game.onKeyReleased(KeyEvent.VK_S, event -> down = false);
        Game.onKeyReleased(KeyEvent.VK_A, event -> left = false);
        Game.onKeyReleased(KeyEvent.VK_D, event -> right = false);

        Game.onMouseEvent(MouseEvents.MousePressed, this::shootLaser);
    }

    @Override
    public void tick() {
        super.tick();

        boolean moving = velX != 0 || velY != 0;
        if (this.moving != moving) {
            this.moving = moving;
            walk.reset();
            idle.reset();
        }

        if (up) velY = -5;
        else if (down) velY = 5;
        else velY = 0;

        if (left) velX = -5;
        else if (right) velX = 5;
        else velX = 0;
    }

    @Override
    public void render(Graphics g) {
        if (left) setAnimation("left");
        else if (right) setAnimation("right");
        else if (up) setAnimation("up");
        else if (down) setAnimation("down");

        frame();

        if (moving) {
            walk.getFrame().render(g, x, y - 32, 64, 64);
        } else {
            idle.getFrame().render(g, x, y - 32, 64, 64);
        }
    }

    private void setAnimation(String name) {
        idle.startAnimation(name);
        walk.startAnimation(name);
    }

    private void frame() {
        idle.update();
        walk.update();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 64, 32);
    }

    private void shootLaser(MouseEvent e) {
        Point mouse = e.getPoint();
//        mouse.move((int) Game.getInstance().getCamera().getX(), (int) Game.getInstance().getCamera().getY());
        double angle = Math.atan2(mouse.y - y, mouse.x - x);
        float velX = (float) Math.cos(angle) * 5;
        float velY = (float) Math.sin(angle) * 5;
        Laser laser = new Laser(x + 32, y);
        laser.setVelX(velX);
        laser.setVelY(velY);
        Game.getInstance().getWorld().addObject(laser);
    }
}
