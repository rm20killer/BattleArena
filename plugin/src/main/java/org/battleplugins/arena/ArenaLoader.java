package org.battleplugins.arena;

import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.arena.ArenaCreateExecutorEvent;
import org.battleplugins.arena.event.arena.ArenaInitializeEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

record ArenaLoader(BattleArena battleArena, String mode, Configuration configuration, Path arenaPath) {

    public void load() {
        if (Files.isDirectory(this.arenaPath)) {
            return;
        }

        try {
            ArenaType arenaType = this.battleArena.arenaTypes.get(this.mode);
            if (arenaType == null) {
                this.battleArena.info("Arena {} specified the mode as {}, which is not valid! Recognized modes: {}", this.arenaPath.getFileName(), this.mode, this.battleArena.arenaTypes.keySet());
                return;
            }

            Arena arena = ArenaConfigParser.newInstance(this.arenaPath, arenaType.arenaClass(), this.configuration, this.battleArena);

            // Need to call this here rather than in the Arena constructor
            // so third party arenas can register their own event resolvers
            arena.getEventManager().registerEvents(arena);

            Bukkit.getPluginManager().registerEvents(arena, this.battleArena);

            this.battleArena.arenas.put(arena.getName(), arena);

            // Register command
            PluginCommand command = this.battleArena.getCommand(arena.getName().toLowerCase(Locale.ROOT));

            ArenaCommandExecutor executor = arena.createCommandExecutor();
            ArenaCreateExecutorEvent event = new ArenaCreateExecutorEvent(arena, executor);
            event.callEvent();

            command.setExecutor(executor);

            new ArenaInitializeEvent(arena).callEvent();

            this.battleArena.info("Loaded arena: {}.", arena.getName());
        } catch (ParseException e) {
            ParseException.handle(e
                    .context("Arena", configuration.getString("name"))
                    .context("Mode", this.mode)
            );
        }
    }
}
