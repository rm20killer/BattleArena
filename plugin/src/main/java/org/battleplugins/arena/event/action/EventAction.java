package org.battleplugins.arena.event.action;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;

import java.util.Map;

public abstract class EventAction {
    private final Map<String, String> params;

    public EventAction(Map<String, String> params, String... requiredKeys) {
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

    public String getOrDefault(String key, String defaultValue) {
        return this.params.getOrDefault(key, defaultValue);
    }

    public void preProcess(Arena arena, Competition<?> competition) {
    }

    public void postProcess(Arena arena, Competition<?> competition) {
    }

    public abstract void call(ArenaPlayer arenaPlayer);
}
