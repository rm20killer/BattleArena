package org.battleplugins.arena.competition.victory;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionLike;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.arena.ArenaPhaseStartEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VictoryManager<T extends Competition<T>> implements ArenaListener, CompetitionLike<T> {
    private final Map<VictoryConditionType<?, ?>, VictoryCondition<?>> victoryConditions = new HashMap<>();

    private final Arena arena;
    private final T competition;

    private boolean closed = false;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public VictoryManager(Arena arena, T competition) {
        this.arena = arena;
        this.competition = competition;

        for (Map.Entry<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> entry : arena.getVictoryConditions().entrySet()) {
            VictoryConditionType<?, ?> type = entry.getKey();
            VictoryCondition<?> condition = ((VictoryConditionType.Provider) entry.getValue()).create((LiveCompetition) competition);

            arena.getEventManager().registerEvents(condition);
            this.victoryConditions.put(type, condition);

            arena.getPlugin().debug("Registered victory condition: {} in competition: {}", condition.getClass().getSimpleName(), competition.getClass().getSimpleName());
        }

        arena.getEventManager().registerEvents(this);
    }

    public Set<ArenaPlayer> identifyPotentialVictors() {
        Set<ArenaPlayer> victors = new HashSet<>();
        int conditionsWithVictors = 0;
        for (VictoryCondition<?> condition : this.victoryConditions.values()) {
            Set<ArenaPlayer> potentialVictors = new HashSet<>(condition.identifyPotentialVictors());

            // Remove existing victors to avoid double counting
            potentialVictors.removeAll(victors);

            victors.addAll(potentialVictors);
            if (!potentialVictors.isEmpty()) {
                conditionsWithVictors++;
            }
        }

        // If we have victory conditions with multiple victors,
        // we cannot realistically determine who actually "won"
        // the game. Future idea: Make this configurable? Need to
        // find a compelling use case for this.
        if (conditionsWithVictors > 1) {
            return Set.of();
        }

        return victors;
    }

    public void end(boolean closed) {
        this.closed = closed;

        for (VictoryCondition<?> condition : this.victoryConditions.values()) {
            condition.end();
        }

        if (closed) {
            this.arena.getEventManager().unregisterEvents(this);
            for (VictoryCondition<?> condition : this.victoryConditions.values()) {
                this.arena.getEventManager().unregisterEvents(condition);
            }
        }
    }

    @ArenaEventHandler
    public void onPhaseStart(ArenaPhaseStartEvent event) {
        if (CompetitionPhaseType.VICTORY.equals(event.getPhase().getNextPhase())) {
            // Start the victory conditions
            for (VictoryCondition<?> condition : this.victoryConditions.values()) {
                condition.start();
            }
        }
    }

    @Override
    public T getCompetition() {
        return this.competition;
    }

    public final boolean isClosed() {
        return this.closed;
    }
}
