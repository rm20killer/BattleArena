package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;

/**
 * Manages the phases of a competition.
 *
 * @param <T> the type of competition
 */
public class PhaseManager<T extends Competition<T>> {
    private final Arena arena;
    private final T competition;

    private CompetitionPhase<T> currentPhase;

    public PhaseManager(Arena arena, T competition) {
        this.arena = arena;
        this.competition = competition;
    }

    /**
     * Sets the current phase of the competition.
     *
     * @param phaseType the phase type to set
     */
    public void setPhase(CompetitionPhaseType<?, ?> phaseType) {
        this.setPhase(phaseType, true);
    }

    /**
     * Sets the current phase of the competition.
     *
     * @param phaseType the phase type to set
     * @param complete whether the phase is complete
     */
    public void setPhase(CompetitionPhaseType<?, ?> phaseType, boolean complete) {
        this.end(complete);

        this.currentPhase = this.arena.createPhase(phaseType, this.competition);
        this.arena.getEventManager().registerEvents(this.currentPhase);
        this.currentPhase.start();
    }

    /**
     * Ends the current phase of the competition.
     *
     * @param complete whether the phase is complete
     */
    public void end(boolean complete) {
        if (this.currentPhase != null) {
            if (complete) {
                this.currentPhase.complete();
            }
            this.arena.getEventManager().unregisterEvents(this.currentPhase);
        }
    }

    /**
     * Returns the current {@link CompetitionPhase} of the competition.
     *
     * @return the current phase of the competition
     */
    public CompetitionPhase<T> getCurrentPhase() {
        return this.currentPhase;
    }
}
