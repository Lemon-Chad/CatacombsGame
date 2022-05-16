package com.lemon.catacombs.engine;

import java.util.*;

public class ConcurrentSet<T> extends AbstractCollection<T> implements Iterable<T>, Collection<T> {
    private final Set<T> set;
    private final Set<T> toRemove;
    private final Set<T> toAdd;

    private final Listener<T> onAdd;
    private final Listener<T> onRemove;

    public ConcurrentSet() {
        this(new HashSet<>());
    }

    public ConcurrentSet(Listener<T> onAdd, Listener<T> onRemove) {
        this(new HashSet<>(), onAdd, onRemove);
    }

    public ConcurrentSet(Set<T> set) {
        this(set, null, null);
    }

    public ConcurrentSet(Set<T> set, Listener<T> onAdd, Listener<T> onRemove) {
        this.set = set;
        toRemove = new HashSet<>();
        toAdd = new HashSet<>();

        this.onAdd = onAdd;
        this.onRemove = onRemove;
    }

    public interface Listener<U> {
        void event(U item);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public boolean add(T t) {
        toAdd.add(t);
        return set.contains(t) || toAdd.contains(t);
    }

    public void delete(T t) {
        toRemove.add(t);
    }

    public void commit() {
        set.removeAll(toRemove);
        set.addAll(toAdd);

        if (onRemove != null) toRemove.forEach(onRemove::event);
        if (onAdd != null) toAdd.forEach(onAdd::event);

        toRemove.clear();
        toAdd.clear();
    }
}
