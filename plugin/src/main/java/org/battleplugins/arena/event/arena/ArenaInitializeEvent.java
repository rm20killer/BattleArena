package org.battleplugins.arena.event.arena;

import org.battleplugins.arena.Arena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an {@link Arena} is initialized.
 */
public class ArenaInitializeEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;

    public ArenaInitializeEvent(Arena arena) {
        this.arena = arena;
    }

    /**
     * Gets the {@link Arena} this event is occurring in.
     *
     * @return the arena this event is occurring in
     */
    public Arena getArena() {
        return this.arena;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
