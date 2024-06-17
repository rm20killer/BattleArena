package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;

public class PhaseManager<T extends Competition<T>> {
    private final Arena arena;
    private final T competition;

    private CompetitionPhase<T> currentPhase;

    public PhaseManager(Arena arena, T competition) {
        this.arena = arena;
        this.competition = competition;
    }

    public void setPhase(CompetitionPhaseType<?, ?> phaseType) {
        this.setPhase(phaseType, true);
    }

    public void setPhase(CompetitionPhaseType<?, ?> phaseType, boolean complete) {
        if (this.currentPhase != null) {
            if (complete) {
                this.currentPhase.complete();
            }
            this.arena.getEventManager().unregisterEvents(this.currentPhase);
        }

        this.currentPhase = this.arena.createPhase(phaseType, this.competition);
        this.arena.getEventManager().registerEvents(this.currentPhase);
        this.currentPhase.start();
    }

    public CompetitionPhase<T> getCurrentPhase() {
        return this.currentPhase;
    }
}
