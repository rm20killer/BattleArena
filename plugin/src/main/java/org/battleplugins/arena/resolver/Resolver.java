package org.battleplugins.arena.resolver;

import net.kyori.adventure.text.Component;

public interface Resolver {

    String resolveToString(String string);

    Component resolveToComponent(Component component);

    <T> T resolve(ResolverKey<T> key);

    boolean has(ResolverKey<?> key);

    void mergeInto(Builder builder);

    Builder toBuilder();

    static Builder builder() {
        return new ResolverImpl.BuilderImpl();
    }

    interface Builder {

        <T> Builder define(ResolverKey<T> key, ResolverProvider<T> provider);

        Resolver build();
    }
}
