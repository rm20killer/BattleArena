package org.battleplugins.arena.resolver;

import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

class ResolverImpl implements Resolver {
    private final Map<ResolverKey<?>, ResolverProvider<?>> results;

    ResolverImpl(Map<ResolverKey<?>, ResolverProvider<?>> results) {
        this.results = new HashMap<>(results);
    }

    @Override
    public String resolveToString(String string) {
        for (Map.Entry<ResolverKey<?>, ResolverProvider<?>> entry : this.results.entrySet()) {
            String key = "%" + entry.getKey().getName().replace("-", "_") + "%";
            string = string.replace(key, entry.getValue().toString(this));
        }

        return string;
    }

    @Override
    public Component resolveToComponent(Component component) {
        for (Map.Entry<ResolverKey<?>, ResolverProvider<?>> entry : this.results.entrySet()) {
            String key = "%" + entry.getKey().getName().replace("-", "_") + "%";
            component = component.replaceText(builder -> builder.matchLiteral(key).replacement(entry.getValue().toComponent(this)));
        }

        return component;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(ResolverKey<T> key) {
        ResolverProvider<?> provider = this.results.get(key);
        if (provider == null) {
            throw new IllegalArgumentException("No provider defined for key " + key);
        }

        return (T) provider.resolve(this);
    }

    @Override
    public boolean has(ResolverKey<?> key) {
        return this.results.containsKey(key);
    }

    @Override
    public void mergeInto(Builder builder) {
        ((BuilderImpl) builder).results.putAll(this.results);
    }

    @Override
    public Builder toBuilder() {
        BuilderImpl builder = new BuilderImpl();
        builder.results.putAll(this.results);
        return builder;
    }

    static class BuilderImpl implements Resolver.Builder {
        private final Map<ResolverKey<?>, ResolverProvider<?>> results = new HashMap<>();

        @Override
        public <T> Builder define(ResolverKey<T> key, ResolverProvider<T> provider) {
            this.results.put(key, provider);
            return this;
        }

        @Override
        public Resolver build() {
            return new ResolverImpl(this.results);
        }
    }
}
