package org.battleplugins.arena.module.oitc;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.arena.ArenaInitializeEvent;
import org.battleplugins.arena.event.player.ArenaKillEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * A module that adds one in the chamber to BattleArena.
 */
@ArenaModule(id = OneInTheChamber.ID, name = "One in the Chamber", description = "Adds One in the Chamber to BattleArena.", authors = "BattlePlugins")
public class OneInTheChamber implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "one-in-the-chamber";

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerArenaResolver(ProjectileHitEvent.class, hitEvent -> {
            if (!(hitEvent.getEntity() instanceof AbstractArrow arrow && arrow.getShooter() instanceof Player player)) {
                return null;
            }

            return ArenaPlayer.arenaPlayer(player)
                    .map(ArenaPlayer::getCompetition)
                    .orElse(null);
        });

        event.getArena().getEventManager().registerEvents(this);
    }

    @ArenaEventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof AbstractArrow arrow && arrow.getShooter() instanceof Player shooter)) {
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        event.setCancelled(true);

        victim.damage(10000, shooter);
    }

    @ArenaEventHandler
    public void onKill(ArenaKillEvent event) {
        event.getKiller().getPlayer().getInventory().addItem(new ItemStack(Material.ARROW));
    }

    @ArenaEventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            arrow.remove();
        }
    }
}
