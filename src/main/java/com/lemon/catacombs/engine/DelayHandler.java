package com.lemon.catacombs.engine;

import java.util.HashSet;
import java.util.Set;

public class DelayHandler {
    private final ConcurrentSet<Event> events = new ConcurrentSet<>();

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
        for (Event event : events) {
            if (event.tick(delta)) {
                events.delete(event);
            }
        }
        events.commit();
    }
}
