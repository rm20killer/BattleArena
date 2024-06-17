package org.battleplugins.arena.event;

import org.battleplugins.arena.BattleArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when BattleArena is starting its initialization.
 */
public class BattleArenaPreInitializeEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();

    private final BattleArena battleArena;

    public BattleArenaPreInitializeEvent(BattleArena battleArena) {
        this.battleArena = battleArena;
    }

    /**
     * Gets the {@link BattleArena} instance.
     *
     * @return the BattleArena instance
     */
    public BattleArena getBattleArena() {
        return battleArena;
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
