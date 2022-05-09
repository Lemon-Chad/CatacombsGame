package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.items.Weapon;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

public class PlayerHUD extends UIComponent {
    public static int kills = 0;
    public static int damage = 0;
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
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        int width = g.getFontMetrics().stringWidth(kills + " kills");
        g.drawString(kills + " kills", Game.getInstance().getWidth() - width - 10, 30);
        width = g.getFontMetrics().stringWidth(damage + " damage");
        g.drawString(damage + " damage", Game.getInstance().getWidth() - width - 10, 60);

        if (Game.getInstance().getPlayer() == null) {
            return;
        }
        renderHealthBar(g);
        renderStaminaBar(g);
        renderInventory(g);
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

                // Draw the ammo
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 15));
                int width = g.getFontMetrics().stringWidth(w.getAmmo() + "");
                g.drawString(w.getAmmo() + "", x + slotSize - width + slotPadding,
                        y + slotPadding + slotSize - 5);
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
