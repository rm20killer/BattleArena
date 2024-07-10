package org.battleplugins.arena.event.player;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.ArenaEvent;
import org.battleplugins.arena.resolver.Resolver;

/**
 * Represents an event that occurs in an {@link Arena} to a
 * {@link ArenaPlayer}.
 */
public interface ArenaPlayerEvent extends ArenaEvent {

    @Override
    default Arena getArena() {
        return this.getArenaPlayer().getArena();
    }

    @Override
    default LiveCompetition<?> getCompetition() {
        return this.getArenaPlayer().getCompetition();
    }

    /**
     * Gets the {@link ArenaPlayer} this event is occurring to.
     *
     * @return the arena player this event is occurring to
     */
    ArenaPlayer getArenaPlayer();

    /**
     * Resolves the {@link ArenaPlayer} this event is occurring to
     * to a {@link Resolver} object.
     *
     * @return the resolved object
     */
    default Resolver resolve() {
        return this.getArenaPlayer().resolve();
    }
}
