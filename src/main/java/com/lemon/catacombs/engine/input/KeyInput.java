package com.lemon.catacombs.engine.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyInput extends KeyAdapter {
    private final Map<Integer, Set<EventHandler>> keyPressEvents = new HashMap<>();
    private final Map<Integer, Set<EventHandler>> keyReleaseEvents = new HashMap<>();

    public interface EventHandler {
         void handle(KeyEvent e);
    }

    public void keyPressed(KeyEvent e) {
        if (keyPressEvents.containsKey(e.getKeyCode())) {
            keyPressEvents.get(e.getKeyCode()).forEach(handler -> handler.handle(e));
        }
    }

    public void keyReleased(KeyEvent e) {
        if (keyReleaseEvents.containsKey(e.getKeyCode())) {
            keyReleaseEvents.get(e.getKeyCode()).forEach(handler -> handler.handle(e));
        }
    }

    public void onKeyPressed(int key, EventHandler handler) {
        keyPressEvents.computeIfAbsent(key, k -> new HashSet<>()).add(handler);
    }

    public void onKeyReleased(int key, EventHandler handler) {
        keyReleaseEvents.computeIfAbsent(key, k -> new HashSet<>()).add(handler);
    }

    public void clear() {
        keyPressEvents.clear();
        keyReleaseEvents.clear();
    }
}
