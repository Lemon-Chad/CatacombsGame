package com.lemon.catacombs.engine;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DelayHandler {
    private final Set<Event> events = new HashSet<>();

    private static class Event {
        private final Runnable runnable;
        private long delay;

        public Event(long delay, Runnable runnable) {
            this.delay = delay;
            this.runnable = runnable;
        }

        public boolean tick(long delta) {
            delay -= delta;
            if (delay <= 0) {
                runnable.run();
            }
            return delay <= 0;
        }
    }

    public void add(long delay, Runnable runnable) {
        events.add(new Event(delay, runnable));
    }

    public void tick(long delta) {
        events.removeAll(new HashSet<>(events).stream().filter(event -> event.tick(delta)).collect(Collectors.toSet()));
    }
}
