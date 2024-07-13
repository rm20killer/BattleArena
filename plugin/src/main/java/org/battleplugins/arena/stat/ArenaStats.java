package org.battleplugins.arena.stat;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ArenaStats {
    private static final Map<String, ArenaStat<?>> STATS = new HashMap<>();

    public static final ArenaStat<Integer> DEATHS = register(new SimpleArenaStat<>("deaths", "Deaths", 0, Integer.class));
    public static final ArenaStat<Integer> LIVES = register(new SimpleArenaStat<>("lives", "Lives", 1, Integer.class));
    public static final ArenaStat<Integer> KILLS = register(new SimpleArenaStat<>("kills", "Kills", 0, Integer.class));

    private ArenaStats() {
    }

    public static <T extends ArenaStat<?>> T register(T stat) {
        STATS.put(stat.getKey(), stat);
        return stat;
    }

    @Nullable
    public static ArenaStat<?> get(String name) {
        return STATS.get(name);
    }

    public static Set<ArenaStat<?>> values() {
        return Set.copyOf(STATS.values());
    }
}
