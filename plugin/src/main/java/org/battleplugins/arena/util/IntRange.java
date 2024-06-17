package org.battleplugins.arena.util;

public class IntRange {
    private final int min;
    private final int max;

    public IntRange(int value) {
        this(value, value);
    }

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public static IntRange minInclusive(int min) {
        return new IntRange(min, Integer.MAX_VALUE);
    }

    public static IntRange maxInclusive(int max) {
        return new IntRange(0, max);
    }
}
