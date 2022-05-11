package com.lemon.catacombs.objects.endless;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

public class CheckeredBackground extends GameObject {
    private static final int tileSize = 64;
    private static final Color oddColor = new Color(0x464641);
    private static final Color evenColor = new Color(0x505050);

    public CheckeredBackground() {
        super(0, 0, ID.UI);
    }

    @Override
    public int getYSort() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void render(Graphics g) {
        int cx = (int) Game.getInstance().getCamera().getX();
        int cy = (int) Game.getInstance().getCamera().getY();

        int ox = cx % (tileSize * 2);
        int oy = cy % (tileSize * 2);
        for (int i = -3; i < Game.getInstance().getWidth() / tileSize + 3; i++) {
            for (int j = -3; j < Game.getInstance().getHeight() / tileSize + 3; j++) {
                g.setColor((i + j) % 2 == 0 ? evenColor : oddColor);
                g.fillRect(i * tileSize - ox + cx, j * tileSize - oy + cy, tileSize, tileSize);
            }
        }
    }

    @Override
    public Rectangle getBounds() {
        return null;
    }

    @Override
    public void collision(GameObject other) {

    }
}
