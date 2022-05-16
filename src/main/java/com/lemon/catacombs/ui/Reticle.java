package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;
import com.lemon.catacombs.objects.entities.Player;

import java.awt.*;

public class Reticle extends UIComponent {
    private static final Dimension CROSSHAIR_SIZE = new Dimension(16, 4);
    private static final Dimension RETICLE_SIZE = new Dimension(8, 8);
    private static final int HITMARKER_SIZE = 32;

    private double radius = 3;
    private int iradius = (int) (8 * radius);

    public Reticle() {
        super(0, 0, ID.UI);
    }

    @Override
    public void tick() {
        Point mouse = Game.getInstance().getMousePosition();
        if (mouse == null) return;
        setX(mouse.x);
        setY(mouse.y);
        Player player = Game.getInstance().getPlayer();
        if (player == null || player.getEquipped() == null) {
            setRadius(0);
            return;
        }
        float mouseX = mouse.x + Game.getInstance().getCamera().getX();
        float mouseY = mouse.y + Game.getInstance().getCamera().getY();
        double angle = player.getEquipped().getBloom() + player.getEquipped().getRecoil();
        double distance = new Point((int) mouseX, (int) mouseY).distance(new Point(player.getX(), player.getY())) / 16;
        setRadius(distance * Math.sqrt(2 * (1 - Math.cos(angle))));
    }

    private void setRadius(double radius) {
        this.radius = radius;
        iradius = (int) (8 * radius);
    }

    @Override
    public void render(Graphics gg) {
        int x = getX() + RETICLE_SIZE.width / 2;
        int y = getY() + RETICLE_SIZE.height / 2;
        Graphics2D g = (Graphics2D) gg.create();
        g.setColor(Color.WHITE);
        g.fillOval(getX(), getY(), RETICLE_SIZE.width, RETICLE_SIZE.height);
        int alpha = (int) (255 * Math.max(0, Math.min(1, 7 - Math.abs(radius - 16) / 2)));
        if (alpha >= 0) {
            g.setColor(new Color(255, 255, 255, alpha));
            drawCross(g, iradius);
        }

        if (Stats.getStats().getSinceLastHit() > 0) {
            Color color = Stats.getStats().getSinceLastKill() > 0 ? Color.RED : Color.WHITE;
            alpha = (int) (255 * Math.min(1, Stats.getStats().getSinceLastHit() / 35f));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            g.rotate(Math.PI / 4, x, y);
            drawCross(g, HITMARKER_SIZE);
        }

        g.dispose();
    }

    private void drawCross(Graphics g, int r) {
        int x = getX() + RETICLE_SIZE.width / 2;
        int y = getY() + RETICLE_SIZE.height / 2;
        g.fillRect(x - r - CROSSHAIR_SIZE.width, y, CROSSHAIR_SIZE.width, CROSSHAIR_SIZE.height);
        g.fillRect(x + r, y, CROSSHAIR_SIZE.width, CROSSHAIR_SIZE.height);
        g.fillRect(x, y - r - CROSSHAIR_SIZE.width, CROSSHAIR_SIZE.height, CROSSHAIR_SIZE.width);
        g.fillRect(x, y + r, CROSSHAIR_SIZE.height, CROSSHAIR_SIZE.width);
    }
}
