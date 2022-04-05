package com.lemon.catacombs.objects;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.input.MouseEvents;
import com.lemon.catacombs.engine.physics.GameObject;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Player extends CollisionObject {

    private boolean up = false, down = false, left = false, right = false;

    public Player(int x, int y) {
        super(x, y, ID.Player, new ID[]{ ID.Block });

        Game.getInstance().player = this;

        addCollisionLayer(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);

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

        if (up) velY = -5;
        else if (down) velY = 5;
        else velY = 0;

        if (left) velX = -5;
        else if (right) velX = 5;
        else velX = 0;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.blue);
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }

    private void shootLaser(MouseEvent e) {
        System.out.println("Shoot");
        Point mouse = e.getPoint();
        double angle = Math.atan2(mouse.y - y, mouse.x - x);
        float velX = (float) Math.cos(angle) * 5;
        float velY = (float) Math.sin(angle) * 5;
        Laser laser = new Laser(x, y);
        laser.setVelX(velX);
        laser.setVelY(velY);
        Game.getInstance().getWorld().addObject(laser);
    }
}
