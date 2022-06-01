package com.lemon.catacombs.objects.rooms;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.Vector;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.entities.Damageable;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.ui.Stats;

import java.awt.*;

public class Door extends Damageable {
    private final int w, h;
    private final boolean horizontal;
    private boolean open;
    private boolean openForward;
    private GameObject source;

    public Door(int x, int y, int w, int h, boolean horizontal) {
        super(x, y, ID.Door, new int[0], 150);
        this.w = w;
        this.h = h;
        this.horizontal = horizontal;
        open = false;
        openForward = false;

        addCollisionLayer(Layers.BLOCKS);
        addCollisionLayer(Layers.DOORS);
        addCollisionMask(Layers.PLAYER_PROJECTILES);

        setBleeds(false);
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        Color color = Color.getHSBColor(getHealth() / (3f * getMaxHealth()), 1f, 1f);
        g.setColor(color);
        Rectangle bounds = getBounds();
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public Rectangle getBounds() {
        if (open) {
            int x = this.x;
            int y = this.y;
            if (openForward) {
                if (horizontal) {
                    y += h - w;
                } else {
                    x += w - h;
                }
            }
            return new Rectangle(x, y, h, w);
        } else {
            return new Rectangle(x, y, w, h);
        }
    }

    @Override
    public int getYSort() {
        return y + 16;
    }

    @Override
    public void collision(GameObject other) {}

    @Override
    public boolean damage(int damage, GameObject source) {
        this.source = source;
        boolean ret = super.damage(damage, source);
        setFVelX(0);
        setFVelY(0);
        setInvulnerable(false);
        Game.playSound("/sounds/hit/hit" + (int) (1 + Math.random() * 2) + ".wav");
        Stats.getStats().addDamage(damage);
        return ret;
    }

    @Override
    protected void onDeath() {
        if (!open) {
            open = true;
        } else {
            return;
        }
        if (horizontal) {
            openForward = y < source.getY();
        } else {
            openForward = x < source.getX();
        }

        int fractureCount = (int) (Utils.range(3, 7));
        double force = 14;
        double spread = Utils.range(9, 11);

        int ox = 0, oy = 0;

        for (int i = 0; i < fractureCount; i++) {
            int x, y, w, h;
            int remaining = fractureCount - i;
            Vector f;
            if (horizontal) {
                x = this.x + ox;
                y = this.y;

                f = new Vector((x - source.getX()) / spread, y - source.getY());

                h = this.h;
                int W = this.w - ox;
                w = (int) (Utils.range(W / 2.0 / remaining, (double) W / remaining));
                ox += w;
                if (remaining == 1) {
                    w = W;
                }
            } else {
                x = this.x;
                y = this.y + oy;

                f = new Vector(x - source.getX(), (y - source.getY()) / spread);

                w = this.w;
                int H = this.h - oy;
                h = (int) (Utils.range(H / 2.0 / remaining, (double) H / remaining));
                oy += h;
                if (remaining == 1) {
                    h = H;
                }
            }

            Particle p = new DoorPiece(x, y, w, h);
            f = f
                    .normalize()
                    .mul(Utils.range(force / 2, force));
            p.setVelX((float) f.x);
            p.setVelY((float) f.y);
            Game.getInstance().getWorld().addParticle(p);
        }

        Game.playSound("/sounds/door/break" + Utils.intRange(1, 3) + ".wav");
        destroy();
    }

    private static class DoorPiece extends Particle {
        private final int w, h;

        public DoorPiece(int x, int y, int w, int h) {
            super(x, y, (float) Utils.range(0.3, 0.4), 20);
            this.w = w;
            this.h = h;
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(x, y, w, h);
        }

        @Override
        public void render(Graphics g) {
            g.setColor(new Color(255, 0, 0, getAlpha()));
            g.fillRect(x, y, w, h);
        }
    }
}
