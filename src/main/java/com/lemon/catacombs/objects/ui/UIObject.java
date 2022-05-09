package com.lemon.catacombs.objects.ui;

import com.lemon.catacombs.engine.physics.GameObject;
import com.lemon.catacombs.objects.ID;

import java.awt.*;

abstract public class UIObject extends GameObject {
    public UIObject(int x, int y) {
        super(x, y, ID.UI);
    }

    public UIObject(int x, int y, int id) {
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
