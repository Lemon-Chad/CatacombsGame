package com.lemon.catacombs.objects.rooms;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.physics.PhysicsObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Player;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.objects.particles.SlideParticle;

import java.awt.*;

public class Crate extends PhysicsObject {
    private final int w, h;
    private boolean falling;
    private int fallTimer;

    public Crate(int x, int y, int w, int h) {
        super(x, y, ID.Crate, new int[] { ID.Block, ID.Door, ID.Crate });
        this.w = w;
        this.h = h;

        addCollisionLayer(Layers.BLOCKS);
        addCollisionMask(Layers.BLOCKS);
        addCollisionMask(Layers.PIT);
        addCollisionMask(Layers.PLAYER_PROJECTILES);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        int x = this.x,
            y = this.y,
            w = this.w,
            h = this.h,
            alpha = 255;
        if (falling) {
            float progress = 1f - (float) fallTimer / Pit.DEPTH;
            w = (int) (w * progress);
            h = (int) (h * progress);
            x += (this.w - w) / 2;
            y += (this.h - h) / 2;
            alpha = (int) (255 * progress);
        }
        g.setColor(new Color(110, 50, 0, alpha));
        g.fillRect(x, y, w, h);
    }

    @Override
    public void tick() {
        if (falling) {
            fallTimer++;
            System.out.println(fallTimer);
            if (fallTimer > Pit.DEPTH) {
                destroy();
            }
            return;
        }
        super.tick();
        friction(0.99f);
        int particleCount = Math.max(0, (int) (new Vector(getVelX(), getVelY()).length() / 7));
        for (int i = 0; i < particleCount; i++) {
            int x = (int) (Math.random() * w) + this.x;
            int y = this.y + h;
            Particle p = new SlideParticle(x, y);
            Game.getInstance().getWorld().addParticle(p);
        }
    }

    public void startFall() {
        falling = true;
        // Remove collisions
        removeCollisionLayer(Layers.BLOCKS);
        removeCollisionMask(Layers.BLOCKS);
        removeCollisionMask(Layers.PIT);
        removeCollisionMask(Layers.PLAYER_PROJECTILES);
        // Stop moving
        setVelX(0);
        setVelY(0);
        // Start falling
        fallTimer = 0;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }

    @Override
    public void collision(GameObject other) {
        super.collision(other);
        if (other instanceof Player.Punch) {
            Player p = Game.getInstance().getPlayer();
            Vector force = new Vector(x - p.getX(), y - p.getY())
                            .normalize()
                            .mul(5f);
            setFVelX((float) force.x);
            setFVelY((float) force.y);
        }
        if (other.getId() == ID.Pit && other.getBounds().contains(getBounds())) {
            startFall();
        }
    }
}
