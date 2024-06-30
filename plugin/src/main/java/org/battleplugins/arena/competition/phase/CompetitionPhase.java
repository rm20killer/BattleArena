package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionLike;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.Id;
import org.battleplugins.arena.config.Scoped;
import org.battleplugins.arena.config.context.EventContextProvider;
import org.battleplugins.arena.config.context.OptionContextProvider;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.options.ArenaOptionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the phase of a competition.
 */
public abstract class CompetitionPhase<T extends Competition<T>> implements CompetitionLike<T>, ArenaListener {
    @Id
    private CompetitionPhaseType<T, CompetitionPhase<T>> type;

    @Scoped
    protected T competition;

    @ArenaOption(name = "allow-join", description = "Whether players can join during this phase.", required = true)
    private boolean allowJoin;

    @ArenaOption(name = "allow-spectate", description = "Whether players can spectate during this phase.")
    private boolean allowSpectate = true;

    @ArenaOption(name = "next-phase", description = "The next phase of the competition.")
    protected CompetitionPhaseType<T, CompetitionPhase<T>> nextPhase;

    @ArenaOption(
            name = "events",
            description = "The events actions to be performed during the game.",
            contextProvider = EventContextProvider.class
    )
    private Map<ArenaEventType<?>, List<EventAction>> eventActions;

    @ArenaOption(
            name = "options",
            description = "The options for this game phase.",
            contextProvider = OptionContextProvider.class
    )
    protected Map<ArenaOptionType<?>, org.battleplugins.arena.options.ArenaOption> options;

    // API methods

    /**
     * Called when the phase starts.
     */
    public abstract void onStart();

    /**
     * Called when the phase completes.
     */
    public abstract void onComplete();

    // Internal methods (cannot be overridden by extending plugins)

    void start() {
        this.onStart();
    }

    protected CompetitionPhase<T> previousPhase;

    void complete() {
        this.onComplete();
    }

    public final T getCompetition() {
        return this.competition;
    }

    /**
     * Gets whether players can join during this phase.
     *
     * @return whether players can join during this phase
     */
    public final boolean canJoin() {
        return this.allowJoin;
    }

    /**
     * Gets whether players can spectate during this phase.
     *
     * @return whether players can spectate during this phase
     */
    public final boolean canSpectate() {
        return this.allowSpectate;
    }

    /**
     * Gets the {@link EventAction actions} for this phase.
     *
     * @return the event actions for this phase
     */
    public final Map<ArenaEventType<?>, List<EventAction>> getEventActions() {
        return Map.copyOf(this.eventActions);
    }

    /**
     * Gets the {@link CompetitionPhaseType} of this phase.
     *
     * @return the competition phase type of this phase
     */
    public final CompetitionPhaseType<T, CompetitionPhase<T>> getType() {
        return this.type;
    }

    /**
     * Gets the next phase that succeeds this phase.
     *
     * @return the next phase that succeeds this phase
     */
    public final Optional<CompetitionPhaseType<T, CompetitionPhase<T>>> nextPhase() {
        return Optional.ofNullable(this.nextPhase);
    }

    /**
     * Gets the next phase that succeeds this phase.
     *
     * @return the next phase that succeeds this phase, or
     *         null if there is no next phase
     */
    @Nullable
    public final CompetitionPhaseType<T, CompetitionPhase<T>> getNextPhase() {
        return this.nextPhase;
    }

    /**
     * Gets the previous phase that precedes this phase.
     *
     * @return the previous phase that precedes this phase
     */
    public final Optional<CompetitionPhase<T>> previousPhase() {
        return Optional.ofNullable(this.previousPhase);
    }

    /**
     * Gets the previous phase that precedes this phase.
     *
     * @return the previous phase that precedes this phase, or
     *         null if there is no previous phase
     */
    @Nullable
    public final CompetitionPhase<T> getPreviousPhase() {
        return this.previousPhase;
    }

    /**
     * Sets the previous phase that precedes this phase.
     *
     * @param previousPhase the previous phase that precedes this phase
     */
    protected final void setPreviousPhase(CompetitionPhase<T> previousPhase) {
        this.previousPhase = previousPhase;
    }
}
