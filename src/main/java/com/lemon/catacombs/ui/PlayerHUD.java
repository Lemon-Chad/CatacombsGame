package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerHUD extends UIComponent {
    private int mpsRefresh = 8;
    private final Dimension healthBarSize;
    private final Dimension staminaBarSize;
    private final int inventoryBoxSize;

    public PlayerHUD(int healthBarWidth, int healthBarHeight, int staminaBarWidth, int staminaBarHeight, int inventoryBoxSize) {
        super(0, 0, ID.UI);
        healthBarSize = new Dimension(healthBarWidth, healthBarHeight);
        staminaBarSize = new Dimension(staminaBarWidth, staminaBarHeight);
        this.inventoryBoxSize = inventoryBoxSize;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics g) {
        if (Game.getInstance().getPlayer() == null) {
            return;
        }
        renderHealthBar(g);
        renderStaminaBar(g);
        renderInventory(g);
        renderStats(g);
    }

    private void renderStats(Graphics g) {
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        int width = g.getFontMetrics().stringWidth(shorten(Stats.getStats().getKills()) + " kills");
        g.drawString(shorten(Stats.getStats().getKills()) + " kills", Game.getInstance().getWidth() - width - 10, 30);
        width = g.getFontMetrics().stringWidth(shorten(Stats.getStats().getDamage()) + " damage");
        g.drawString(shorten(Stats.getStats().getDamage()) + " damage", Game.getInstance().getWidth() - width - 10, 60);
        width = g.getFontMetrics().stringWidth(shorten(Stats.getStats().getMetersPerSecond()) + " m/s");
        g.drawString(shorten(Stats.getStats().getMetersPerSecond()) + " m/s", Game.getInstance().getWidth() - width - 10, 90);
    }

    private String shorten(double number) {
        if (number < 1000) {
            return Math.round(number * 100f) / 100f + "";
        } else if (number < 1000000) {
            return Math.round(number / 10f) / 100f + "k";
        } else if (number < 1000000000) {
            return Math.round(number / 10_000f) / 100f + "m";
        }
        return Math.round(number / 10_000_000f) / 100f + "b";
    }

    private String shorten(int number) {
        return number < 1000 ? number + "" : shorten((double) number);
    }

    private void renderInventory(Graphics g) {
        int x = 20;
        int y = 20;
        int slotSize = (inventoryBoxSize * 4) / 5;
        int slotPadding = (inventoryBoxSize - slotSize) / 2;
        int currentWeapon = Game.getInstance().getPlayer().getSelected();
        for (int i = 0; i < 3; i++) {
            g.setColor(i == currentWeapon ? new Color(0, 0, 0, 128) : new Color(0, 0, 0, 64));
            g.fillRect(x, y, inventoryBoxSize, inventoryBoxSize);

            Weapon w = Game.getInstance().getPlayer().getWeapon(i);
            if (w != null) {
                BufferedImage image = w.getSprite().getImage();
                float scale = (float) slotSize / Math.max(image.getWidth(), image.getHeight());
                int width = (int) (image.getWidth() * scale);
                int height = (int) (image.getHeight() * scale);
                int widthOffset = width < height ? (height - width) / 2 : 0;
                int heightOffset = height < width ? (width - height) / 2 : 0;
                // Tilt it because it's cool
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.rotate(Math.toRadians(-22), x + slotPadding + width / 2f, y + slotPadding + height / 2f);
                g2d.drawImage(image, x + slotPadding + widthOffset, y + slotPadding + heightOffset, width, height, null);
                g2d.dispose();

                // Draw the ammo/durability
                if (w.isMelee()) {
                    double durability = w.getDurability();
                    double hue = durability / 3;
                    Color color = Color.getHSBColor((float) hue, 1, 1);
                    int barHeight = (int) (slotSize * durability);
                    int barX = x + slotPadding + slotSize - 8;
                    int barY = y + slotPadding + (slotSize - height);
                    g.setColor(color);
                    g.fillRect(barX, barY, 8, barHeight);
                } else {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 15));
                    int fontWidth = g.getFontMetrics().stringWidth(w.getAmmo() + "");
                    g.drawString(w.getAmmo() + "", x + slotSize - fontWidth + slotPadding,
                            y + slotPadding + slotSize - 5);
                }
            }

            x += inventoryBoxSize + 10;
        }
    }

    private void renderHealthBar(Graphics g) {
        int x = 10;
        int y = Game.getInstance().getHeight() - healthBarSize.height - staminaBarSize.height - 20;
        g.setColor(Color.BLACK);
        g.fillRect(x, y, healthBarSize.width, healthBarSize.height);
        g.setColor(Color.GREEN);
        g.fillRect(x, y, healthBarSize.width * Game.getInstance().getPlayer().getHealth() / Game.getInstance().getPlayer().getMaxHealth(), healthBarSize.height);
    }

    private void renderStaminaBar(Graphics g) {
        int w = staminaBarSize.width * Game.getInstance().getPlayer().getStamina() / Game.getInstance().getPlayer().getMaxStamina();
        int x = 10;
        int y = Game.getInstance().getHeight() - staminaBarSize.height - 10;
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, staminaBarSize.height);
    }
}
