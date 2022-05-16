package com.lemon.catacombs.objects.entities;

import com.lemon.catacombs.Utils;
import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.engine.render.Animation;
import com.lemon.catacombs.engine.render.Sprite;
import com.lemon.catacombs.engine.render.Spriteable;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.Layers;
import com.lemon.catacombs.objects.particles.Particle;
import com.lemon.catacombs.objects.particles.PickupParticle;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Collectable extends GameObject {
    private final Spriteable sprite;
    private final int width;
    private final int height;
    private final float initialScale;
    private float scale;
    private int life;
    private final int maxLife;
    private final CollectableListener listener;
    private final boolean onInteract;

    public Collectable(Spriteable sprite, int x, int y, float scale, int life, boolean onInteract, CollectableListener listener) {
        super(x, y, ID.Collectable);
        this.sprite = sprite;
        this.width = sprite.getSprite().getImage().getWidth();
        this.height = sprite.getSprite().getImage().getHeight();
        this.initialScale = scale;
        this.scale = scale;
        this.life = life;
        this.maxLife = life;
        this.listener = listener;
        this.onInteract = onInteract;
        addCollisionLayer(Layers.ITEM);
        addCollisionMask(Layers.PLAYER);
        addCollisionMask(Layers.BLOCKS);
    }

    public interface CollectableListener {
        void onCollect(Player player, Collectable collectable);
    }

    @Override
    public void tick() {
        super.tick();
        scale = initialScale + 0.75f * (1 + (float) Math.sin(life * 0.075));
        life--;
        if (life <= 0) {
            destroy();
        }
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        float alpha = Math.min(1, 2 * (float) life / (float) maxLife);
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

        BufferedImage newImage = new BufferedImage((int) (width * scale),
                (int) (height * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = newImage.createGraphics();
        g2d.setComposite(ac);
        g2d.drawImage(sprite.getSprite().getImage(), 0, 0, (int) (width * scale), (int) (height * scale), null);
        g2d.dispose();

        g.drawImage(newImage, (int) (x + (1 - scale) * width / 2), (int) (y + (1 - scale) * height / 2), null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle((int) (x + (1 - scale) * width / 2), (int) (y + (1 - scale) * height / 2),
                (int) (width * scale), (int) (height * scale));
    }

    @Override
    public void collision(GameObject other) {
        if (other.getId() == ID.Player) {
            Player player = (Player) other;
            if (onInteract && !player.isInteracting()) {
                return;
            }
            Game.playSound("/sounds/item.wav");
            listener.onCollect(player, this);
            for (int i = 0; i < Utils.range(5, 15); i++) {
                Particle particle = new PickupParticle(x, y);
                Game.getInstance().getWorld().addParticle(particle);
            }
        }
        destroy();
    }
}
