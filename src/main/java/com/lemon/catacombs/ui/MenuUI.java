package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.Game;
import com.lemon.catacombs.engine.render.UIComponent;
import com.lemon.catacombs.objects.ID;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuUI extends UIComponent {
    public MenuUI() {
        super(0, 0, ID.UI);
        Game.onKeyPressed(KeyEvent.VK_ENTER, event -> Game.getInstance().endlessMode());
    }

    @Override
    public void tick() {}

    @Override
    public void render(Graphics g) {
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Catacombs Combat Testing", 100, 100);

        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press 'Enter' to start", 100, 200);
    }
}
