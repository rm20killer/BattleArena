package org.battleplugins.arena.module.boundaryenforcer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.map.options.Bounds;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.arena.ArenaInitializeEvent;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A module that enforces game boundaries and ensures players do not leave it.
 */
@ArenaModule(id = BoundaryEnforcer.ID, name = "Boundary Enforcer", description = "Enforces game boundaries and ensures players do not leave it.", authors = "BattlePlugins")
public class BoundaryEnforcer implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "boundary-enforcer";

    private static final long ALERT_INTERVAL = 2000L;
    private static final Message CANNOT_LEAVE_ARENA = Messages.message("cannot-leave-arena", Component.text("You cannot leave the arena!", NamedTextColor.RED));

    private final Map<UUID, Long> lastAlert = new HashMap<>();

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerEvents(this);
    }

    @ArenaEventHandler
    public void onMove(PlayerMoveEvent event, ArenaPlayer player) {
        // Check to see if the player has changed blocks
        if (event.getFrom().toBlock().equals(event.getTo().toBlock())) {
            return;
        }

        // Check to see if the player is in the arena
        Bounds bounds = player.getCompetition().getMap().getBounds();
        if (bounds == null || bounds.isInside(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ())) {
            return;
        }

        event.setCancelled(true);

        // Check to see if the player has been alerted recently
        if (!this.lastAlert.containsKey(player.getPlayer().getUniqueId()) || System.currentTimeMillis() - this.lastAlert.get(player.getPlayer().getUniqueId()) >= ALERT_INTERVAL) {
            CANNOT_LEAVE_ARENA.send(player.getPlayer());
            this.lastAlert.put(player.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
}
