package org.battleplugins.arena.resolver;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.TypeToken;

import java.util.Set;

public final class ResolverKeys {
    public static final ResolverKey<Arena> ARENA = ResolverKey.create("arena", Arena.class);
    public static final ResolverKey<Competition> COMPETITION = ResolverKey.create("competition", Competition.class);
    public static final ResolverKey<ArenaPlayer> KILLED = ResolverKey.create("killed", ArenaPlayer.class);
    public static final ResolverKey<ArenaPlayer> KILLER = ResolverKey.create("killer", ArenaPlayer.class);
    public static final ResolverKey<ArenaPlayer> PLAYER = ResolverKey.create("player", ArenaPlayer.class);
    public static final ResolverKey<Integer> LIVES_LEFT = ResolverKey.create("lives-left", Integer.class);
    public static final ResolverKey<Integer> ONLINE_PLAYERS = ResolverKey.create("online-players", Integer.class);
    public static final ResolverKey<Integer> MAX_PLAYERS = ResolverKey.create("max-players", Integer.class);
    public static final ResolverKey<Set<ArenaPlayer>> PLAYERS = ResolverKey.create("players", TypeToken.of(Set.class));
    public static final ResolverKey<ArenaStat> STAT = ResolverKey.create("stat", ArenaStat.class);
    public static final ResolverKey<StatHolder> STAT_HOLDER = ResolverKey.create("stat-holder", StatHolder.class);
    public static final ResolverKey<Object> OLD_STAT_VALUE = ResolverKey.create("old-stat-value", Object.class);
    public static final ResolverKey<Object> NEW_STAT_VALUE = ResolverKey.create("new-stat-value", Object.class);
    public static final ResolverKey<ArenaTeam> TEAM = ResolverKey.create("team", ArenaTeam.class);
}
