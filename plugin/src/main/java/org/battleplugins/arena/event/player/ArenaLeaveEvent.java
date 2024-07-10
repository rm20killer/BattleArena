package org.battleplugins.arena.event.player;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player leaves an arena.
 */
@EventTrigger("on-leave")
public class ArenaLeaveEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Cause cause;

    public ArenaLeaveEvent(ArenaPlayer player, Cause cause) {
        super(player.getArena(), player);

        this.cause = cause;
    }

    /**
     * Gets the cause of the player leaving the arena.
     *
     * @return the cause of the player leaving the arena
     */
    public Cause getCause() {
        return this.cause;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * The cause of the player leaving the arena.
     */
    public enum Cause {
        /**
         * The player left the arena by command.
         */
        COMMAND,
        /**
         * The player left the arena by disconnection.
         */
        DISCONNECT,
        /**
         * The player left the arena due to the game
         * kicking them out.
         */
        GAME,
        /**
         * The player left the arena due to the server
         * or game shutting down.
         */
        SHUTDOWN,
        /**
         * The plugin caused the player to leave the arena.
         */
        PLUGIN,
        /**
         * The player left the arena due to being kicked by
         * an administrator.
         */
        KICKED,
        /**
         * The competition was forcefully removed from the
         * arena.
         */
        REMOVED
    }
}
