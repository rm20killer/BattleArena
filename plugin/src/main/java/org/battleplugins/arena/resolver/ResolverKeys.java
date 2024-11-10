package org.battleplugins.arena.resolver;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.map.CompetitionMap;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.victory.VictoryConditionType;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.TypeToken;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ResolverKeys {
    private static final Map<String, ResolverKey<?>> RESOLVER_KEYS = new HashMap<>();
    
    public static final ResolverKey<Arena> ARENA = register("arena", Arena.class);
    public static final ResolverKey<Integer> ALIVE_PLAYERS = register("alive-players", Integer.class);
    public static final ResolverKey<Competition> COMPETITION = register("competition", Competition.class);
    public static final ResolverKey<ArenaPlayer> KILLED = register("killed", ArenaPlayer.class);
    public static final ResolverKey<ArenaPlayer> KILLER = register("killer", ArenaPlayer.class);
    public static final ResolverKey<ArenaPlayer> PLAYER = register("player", ArenaPlayer.class);
    public static final ResolverKey<Integer> LIVES_LEFT = register("lives-left", Integer.class);
    public static final ResolverKey<Integer> ONLINE_PLAYERS = register("online-players", Integer.class);
    public static final ResolverKey<CompetitionMap> MAP = register("map", CompetitionMap.class);
    public static final ResolverKey<Integer> MAX_PLAYERS = register("max-players", Integer.class);
    public static final ResolverKey<Set<ArenaPlayer>> PLAYERS = register("players", TypeToken.of(Set.class));
    public static final ResolverKey<Integer> SPECTATORS = register("spectators", Integer.class);
    public static final ResolverKey<ArenaStat> STAT = register("stat", ArenaStat.class);
    public static final ResolverKey<StatHolder> STAT_HOLDER = register("stat-holder", StatHolder.class);
    public static final ResolverKey<Object> OLD_STAT_VALUE = register("old-stat-value", Object.class);
    public static final ResolverKey<Object> NEW_STAT_VALUE = register("new-stat-value", Object.class);
    public static final ResolverKey<CompetitionPhase> PHASE = register("phase", CompetitionPhase.class);
    public static final ResolverKey<ArenaTeam> TEAM = register("team", ArenaTeam.class);
    public static final ResolverKey<Duration> TIME_REMAINING = register("time-remaining", Duration.class);
    public static final ResolverKey<Duration> TIME_REMAINING_SHORT = register("time-remaining-short", Duration.class);
    public static final ResolverKey<Duration> REMAINING_START_TIME = register("remaining-start-time", Duration.class);
    public static final ResolverKey<VictoryConditionType> VICTORY_CONDITION_TYPE = register("victory-condition-type", VictoryConditionType.class);
    
    private static <T> ResolverKey<T> register(String name, Class<T> type) {
        ResolverKey<T> key = ResolverKey.create(name, type);
        RESOLVER_KEYS.put(name, key);
        return key;
    }

    private static <T> ResolverKey<T> register(String name, TypeToken<T> type) {
        ResolverKey<T> key = ResolverKey.create(name, type);
        RESOLVER_KEYS.put(name, key);
        return key;
    }

    @SuppressWarnings("unchecked")
    public static <T> ResolverKey<T> get(String name) {
        return (ResolverKey<T>) RESOLVER_KEYS.get(name);
    }
}
