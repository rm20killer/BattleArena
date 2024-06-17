package org.battleplugins.arena.options;

import java.util.Map;

public class ArenaOption {
    private final Map<String, String> params;

    public ArenaOption(Map<String, String> params, String... requiredKeys) {
        this.params = params;
        for (String key : requiredKeys) {
            if (!params.containsKey(key)) {
                throw new IllegalArgumentException("Missing required key: " + key);
            }
        }
    }

    public String get(String key) {
        return this.params.get(key);
    }
}
