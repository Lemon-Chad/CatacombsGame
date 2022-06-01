package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuUI extends UIComponent {
    private static final String TITLE = "CATACOMBS";
    private static final int TYPE_TIME = 15;
    private static final float TYPE_IMPACT = 1.5f;
    private static final int FLASH_DURATION = 45;
    private int life;

    public MenuUI() {
        super(0, 0, ID.UI);
        Game.onKeyPressed(KeyEvent.VK_ENTER, event -> Game.getInstance().generationTest());
    }

    @Override
    public void tick() {
        life++;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);

        Font font = Game.getFont("/fonts/forward.TTF", Font.BOLD, 30);
        g.setFont(font);
        if (life > 0 && life % TYPE_TIME == 0 && life / TYPE_TIME <= TITLE.length()) {
            Game.getInstance().getCamera().setShake(TYPE_IMPACT);
        }
        String title = TITLE.substring(0, Math.min(TITLE.length(), life / TYPE_TIME));
        int width = g.getFontMetrics().stringWidth(title);
        int height = g.getFontMetrics().getHeight() - 2 * g.getFontMetrics().getAscent();
        int x = Game.getInstance().getWidth() / 2 - width / 2;
        int y = Game.getInstance().getHeight() / 2 - height / 2;
        g.setColor(new Color(0, 0, 0, 127));
        g.drawString(title, x + 4, y + 4);
        if (life / TYPE_TIME >= TITLE.length()) {
            int alpha = (int) (255 * (1 + ((float) TITLE.length() * TYPE_TIME - life ) / FLASH_DURATION));
            if (alpha > 0) {
                g.setColor(new Color(255, 255, 255, alpha));
                g.fillRect(0, 0, Game.getInstance().getWidth(), Game.getInstance().getHeight());
            }
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        g.drawString(title, x, y);

        if (life / TYPE_TIME >= TITLE.length()) {
            g.setFont(Game.getFont("/fonts/forward.TTF", Font.BOLD, 20));
            String subtitle = "Press ENTER to start";
            if (life % 50 <= 25)
                text(g, subtitle, Game.getInstance().getHeight() / 2 + 256);
        }
    }

    private void text(Graphics g, String text, int yCenter) {
        int width = g.getFontMetrics().stringWidth(text);
        int height = g.getFontMetrics().getHeight() - 2 * g.getFontMetrics().getAscent();
        int x = Game.getInstance().getWidth() / 2 - width / 2;
        int y = yCenter + height / 2;
        g.setColor(new Color(0, 0, 0, 127));
        g.drawString(text, x + 4, y + 4);
        g.setColor(Color.WHITE);
        g.drawString(text, x, y);
    }
}
