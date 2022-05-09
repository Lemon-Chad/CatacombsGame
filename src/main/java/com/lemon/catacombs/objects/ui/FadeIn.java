package com.lemon.catacombs.objects.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

public class FadeIn extends UIComponent {
    private final int duration;
    private int life;

    public FadeIn(int duration) {
        super(0, 0, ID.UI);
        this.duration = duration;
        life = duration + 30;
    }

    @Override
    public void tick() {
        life--;
        if (life <= 0) {
            destroy();
        }
    }

    @Override
    public int getYSort() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(0, 0, 0, (int) (Math.min(1, (float) life / duration) * 255)));
        g.fillRect(0, 0, Game.getInstance().getWidth(), Game.getInstance().getHeight());
    }
}
