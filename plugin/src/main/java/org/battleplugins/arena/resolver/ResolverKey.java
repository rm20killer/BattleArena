package org.battleplugins.arena.resolver;

import org.battleplugins.arena.util.TypeToken;

public final class ResolverKey<T> {
    private final String name;
    private final TypeToken<T> type;

    ResolverKey(String name, Class<T> type) {
        this.name = name;
        this.type = TypeToken.of(type);
    }

    ResolverKey(String name, TypeToken<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public TypeToken<T> getType() {
        return this.type;
    }

    public static <T> ResolverKey<T> create(String name, Class<T> type) {
        return new ResolverKey<>(name, type);
    }

    public static <T> ResolverKey<T> create(String name, TypeToken<T> type) {
        return new ResolverKey<>(name, type);
    }
}
