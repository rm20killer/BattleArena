package org.battleplugins.arena.event.action;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.resolver.Resolvable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an action that occurs in an {@link Arena}.
 */
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

    /**
     * Gets the parameter with the given key.
     *
     * @param key the key to get the parameter from
     * @return the parameter with the given key
     */
    @Nullable
    public String get(String key) {
        return this.params.get(key);
    }

    /**
     * Gets the parameter with the given key or the default value if the key does not exist.
     *
     * @param key the key to get the parameter from
     * @param defaultValue the default value to return if the key does not exist
     * @return the parameter with the given key or the default value if the key does not exist
     */
    public String getOrDefault(String key, String defaultValue) {
        return this.params.getOrDefault(key, defaultValue);
    }

    /**
     * Called before the action is processed.
     * <p>
     * This is run globally any time an action occurs, meaning this
     * method is not bound to any specific player.
     *
     * @param arena the arena the action is occurring in
     * @param competition the competition the action is occurring in
     * @param resolvable the resolvable to call the action for
     */
    public void preProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
    }

    /**
     * Called after the action is processed.
     * <p>
     * This is run globally any time an action occurs, meaning this
     * method is not bound to any specific player.
     *
     * @param arena the arena the action is occurring in
     * @param competition the competition the action is occurring in
     * @param resolvable the resolvable to call the action for
     */
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
    }

    /**
     * Calls the action for the given {@link ArenaPlayer}.
     *
     * @param arenaPlayer the player to call the action for
     * @param resolvable the resolvable to call the action for
     */
    public abstract void call(ArenaPlayer arenaPlayer, Resolvable resolvable);
}
