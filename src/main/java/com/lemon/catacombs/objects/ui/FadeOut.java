package com.lemon.catacombs.objects.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

public class FadeOut extends UIComponent {
    private final int duration;
    private final FadeOutListener listener;
    private int life;

    public FadeOut(int duration, FadeOutListener listener) {
        super(0, 0, ID.UI);
        this.duration = duration;
        this.listener = listener;
    }

    public interface FadeOutListener {
        void onFadeOut();
    }

    @Override
    public void tick() {
        life++;
        if (life >= duration + 30) {
            listener.onFadeOut();
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
