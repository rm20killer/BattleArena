package org.battleplugins.arena.module.placeholderapi;

import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.BattleArenaShutdownEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

/**
 * A module that allows for hooking into the PlaceholderAPI plugin.
 */
@ArenaModule(id = PlaceholderApiIntegration.ID, name = "PlaceholderAPI", description = "Adds support for hooking into the PlaceholderAPI plugin.", authors = "BattlePlugins")
public class PlaceholderApiIntegration implements ArenaModuleInitializer {
    public static final String ID = "placeholderapi";

    private PlaceholderApiContainer container;

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        // Check that we have Vault installed
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            event.getBattleArena().module(PlaceholderApiIntegration.ID).ifPresent(container ->
                    container.disable("PlaceholderAPI is required for the PlaceholderAPI integration module to work!")
            );

            return;
        }

        this.container = new PlaceholderApiContainer(event.getBattleArena());
    }

    @EventHandler
    public void onShutdown(BattleArenaShutdownEvent event) {
        if (this.container != null) {
            this.container.disable();
        }
    }
}
