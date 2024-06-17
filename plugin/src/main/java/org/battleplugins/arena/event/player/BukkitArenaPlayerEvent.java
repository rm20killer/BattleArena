package org.battleplugins.arena.event.player;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitArenaPlayerEvent extends PlayerEvent implements ArenaPlayerEvent {
    private final Arena arena;
    private final ArenaPlayer player;

    public BukkitArenaPlayerEvent(@NotNull Arena arena, @NotNull ArenaPlayer player) {
        super(player.getPlayer());

        this.arena = arena;
        this.player = player;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public ArenaPlayer getArenaPlayer() {
        return this.player;
    }
}
