package com.lemon.catacombs.engine.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MouseInput extends MouseAdapter {
    private final Map<MouseEvents, Set<EventHandler>> eventHandlers = new HashMap<>();

    public interface EventHandler {
        void onEvent(MouseEvent e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(Math.random());
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
        Set<EventHandler> handlers = eventHandlers.computeIfAbsent(type, k -> new HashSet<>());
        for (EventHandler handler : handlers) {
            handler.onEvent(e);
        }
    }
}
