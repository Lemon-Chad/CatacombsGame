package com.lemon.catacombs.engine.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.*;

public class MouseInput extends MouseAdapter {
    private final Map<MouseEvents, Set<EventHandler>> eventHandlers = new HashMap<>();
    private final List<Event> events = new LinkedList<>();

    public interface EventHandler {
        void onEvent(MouseEvent e);
    }

    private static class Event {
        private final MouseEvents event;
        private final MouseEvent mouseEvent;

        public Event(MouseEvents event, MouseEvent mouseEvent) {
            this.event = event;
            this.mouseEvent = mouseEvent;
        }

        public MouseEvents getEvent() {
            return event;
        }

        public MouseEvent getMouseEvent() {
            return mouseEvent;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onEvent(MouseEvents.MouseClicked, e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        onEvent(MouseEvents.MousePressed, e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        onEvent(MouseEvents.MouseReleased, e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        onEvent(MouseEvents.MouseEntered, e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        onEvent(MouseEvents.MouseExited, e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        onEvent(MouseEvents.MouseDragged, e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        onEvent(MouseEvents.MouseMoved, e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        onEvent(MouseEvents.MouseWheelMoved, e);
    }

    public void addEventHandler(MouseEvents event, EventHandler handler) {
        eventHandlers.computeIfAbsent(event, k -> new HashSet<>()).add(handler);
    }

    public void onEvent(MouseEvents type, MouseEvent e) {
        events.add(new Event(type, e));
    }

    public void onTickEvent(MouseEvents type, MouseEvent e) {
        Set<EventHandler> handlers = eventHandlers.computeIfAbsent(type, k -> new HashSet<>());
        for (EventHandler handler : handlers) {
            handler.onEvent(e);
        }
    }

    public void tick() {
        for (Event e : events) {
            onTickEvent(e.getEvent(), e.getMouseEvent());
        }
        events.clear();
    }

    public void clear() {
        eventHandlers.clear();
    }
}
