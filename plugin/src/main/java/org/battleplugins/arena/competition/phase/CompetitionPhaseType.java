package org.battleplugins.arena.competition.phase;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.phase.phases.CountdownPhase;
import org.battleplugins.arena.competition.phase.phases.IngamePhase;
import org.battleplugins.arena.competition.phase.phases.VictoryPhase;
import org.battleplugins.arena.competition.phase.phases.WaitingPhase;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a type of competition phase.
 *
 * @param <C> the type of competition
 * @param <T> the type of competition phase
 */
public final class CompetitionPhaseType<C extends Competition<C>, T extends CompetitionPhase<C>> {
    private static final Map<String, CompetitionPhaseType<?, ?>> PHASE_TYPES = new HashMap<>();

    public static final CompetitionPhaseType<?, WaitingPhase<?>> WAITING = new CompetitionPhaseType("waiting", WaitingPhase.class);
    public static final CompetitionPhaseType<?, CountdownPhase<?>> COUNTDOWN = new CompetitionPhaseType("countdown", CountdownPhase.class);
    public static final CompetitionPhaseType<?, IngamePhase<?>> INGAME = new CompetitionPhaseType("ingame", IngamePhase.class);
    public static final CompetitionPhaseType<?, VictoryPhase<?>> VICTORY = new CompetitionPhaseType("victory", VictoryPhase.class);

    private final String name;
    private final Class<T> clazz;

    CompetitionPhaseType(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;

        PHASE_TYPES.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getPhaseType() {
        return this.clazz;
    }

    @Nullable
    public static CompetitionPhaseType<?, ?> get(String name) {
        return PHASE_TYPES.get(name);
    }

    public static <C extends Competition<C>, T extends CompetitionPhase<C>> CompetitionPhaseType<C, T> create(String name, Class<T> clazz) {
        return new CompetitionPhaseType<>(name, clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompetitionPhaseType<?, ?> that = (CompetitionPhaseType<?, ?>) o;
        return Objects.equals(this.clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz);
    }

    public static Set<CompetitionPhaseType<?, ?>> values() {
        return Set.copyOf(PHASE_TYPES.values());
    }

    public interface Provider<C extends Competition<C>, T extends CompetitionPhase<C>> {

        T create(C competition);
    }
}
