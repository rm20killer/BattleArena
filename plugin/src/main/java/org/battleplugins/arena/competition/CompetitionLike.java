package org.battleplugins.arena.competition;

/**
 * Represents a competition-like object.
 */
public interface CompetitionLike<T extends Competition<T>> {

    /**
     * Gets the competition.
     *
     * @return the competition
     */
    T getCompetition();
}
