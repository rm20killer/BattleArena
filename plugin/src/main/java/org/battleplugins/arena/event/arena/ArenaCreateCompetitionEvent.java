package org.battleplugins.arena.event.arena;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.ArenaEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a competition is created in an {@link Arena}.
 */
public class ArenaCreateCompetitionEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;

    public ArenaCreateCompetitionEvent(Arena arena, Competition<?> competition) {
        this.arena = arena;
        this.competition = competition;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public Competition<?> getCompetition() {
        return this.competition;
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
