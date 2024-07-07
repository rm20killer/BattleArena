package org.battleplugins.arena.event.player;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.team.ArenaTeam;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves an {@link ArenaTeam}.
 */
public class ArenaTeamLeaveEvent extends BukkitArenaPlayerEvent {

    private final static HandlerList HANDLERS = new HandlerList();

    private final ArenaTeam team;

    public ArenaTeamLeaveEvent(ArenaPlayer player, ArenaTeam team) {
        super(player.getArena(), player);
        this.team = team;
    }

    /**
     * Returns the {@link ArenaTeam} the player left.
     *
     * @return the team the player left
     */
    public ArenaTeam getTeam() {
        return this.team;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
