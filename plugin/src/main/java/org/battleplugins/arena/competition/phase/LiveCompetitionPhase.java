package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.arena.ArenaPhaseCompleteEvent;
import org.battleplugins.arena.event.arena.ArenaPhaseStartEvent;
import org.battleplugins.arena.options.ArenaOptionType;
import org.jetbrains.annotations.Nullable;

public abstract class LiveCompetitionPhase<T extends LiveCompetition<T>> extends CompetitionPhase<T> {

    public void setPhase(CompetitionPhaseType<T, CompetitionPhase<T>> phase) {
        this.competition.getPhaseManager().setPhase(phase);
        this.competition.getPhaseManager().getCurrentPhase().setPreviousPhase(this);
    }

    public void setPhase(CompetitionPhaseType<T, CompetitionPhase<T>> phase, boolean complete) {
        this.competition.getPhaseManager().setPhase(phase, complete);
        this.competition.getPhaseManager().getCurrentPhase().setPreviousPhase(this);
    }

    @Override
    final void start() {
        super.start();

        this.competition.getArena().getEventManager().callEvent(new ArenaPhaseStartEvent(
                this.competition.getArena(),
                this.competition,
                this
        ));
    }

    @Override
    final void complete() {
        this.competition.getArena().getEventManager().callEvent(new ArenaPhaseCompleteEvent(
                this.competition.getArena(),
                this.competition,
                this
        ));

        super.complete();
    }

    protected void advanceToNextPhase() {
        if (this.nextPhase == null) {
            this.competition.getArena().getPlugin().warn("No next phase found for {}! Not advancing to next phase.", this.getClass().getSimpleName());
            return;
        }

        this.setPhase(this.nextPhase);
    }

    @Nullable
    public <E extends org.battleplugins.arena.options.ArenaOption> E getOption(ArenaOptionType<E> type) {
        if (this.options == null) {
            return null;
        }

        E arenaOption = (E) this.options.get(type);
        if (arenaOption == null) {
            return this.competition.getArena().getOption(type);
        }

        return arenaOption;
    }
}
