package org.battleplugins.arena.competition.victory;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.types.HighestStatCondition;
import org.battleplugins.arena.competition.victory.types.TeamsAliveCondition;
import org.battleplugins.arena.competition.victory.types.TimeLimitCondition;
import org.battleplugins.arena.config.DocumentationSource;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a victory condition type.
 *
 * @param <C> the type of competition
 * @param <T> the type of victory condition
 */
@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/victory-conditions-reference")
public final class VictoryConditionType<C extends LiveCompetition<C>, T extends VictoryCondition<C>> {
    private static final Map<String, VictoryConditionType<?, ?>> VICTORY_TYPES = new HashMap<>();

    public static final VictoryConditionType<?, ?> HIGHEST_STAT = new VictoryConditionType<>("highest-stat", HighestStatCondition.class);
    public static final VictoryConditionType<?, ?> TEAMS_ALIVE = new VictoryConditionType<>("teams-alive", TeamsAliveCondition.class);
    public static final VictoryConditionType<?, ?> TIME_LIMIT = new VictoryConditionType<>("time-limit", TimeLimitCondition.class);

    private final String name;
    private final Class<T> clazz;

    VictoryConditionType(String name, Class<T> clazz) {
        this.name = name;
        this.clazz = clazz;

        VICTORY_TYPES.put(name, this);
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getVictoryType() {
        return this.clazz;
    }

    @Nullable
    public static VictoryConditionType<?, ?> get(String name) {
        return VICTORY_TYPES.get(name);
    }

    public static <C extends LiveCompetition<C>, T extends VictoryCondition<C>> VictoryConditionType<C, T> create(String name, Class<T> clazz) {
        return new VictoryConditionType<>(name, clazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VictoryConditionType<?, ?> that)) return false;
        return this.clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return this.clazz.hashCode();
    }

    public static Set<VictoryConditionType<?, ?>> values() {
        return Set.copyOf(VICTORY_TYPES.values());
    }

    public interface Provider<C extends LiveCompetition<C>, T extends VictoryCondition<C>> {

        T create(C competition);
    }
}
