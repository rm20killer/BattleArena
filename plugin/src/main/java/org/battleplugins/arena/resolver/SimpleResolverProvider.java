package org.battleplugins.arena.resolver;

import net.kyori.adventure.text.Component;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleResolverProvider<T> implements ResolverProvider<T> {
    private final T value;
    private final Function<T, String> toString;
    private final Function<T, Component> toComponent;

    protected SimpleResolverProvider(T value, Supplier<String> toString) {
        this(value, t -> toString.get());
    }

    protected SimpleResolverProvider(T value, Function<T, String> toString) {
        this(value, toString, t -> Component.text(toString.apply(t)));
    }

    protected SimpleResolverProvider(T value, Function<T, String> toString, Function<T, Component> toComponent) {
        this.value = value;
        this.toString = toString;
        this.toComponent = toComponent;
    }

    @Override
    public T resolve(Resolver resolver) {
        return this.value;
    }

    @Override
    public String toString(Resolver resolver) {
        return this.toString.apply(this.value);
    }

    @Override
    public Component toComponent(Resolver resolver) {
        return this.toComponent.apply(this.value);
    }
}
