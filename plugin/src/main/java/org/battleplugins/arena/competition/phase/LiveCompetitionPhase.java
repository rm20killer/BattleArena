package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.arena.ArenaPhaseCompleteEvent;
import org.battleplugins.arena.event.arena.ArenaPhaseStartEvent;
import org.battleplugins.arena.options.ArenaOptionType;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.resolver.Resolver;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents a live competition phase.
 *
 * @param <T> the type of competition
 */
public abstract class LiveCompetitionPhase<T extends LiveCompetition<T>> extends CompetitionPhase<T> implements Resolvable {

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

    /**
     * Advances to the next phase.
     */
    protected void advanceToNextPhase() {
        if (this.nextPhase == null) {
            this.competition.getArena().getPlugin().warn("No next phase found for {}! Not advancing to next phase.", this.getClass().getSimpleName());
            return;
        }

        // If the victory manager is closed, don't advance to the next phase.
        // This competition is no longer joinable and should be de-referenced.
        if (this.competition.getVictoryManager().isClosed()) {
            return;
        }

        this.setPhase(this.nextPhase);
    }

    /**
     * Gets the {@link org.battleplugins.arena.options.ArenaOption} of the specified type.
     *
     * @param type the type of option
     * @param <E> the type of option
     * @return the option of the specified type
     */
    public final <E extends org.battleplugins.arena.options.ArenaOption> Optional<E> option(ArenaOptionType<E> type) {
        return Optional.ofNullable(this.getOption(type));
    }

    /**
     * Gets the {@link org.battleplugins.arena.options.ArenaOption} of the specified type.
     *
     * @param type the type of option
     * @param <E> the type of option
     * @return the option of the specified type, or null if it does not exist
     */
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

    @Override
    public Resolver resolve() {
        return Resolver.builder().build();
    }
}
