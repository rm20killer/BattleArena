package org.battleplugins.arena.event;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an event that occurs in an {@link Arena}.
 */
public interface ArenaEvent extends Resolvable {

    /**
     * Gets the {@link Arena} this event is occurring in.
     *
     * @return the arena this event is occurring in
     */
    Arena getArena();

    /**
     * Gets the {@link Competition} this event is occurring in.
     *
     * @return the competition this event is occurring in
     */
    Competition<?> getCompetition();

    /**
     * Gets the {@link EventTrigger} for this event.
     *
     * @return the event trigger for this event
     */
    @Nullable
    default EventTrigger getEventTrigger() {
        if (this.getClass().isAnnotationPresent(EventTrigger.class)) {
            return this.getClass().getAnnotation(EventTrigger.class);
        }

        return null;
    }

    default Resolver resolve() {
        return Resolver.builder()
                .define(ResolverKeys.ARENA, ResolverProvider.simple(this.getArena(), Arena::getName))
                .define(ResolverKeys.COMPETITION, ResolverProvider.simple(this.getCompetition(), c -> c.getMap().getName()))
                .build();
    }
}
