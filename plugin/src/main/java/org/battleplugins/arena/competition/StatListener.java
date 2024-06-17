package org.battleplugins.arena.competition;

import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.player.ArenaDeathEvent;
import org.battleplugins.arena.event.player.ArenaKillEvent;
import org.battleplugins.arena.stat.ArenaStats;
import org.bukkit.event.EventPriority;

public class StatListener<T extends Competition<T>> implements ArenaListener, CompetitionLike<T> {
    private final LiveCompetition<T> competition;

    public StatListener(LiveCompetition<T> competition) {
        this.competition = competition;
    }

    @ArenaEventHandler(priority = EventPriority.LOWEST)
    public void onDeath(ArenaDeathEvent event) {
        event.getArenaPlayer().computeStat(ArenaStats.DEATHS, old -> (old == null ? 0 : old) + 1);
        if (event.getArena().isLivesEnabled()) {
            event.getArenaPlayer().computeStat(ArenaStats.LIVES, old -> (old == null ? 0 : old) - 1);
        }
    }

    @ArenaEventHandler(priority = EventPriority.LOWEST)
    public void onKill(ArenaKillEvent event) {
        event.getKiller().computeStat(ArenaStats.KILLS, old -> (old == null ? 0 : old) + 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getCompetition() {
        return (T) this.competition;
    }
}
