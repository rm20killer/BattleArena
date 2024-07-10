package org.battleplugins.arena.resolver;

public interface Resolver {

    String resolveToString(String string);

    <T> T resolve(ResolverKey<T> key);

    boolean has(ResolverKey<?> key);

    Builder toBuilder();

    static Builder builder() {
        return new ResolverImpl.BuilderImpl();
    }

    interface Builder {

        <T> Builder define(ResolverKey<T> key, ResolverProvider<T> provider);

        Resolver build();
    }
}
