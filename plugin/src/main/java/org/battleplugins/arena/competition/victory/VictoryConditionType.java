package org.battleplugins.arena.competition.victory;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.types.HighestStatCondition;
import org.battleplugins.arena.competition.victory.types.TeamsAliveCondition;
import org.battleplugins.arena.competition.victory.types.TimeLimitCondition;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VictoryConditionType<C extends LiveCompetition<C>, T extends VictoryCondition<C>> {
    private static final Map<String, VictoryConditionType<?, ?>> VICTORY_TYPES = new HashMap<>();

    public static final VictoryConditionType<?, ?> HIGHEST_STAT = new VictoryConditionType<>("highest-stat", HighestStatCondition.class);
    public static final VictoryConditionType<?, ?> TEAMS_ALIVE = new VictoryConditionType<>("teams-alive", TeamsAliveCondition.class);
    public static final VictoryConditionType<?, ?> TIME_LIMIT = new VictoryConditionType<>("time-limit", TimeLimitCondition.class);

    private final Class<T> clazz;

    VictoryConditionType(String name, Class<T> clazz) {
        this.clazz = clazz;

        VICTORY_TYPES.put(name, this);
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

    public interface Provider<C extends LiveCompetition<C>, T extends VictoryCondition<C>> {

        T create(C competition);
    }
}
