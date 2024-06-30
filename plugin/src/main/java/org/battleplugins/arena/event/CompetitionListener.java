package org.battleplugins.arena.event;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionLike;

/**
 * A listener for competitions.
 * <p>
 * This functionality extends upon {@link ArenaListener} and
 * further isolates any event actions to the given {@link T competition}.
 *
 * @param <T> the type of competition
 */
public class CompetitionListener<T extends Competition<T>> implements ArenaListener, CompetitionLike<T> {
    private final T competition;
    
    public CompetitionListener(T competition) {
        this.competition = competition;
    }

    @Override
    public T getCompetition() {
        return competition;
    }
}
