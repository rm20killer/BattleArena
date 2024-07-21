package org.battleplugins.arena.competition.team;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.player.ArenaStatChangeEvent;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class TeamStatHolder implements StatHolder {
    private final TeamManager teamManager;
    private final ArenaTeam team;

    private final Map<ArenaStat<?>, Object> globalStats = new HashMap<>();

    public TeamStatHolder(TeamManager teamManager, ArenaTeam team) {
        this.teamManager = teamManager;
        this.team = team;
    }

    @Override
    public <T> Optional<T> stat(ArenaStat<T> stat) {
        return Optional.ofNullable(this.getStat(stat));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getStat(ArenaStat<T> stat) {
        if (this.globalStats.containsKey(stat)) {
            return (T) this.globalStats.get(stat);
        }

        if (!Number.class.isAssignableFrom(stat.getType())) {
            throw new IllegalArgumentException("Don't know how to accumulate type " + stat.getType());
        }

        Set<ArenaPlayer> players = this.teamManager.getPlayersOnTeam(this.team);
        Number total = null;
        for (ArenaPlayer player : players) {
            T playerStat = player.getStat(stat);
            if (playerStat == null) {
                continue;
            }

            if (total == null) {
                total = (Number) playerStat;
            } else {
                Class<T> type = stat.getType();
                if (type.equals(Integer.class)) {
                    total = total.intValue() + (Integer) playerStat;
                } else if (type.equals(Double.class)) {
                    total = total.doubleValue() + (Double) playerStat;
                } else if (type.equals(Float.class)) {
                    total = total.floatValue() + (Float) playerStat;
                } else if (type.equals(Long.class)) {
                    total = total.longValue() + (Long) playerStat;
                } else if (type.equals(Short.class)) {
                    total = total.shortValue() + (Short) playerStat;
                } else if (type.equals(Byte.class)) {
                    total = total.byteValue() + (Byte) playerStat;
                } else {
                    throw new IllegalArgumentException("Don't know how to accumulate type " + stat.getType());
                }
            }
        }

        return (T) total;
    }

    @Override
    public <T> void setStat(ArenaStat<T> stat, T value) {
        this.globalStats.put(stat, value);
    }

    @Override
    public <T> void computeStat(ArenaStat<T> stat, Function<? super T, ? extends T> computeFunction) {
        this.globalStats.compute(stat, (key, oldValue) -> {
            T newValue = computeFunction.apply((T) oldValue);
            return this.statChange(stat, (T) oldValue, newValue);
        });
    }

    @Override
    public String describe() {
        return this.team.getName();
    }

    public ArenaTeam getTeam() {
        return this.team;
    }

    private <T> T statChange(ArenaStat<T> stat, T oldValue, T newValue) {
        ArenaStatChangeEvent<T> event = new ArenaStatChangeEvent<>(this.teamManager.getCompetition(), this, stat, oldValue, newValue);
        this.teamManager.getCompetition().getArena().getEventManager().callEvent(event);
        return event.getNewValue();
    }
}
