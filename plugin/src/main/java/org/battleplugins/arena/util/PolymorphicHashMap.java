package org.battleplugins.arena.util;

import java.util.HashMap;
import java.util.Optional;

public class PolymorphicHashMap<K extends Class<?>, V> extends HashMap<K, V> {

    @Override
    public boolean containsKey(Object key) {
        return this.findEntry((K) key).isPresent();
    }

    @Override
    public V get(Object key) {
        Optional<Entry<K, V>> entry = this.findEntry((K) key);
        return entry.map(Entry::getValue).orElse(null);
    }

    private Optional<Entry<K, V>> findEntry(K key) {
        return this.entrySet()
                .stream()
                .filter(e -> e.getKey().isAssignableFrom(key))
                .findFirst();
    }
}