package org.battleplugins.arena.event.player;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.JoinResult;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.event.ArenaEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is about to join an arena.
 */
public class ArenaPreJoinEvent extends PlayerEvent implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final Competition<?> competition;
    private final PlayerRole role;
    private JoinResult result;

    public ArenaPreJoinEvent(Arena arena, Competition<?> competition, PlayerRole role, JoinResult result, Player player) {
        super(player);

        this.arena = arena;
        this.competition = competition;
        this.role = role;
        this.result = result;
    }

    /**
     * Gets the arena the player is joining.
     *
     * @return the arena the player is joining
     */
    @Override
    public Arena getArena() {
        return this.arena;
    }

    /**
     * Gets the competition the player is joining.
     *
     * @return the competition the player is joining
     */
    @Override
    public Competition<?> getCompetition() {
        return this.competition;
    }

    /**
     * Gets the role the player is joining as.
     *
     * @return the role the player is joining as
     */
    public PlayerRole getRole() {
        return this.role;
    }

    /**
     * Gets the result of the join.
     *
     * @return the result of the join
     */
    public JoinResult getResult() {
        return this.result;
    }

    /**
     * Sets the result of the join.
     *
     * @param result the result of the join
     */
    public void setResult(JoinResult result) {
        this.result = result;
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
