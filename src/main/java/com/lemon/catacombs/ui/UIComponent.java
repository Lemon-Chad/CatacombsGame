package com.lemon.catacombs.ui;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

abstract public class UIComponent extends GameObject {
    public UIComponent(int x, int y) {
        super(x, y, ID.UI);
    }

    public UIComponent(int x, int y, int id) {
        super(x, y, id);
    }

    @Override
    public int getYSort() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 0, 0);
    }

    @Override
    public void collision(GameObject other) {
        // Do nothing
    }
}
