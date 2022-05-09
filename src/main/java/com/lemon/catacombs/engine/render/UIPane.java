package com.lemon.catacombs.engine.render;

import java.awt.*;
import java.util.*;

public class UIPane {
    private final Set<UIComponent> components;

    public UIPane() {
        this.components = new HashSet<>();
    }

    public void addComponent(UIComponent component) {
        this.components.add(component);
    }

    public void removeComponent(UIComponent component) {
        this.components.remove(component);
    }

    public void render(Graphics g) {
        for (UIComponent object : YSortable.sort(this.components)) {
            object.render(g);
        }
    }

}
