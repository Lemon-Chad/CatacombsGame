package com.lemon.catacombs.engine.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class KeyInput extends KeyAdapter {
    private final Map<Integer, Set<EventHandler>> keyPressEvents = new HashMap<>();
    private final Map<Integer, Set<EventHandler>> keyReleaseEvents = new HashMap<>();

    private final List<Event> keyEvents = new LinkedList<>();

    public interface EventHandler {
         void handle(KeyEvent e);
    }

    private static class Event {
        private final boolean pressed;
        private final KeyEvent e;

        public Event(boolean pressed, KeyEvent e) {
            this.pressed = pressed;
            this.e = e;
        }

        public boolean isPressed() {
            return pressed;
        }

        public KeyEvent getEvent() {
            return e;
        }
    }

    public void keyPressEvent(KeyEvent e) {
        if (keyPressEvents.containsKey(e.getKeyCode())) {
            keyPressEvents.get(e.getKeyCode()).forEach(handler -> handler.handle(e));
        }
    }

    public void keyReleaseEvent(KeyEvent e) {
        if (keyReleaseEvents.containsKey(e.getKeyCode())) {
            keyReleaseEvents.get(e.getKeyCode()).forEach(handler -> handler.handle(e));
        }
    }

    public void keyPressed(KeyEvent e) {
        keyEvents.add(new Event(true, e));
    }

    public void keyReleased(KeyEvent e) {
        keyEvents.add(new Event(false, e));
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

    public void tick() {
        for (Event e : keyEvents) {
            if (e.isPressed()) {
                keyPressEvent(e.getEvent());
            } else {
                keyReleaseEvent(e.getEvent());
            }
        }
        keyEvents.clear();
    }
}
