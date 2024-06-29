package org.battleplugins.arena.event.player;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.EventTrigger;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called for {@link ArenaPlayer}s who lose a life
 * in an {@link Arena}.
 * <p>
 * This event will be called any time a player has a
 * life left to live. In the event the player has no
 * lives left, {@link ArenaLivesExhaustEvent} will be called.
 */
@EventTrigger("on-life-deplete")
public class ArenaLifeDepleteEvent extends BukkitArenaPlayerEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final int livesLeft;

    public ArenaLifeDepleteEvent(@NotNull Arena arena, @NotNull ArenaPlayer player, int livesLeft) {
        super(arena, player);

        this.livesLeft = livesLeft;
    }

    /**
     * Gets the amount of lives the player has left.
     *
     * @return the amount of lives the player has left
     */
    public int getLivesLeft() {
        return this.livesLeft;
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
