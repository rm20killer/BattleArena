package org.battleplugins.arena.module.tournaments;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.module.ArenaModule;
import org.battleplugins.arena.module.ArenaModuleContainer;
import org.battleplugins.arena.module.ArenaModuleInitializer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A module that adds tournaments to BattleArena.
 */
@ArenaModule(id = Tournaments.ID, name = "Tournaments", description = "Adds tournaments to BattleArena.", authors = "BattlePlugins")
public class Tournaments implements ArenaModuleInitializer {
    public static final String ID = "tournaments";

    private final Map<Arena, Tournament> activeTournaments = new HashMap<>();
    private TournamentConfig config;

    public Tournaments() {
        // Register the tournament command
        BattleArena.getInstance().registerExecutor("tournament", new TournamentCommandExecutor("tournament", this), "tourney");
    }

    @EventHandler
    public void onPostInitialize(BattleArenaPostInitializeEvent event) {
        ArenaModuleContainer<Tournaments> container = event.getBattleArena()
                .<Tournaments>module(ID)
                .orElseThrow();

        Path dataFolder = event.getBattleArena().getDataFolder().toPath();
        Path tournamentPath = dataFolder.resolve("tournament-config.yml");
        if (Files.notExists(tournamentPath)) {
            InputStream inputStream = container.getResource("tournament-config.yml");
            try {
                Files.copy(inputStream, tournamentPath);
            } catch (Exception e) {
                event.getBattleArena().error("Failed to copy tournament-config.yml to data folder!", e);
                container.disable("Failed to copy tournament-config.yml to data folder!");
                return;
            }
        }

        Configuration tournamentConfig = YamlConfiguration.loadConfiguration(tournamentPath.toFile());
        try {
            this.config = ArenaConfigParser.newInstance(tournamentPath, TournamentConfig.class, tournamentConfig);
        } catch (ParseException e) {
            ParseException.handle(e);

            container.disable("Failed to parse tournament-config.yml!");
        }
    }

    public void createTournament(Arena arena) throws TournamentException {
        if (this.activeTournaments.containsKey(arena)) {
            throw new TournamentException(TournamentMessages.TOURNAMENT_ALREADY_EXISTS);
        }

        Tournament tournament = Tournament.createTournament(this, arena);
        this.activeTournaments.put(arena, tournament);
    }

    public void startTournament(Arena arena) throws TournamentException {
        Tournament tournament = this.activeTournaments.get(arena);
        if (tournament == null) {
            throw new TournamentException(TournamentMessages.TOURNAMENT_NOT_FOUND);
        }

        tournament.start();
    }

    public void endTournament(Arena arena) throws TournamentException {
        Tournament tournament = this.activeTournaments.get(arena);
        if (tournament == null) {
            throw new TournamentException(TournamentMessages.TOURNAMENT_NOT_FOUND);
        }

        tournament.finish(null);
    }

    void removeTournament(Arena arena) {
        this.activeTournaments.remove(arena);
    }

    @Nullable
    public Tournament getTournament(Arena arena) {
        return this.activeTournaments.get(arena);
    }

    public List<Tournament> getActiveTournaments() {
        return List.copyOf(this.activeTournaments.values());
    }

    public TournamentConfig getConfig() {
        return this.config;
    }
}
