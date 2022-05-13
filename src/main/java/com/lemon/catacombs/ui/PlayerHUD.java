package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class PlayerHUD extends UIComponent {
    public static int kills = 0;
    public static int damage = 0;
    public static double metersPerSecond = 0;
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
        mpsRefresh--;
        if (mpsRefresh <= 0) {
            Player player = Game.getInstance().getPlayer();
            metersPerSecond = new Point((int) player.getVelX(), (int) player.getVelY()).distance(0, 0) / 5;
            mpsRefresh = 8;
        }
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        int width = g.getFontMetrics().stringWidth(shorten(kills) + " kills");
        g.drawString(shorten(kills) + " kills", Game.getInstance().getWidth() - width - 10, 30);
        width = g.getFontMetrics().stringWidth(shorten(damage) + " damage");
        g.drawString(shorten(damage) + " damage", Game.getInstance().getWidth() - width - 10, 60);
        width = g.getFontMetrics().stringWidth(shorten(metersPerSecond) + " m/s");
        g.drawString(shorten(metersPerSecond) + " m/s", Game.getInstance().getWidth() - width - 10, 90);
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
                // Tilt it because it's cool
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.rotate(Math.toRadians(-22), x + slotPadding + slotSize / 2f, y + slotPadding + slotSize / 2f);
                g2d.drawImage(w.getSprite().getImage(), x + slotPadding, y + slotPadding, slotSize, slotSize, null);
                g2d.dispose();

                // Draw the ammo/durability
                if (w.isMelee()) {
                    double durability = w.getDurability();
                    double hue = durability / 3;
                    Color color = Color.getHSBColor((float) hue, 1, 1);
                    int height = (int) (slotSize * durability);
                    int barX = x + slotPadding + slotSize - 8;
                    int barY = y + slotPadding + (slotSize - height);
                    g.setColor(color);
                    g.fillRect(barX, barY, 8, height);
                } else {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 15));
                    int width = g.getFontMetrics().stringWidth(w.getAmmo() + "");
                    g.drawString(w.getAmmo() + "", x + slotSize - width + slotPadding,
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
