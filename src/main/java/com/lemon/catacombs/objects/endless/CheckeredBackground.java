package com.lemon.catacombs.objects.endless;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Set;

public class CheckeredBackground extends GameObject {
    private static final Color oddColor = new Color(0x464641);
    private static final Color evenColor = new Color(0x505050);
    private final @Nullable Set<Point> points;
    private int tileSize = 64;

    public CheckeredBackground() {
        super(0, 0, ID.UI);
        points = null;
    }

    public CheckeredBackground(@NotNull Set<Point> points) {
        super(0, 0, ID.UI);
        this.points = points;
    }

    @Override
    public int getYSort() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
        if (points == null) {
            renderScrolling(g);
        } else {
            renderFixed(g);
        }
    }

    private void renderScrolling(Graphics g) {
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

    private void renderFixed(Graphics g) {
        assert points != null;
        for (Point point : points) {
            g.setColor((point.x / tileSize + point.y / tileSize) % 2 == 0 ? evenColor : oddColor);
            g.fillRect(point.x, point.y, tileSize, tileSize);
        }
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, 0, 0);
    }

    @Override
    public void collision(GameObject other) {

    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }
}
