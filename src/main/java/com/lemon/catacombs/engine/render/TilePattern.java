package com.lemon.catacombs.engine.render;

public class TilePattern {
    private final boolean[] pattern;

    public TilePattern(int key) {
        this.pattern = fromKey(key);
    }

    public boolean match(int key) {
        return match(fromKey(key));
    }

    public boolean match(boolean[] pattern) {
        for (int i = 0; i < pattern.length; i++) {
            /*
             * A B O
             * 1 0 0
             * 0 0 1
             * 0 1 1
             * 1 1 1
             *
             * !A || (A && B)
             * !(A && !(A && B))
             * !(A && !B)
             * !A || B
             */
            if (this.pattern[i] && !pattern[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean[] fromKey(int key) {
        boolean[] pattern = new boolean[8];
        for (int i = 0; i < 8; i++) {
            pattern[i] = (key & (1 << i)) != 0;
        }
        return pattern;
    }

    public int complexity() {
        int complexity = 0;
        for (boolean b : pattern) {
            complexity += b ? 1 : 0;
        }
        return complexity;
    }
}
