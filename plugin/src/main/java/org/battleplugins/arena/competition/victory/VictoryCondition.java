package org.battleplugins.arena.competition.victory;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.CompetitionLike;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.phase.phases.VictoryPhase;
import org.battleplugins.arena.config.Id;
import org.battleplugins.arena.config.Scoped;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;

import java.util.Set;

public class VictoryCondition<T extends LiveCompetition<T>> implements CompetitionLike<T>, ArenaListener, Resolvable {

    @Id
    private VictoryConditionType<?, ?> type;

    @Scoped
    protected T competition;

    // API methods

    public void onStart() {

    }

    public void onEnd() {

    }

    public Set<ArenaPlayer> identifyPotentialVictors() {
        return Set.of();
    }

    // Internal methods (cannot be overridden by extending plugins)

    public final void start() {
        this.competition.getArena().getPlugin().debug("Starting victory condition: {}", this.getClass().getSimpleName());
        this.onStart();
    }

    public final void end() {
        this.competition.getArena().getPlugin().debug("Ending victory condition: {}", this.getClass().getSimpleName());
        this.onEnd();
    }

    public final T getCompetition() {
        return this.competition;
    }

    public final void advanceToNextPhase(Set<ArenaPlayer> victors) {
        CompetitionPhase<T> currentPhase = this.competition.getPhaseManager().getCurrentPhase();
        CompetitionPhaseType<T, CompetitionPhase<T>> nextPhase = currentPhase.getNextPhase();

        this.competition.getArena().getPlugin().debug("Condition {} advancing to next phase: {}", this.getClass().getSimpleName(), nextPhase == null ? "NONE" : nextPhase.getName());

        // Ensure the next phase is a victory phase
        if (!CompetitionPhaseType.VICTORY.equals(nextPhase)) {
            this.competition.getArena().getPlugin().warn("Victory conditions for {} were met, but the next phase was not a victory phase. Not advancing onto the next phase!", this.getClass().getSimpleName());
            return;
        }

        this.competition.getPhaseManager().setPhase(nextPhase);

        // Get the victory phase and call the onVictory method
        CompetitionPhase<T> victoryPhase = this.competition.getPhaseManager().getCurrentPhase();
        if (victoryPhase instanceof VictoryPhase) {
            if (victors.isEmpty()) {
                ((VictoryPhase<T>) victoryPhase).onDraw();
            } else {
                ((VictoryPhase<T>) victoryPhase).onVictory(victors);
            }
        }

        // End all competition phases to ensure no other
        // victory conditions can run and get out of sync
        this.competition.getVictoryManager().end(false);
    }

    @Override
    public Resolver resolve() {
        return Resolver.builder()
                .define(ResolverKeys.VICTORY_CONDITION_TYPE, ResolverProvider.simple(this.type, VictoryConditionType::getName))
                .build();
    }
}
