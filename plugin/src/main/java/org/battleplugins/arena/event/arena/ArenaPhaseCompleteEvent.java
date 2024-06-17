package org.battleplugins.arena.event.arena;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.event.ArenaEvent;
import org.battleplugins.arena.event.EventTrigger;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an {@link Arena} completes a {@link CompetitionPhase}.
 */
@EventTrigger("on-complete")
public class ArenaPhaseCompleteEvent extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;
    private final CompetitionPhase<?> phase;

    public ArenaPhaseCompleteEvent(Arena arena, Competition<?> competition, CompetitionPhase<?> phase) {
        this.arena = arena;
        this.competition = competition;
        this.phase = phase;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public Competition<?> getCompetition() {
        return this.competition;
    }

    /**
     * Gets the phase that was completed.
     *
     * @return the phase that was completed
     */
    public CompetitionPhase<?> getPhase() {
        return this.phase;
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
