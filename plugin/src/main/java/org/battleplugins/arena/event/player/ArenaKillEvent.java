package org.battleplugins.arena.event.player;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player kills another player in
 * an arena.
 */
@EventTrigger("on-kill")
public class ArenaKillEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final ArenaPlayer killed;

    public ArenaKillEvent(ArenaPlayer player, ArenaPlayer killed) {
        super(player.getArena(), player);

        this.killed = killed;
    }

    /**
     * Returns the player that was killed.
     *
     * @return the player that was killed
     */
    public ArenaPlayer getKilled() {
        return this.killed;
    }

    /**
     * Returns the player that killed the other player.
     *
     * @return the player that killed the other player
     */
    public ArenaPlayer getKiller() {
        return this.getArenaPlayer();
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
