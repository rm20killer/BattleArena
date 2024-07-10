package org.battleplugins.arena.resolver;

import net.kyori.adventure.text.Component;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ResolverProvider<T> {

    T resolve(Resolver resolver);

    String toString(Resolver resolver);

    Component toComponent(Resolver resolver);

    static <T> ResolverProvider<T> simple(T value, Supplier<String> toString) {
        return new SimpleResolverProvider<>(value, toString);
    }

    static <T> ResolverProvider<T> simple(T value, Function<T, String> toString) {
        return new SimpleResolverProvider<>(value, toString);
    }

    static <T> ResolverProvider<T> simple(T value, Function<T, String> toString, Function<T, Component> toComponent) {
        return new SimpleResolverProvider<>(value, toString, toComponent);
    }
}
