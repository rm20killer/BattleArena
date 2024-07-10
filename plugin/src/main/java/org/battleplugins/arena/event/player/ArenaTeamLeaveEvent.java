package org.battleplugins.arena.event.player;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
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

    @Override
    public Resolver resolve() {
        return super.resolve().toBuilder()
                .define(ResolverKeys.TEAM, ResolverProvider.simple(this.team, ArenaTeam::getName, ArenaTeam::getFormattedName))
                .build();
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
