package com.lemon.catacombs.engine;

import java.util.LinkedList;
import java.util.List;

public class LootTable<T> {
    public class Entry {
        public final T item;
        public final float weight;
        public float height;

        public Entry(T item, float weight, float height) {
            this.item = item;
            this.weight = weight;
            this.height = height;
        }
    }

    private final List<Entry> entries;
    private float top;

    public LootTable() {
        entries = new LinkedList<>();
    }

    public LootTable<T> add(T item, float weight) {
        top += weight;
        entries.add(new Entry(item, weight, top));
        return this;
    }

    public T getRandomItem() {
        float random = (float) Math.random() * top;
        for (Entry entry : entries) {
            if (random < entry.height) {
                return entry.item;
            }
        }
        return entries.get(entries.size() - 1).item;
    }

    @SafeVarargs
    public static <T> LootTable<T> EvenDistribution(T... items) {
        LootTable<T> table = new LootTable<>();
        float weight = 1.0f / items.length;
        for (T item : items) {
            table.add(item, weight);
        }
        return table;
    }
}
