package org.battleplugins.arena.module.restoration;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.event.arena.ArenaCreateExecutorEvent;
import org.battleplugins.arena.messages.Message;
import org.battleplugins.arena.messages.Messages;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.nio.file.Path;
import java.util.Locale;

/**
 * A module that adds an action to restore arenas.
 */
@ArenaModule(id = ArenaRestoration.ID, name = "Arena Restoration", description = "Adds an action to restore arenas at a given point.", authors = "BattlePlugins")
public class ArenaRestoration implements ArenaModuleInitializer {
    public static final String ID = "arena-restoration";

    public static final EventActionType<RestoreArenaAction> RESTORE_ARENA_ACTION = EventActionType.create("restore-arena", RestoreArenaAction.class, RestoreArenaAction::new);

    public static final Message NO_BOUNDS = Messages.error("arena-restoration-no-bounds", "You must first set the map bounds before executing this command!");
    public static final Message SCHEMATIC_CREATED = Messages.success("arena-restoration-schematic-created", "Schematic created for map <secondary>{}</secondary>.");
    public static final Message FAILED_TO_CREATE_SCHEMATIC = Messages.error("arena-restoration-failed-to-create-schematic", "Failed to create schematic! Check the console for more information.");

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        // Check that we have WorldEdit installed
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            event.getBattleArena().module(ArenaRestoration.ID).ifPresent(container -> {
                container.disable("WorldEdit is required for the arena restoration module to work!");
            });
        }
    }

    @EventHandler
    public void onCreateExecutor(ArenaCreateExecutorEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.registerSubExecutor(new ArenaRestorationExecutor(this, event.getArena()));
    }

    public Path getSchematicPath(Arena arena, Competition<?> competition) {
        return arena.getPlugin().getDataFolder().toPath()
                .resolve("schematics")
                .resolve(arena.getName().toLowerCase(Locale.ROOT))
                .resolve(competition.getMap().getName().toLowerCase(Locale.ROOT) + "." +
                        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getPrimaryFileExtension()
                );
    }
}
