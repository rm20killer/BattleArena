package org.battleplugins.arena.stat;

public class SimpleArenaStat<T> implements ArenaStat<T> {
    private final String key;
    private final String name;
    private final T defaultValue;
    private final Class<T> type;

    public SimpleArenaStat(String key, String name, T defaultValue, Class<T> type) {
        this.key = key;
        this.name = name;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }
}
