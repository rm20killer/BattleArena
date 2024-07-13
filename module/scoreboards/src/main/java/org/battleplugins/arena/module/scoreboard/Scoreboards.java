package org.battleplugins.arena.module.scoreboard;

import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.action.EventActionType;
import org.battleplugins.arena.event.arena.ArenaInitializeEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleContainer;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.battleplugins.arena.module.scoreboard.action.ApplyScoreboardAction;
import org.battleplugins.arena.module.scoreboard.action.RemoveScoreboardAction;
import org.battleplugins.arena.module.scoreboard.config.ScoreboardLineCreatorContextProvider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A module that adds scoreboards to the arena.
 */
@ArenaModule(id = Scoreboards.ID, name = "Scoreboards", description = "Adds scoreboards to BattleArena.", authors = "BattlePlugins")
public class Scoreboards implements ArenaModuleInitializer, ArenaListener {
    public static final String ID = "scoreboards";

    public static final EventActionType<ApplyScoreboardAction> APPLY_SCOREBOARD_ACTION = EventActionType.create("apply-scoreboard", ApplyScoreboardAction.class, ApplyScoreboardAction::new);
    public static final EventActionType<RemoveScoreboardAction> REMOVE_SCOREBOARD_ACTION = EventActionType.create("remove-scoreboard", RemoveScoreboardAction.class, RemoveScoreboardAction::new);

    private ScoreboardsConfig config;

    public Scoreboards() {
        ArenaConfigParser.registerContextProvider(ScoreboardLineCreatorContextProvider.class, new ScoreboardLineCreatorContextProvider());
    }

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        ArenaModuleContainer<Scoreboards> container = event.getBattleArena()
                .<Scoreboards>module(ID)
                .orElseThrow();

        Path dataFolder = event.getBattleArena().getDataFolder().toPath();
        Path scoreboardsPath = dataFolder.resolve("scoreboards.yml");
        if (Files.notExists(scoreboardsPath)) {
            InputStream inputStream = container.getResource("scoreboards.yml");
            try {
                Files.copy(inputStream, scoreboardsPath);
            } catch (Exception e) {
                event.getBattleArena().error("Failed to copy scoreboards.yml to data folder!", e);
                container.disable("Failed to copy scoreboards.yml to data folder!");
                return;
            }
        }

        Configuration scoreboardsConfig = YamlConfiguration.loadConfiguration(scoreboardsPath.toFile());
        try {
            this.config = ArenaConfigParser.newInstance(scoreboardsPath, ScoreboardsConfig.class, scoreboardsConfig);
        } catch (ParseException e) {
            ParseException.handle(e);

            container.disable("Failed to parse scoreboards.yml!");
        }
    }

    @EventHandler
    public void onArenaInitialize(ArenaInitializeEvent event) {
        if (!event.getArena().isModuleEnabled(ID)) {
            return;
        }

        event.getArena().getEventManager().registerEvents(this);
    }

    public ScoreboardsConfig getConfig() {
        return this.config;
    }
}
