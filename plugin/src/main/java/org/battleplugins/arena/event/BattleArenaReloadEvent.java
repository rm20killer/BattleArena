package org.battleplugins.arena.event;

import org.battleplugins.arena.BattleArena;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when BattleArena is reloaded.
 */
public class BattleArenaReloadEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();

    private final BattleArena battleArena;

    public BattleArenaReloadEvent(BattleArena battleArena) {
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
