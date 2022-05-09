package com.lemon.catacombs.engine.render;

import com.lemon.catacombs.engine.Game;

import java.awt.*;

abstract public class UIComponent implements YSortable {
    private int x, y;
    private final int id;

    public UIComponent(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public abstract void tick();
    public abstract void render(Graphics g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getYSort() {
        return y;
    }

    public void destroy() {
        Game.getInstance().getWorld().removeObject(this);
    }
}
