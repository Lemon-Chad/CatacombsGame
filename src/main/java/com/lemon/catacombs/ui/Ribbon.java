package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

public abstract class Ribbon extends UIComponent {
    private int animationTick;
    private boolean playing;
    private final int height;

    public Ribbon(int y, int h) {
        super(0, y, ID.UI);
        height = h;
        animationTick = 0;
    }

    public abstract boolean isTriggered();
    public abstract String getText();
    public abstract Color getColor();
    public abstract int getDuration();
    public abstract int getTransition();
    public abstract Font getFont();
    public float interpolate(float x) {
        return x;
    }
    public int getSlant() {
        return 48;
    }

    @Override
    public void tick() {
        if (playing) {
            animationTick++;
            if (animationTick >= getDuration() + getTransition() * 2) {
                animationTick = 0;
                playing = false;
            }
        } else {
            playing = isTriggered();
        }
    }

    @Override
    public void render(Graphics g) {
        if (!playing) return;
        int x = 0;
        int w = Game.getInstance().getWidth() + getSlant();
        int h = height;
        if (animationTick < getTransition()) {
            x = (int) (interpolate( (float) animationTick / getTransition()) * w - w);
        } else if (animationTick > getDuration() + getTransition() && animationTick < getDuration() + 2 * getTransition()) {
            int t = animationTick - getTransition() - getDuration();
            x = (int) (interpolate((float) t / getTransition()) * w);
        }
        g.setColor(getColor());
        g.fillPolygon(new int[] {x, x + w, x + w - getSlant(), x - getSlant()}, new int[] {0, 0, h, h}, 4);

        g.setColor(Color.WHITE);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        // Center text
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        g.drawString(getText(), x + (w - textWidth) / 2, (h - textHeight) / 2 + fm.getAscent());

    }
}
