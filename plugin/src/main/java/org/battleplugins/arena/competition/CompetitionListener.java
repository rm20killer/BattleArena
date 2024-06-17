package org.battleplugins.arena.competition;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.player.ArenaDeathEvent;
import org.battleplugins.arena.event.player.ArenaKillEvent;
import org.battleplugins.arena.event.player.ArenaRespawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CompetitionListener<T extends Competition<T>> implements ArenaListener {

    private final LiveCompetition<T> competition;

    public CompetitionListener(LiveCompetition<T> competition) {
        this.competition = competition;
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event, ArenaPlayer player) {
        player.getCompetition().leave(player);
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event, ArenaPlayer player) {
        if (event.isCancelled()) {
            return;
        }

        // Call the death event
        this.competition.getArena().getEventManager().callEvent(new ArenaDeathEvent(player));

        // Now see if the player was killed by another player
        // in this same arena
        Player killer = player.getPlayer().getKiller();
        if (killer == null) {
            return;
        }

        ArenaPlayer killerPlayer = ArenaPlayer.getArenaPlayer(killer);
        if (killerPlayer == null) {
            return;
        }

        // Check if the killer is in the same arena
        if (killerPlayer.getCompetition().equals(this.competition)) {
            this.competition.getArena().getEventManager().callEvent(new ArenaKillEvent(killerPlayer, player));
        }
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerDeathEvent event, ArenaPlayer player) {
        if (event.isCancelled()) {
            return;
        }

        // Call the respawn event
        this.competition.getArena().getEventManager().callEvent(new ArenaRespawnEvent(player));
    }
}
