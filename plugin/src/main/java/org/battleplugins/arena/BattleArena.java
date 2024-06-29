package org.battleplugins.arena;

import org.battleplugins.arena.command.BACommandExecutor;
import org.battleplugins.arena.command.BaseCommandExecutor;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionManager;
import org.battleplugins.arena.competition.CompetitionResult;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.competition.event.EventOptions;
import org.battleplugins.arena.competition.event.EventScheduler;
import org.battleplugins.arena.competition.event.EventType;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapType;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.BattleArenaPreInitializeEvent;
import org.battleplugins.arena.event.BattleArenaReloadEvent;
import org.battleplugins.arena.event.BattleArenaReloadedEvent;
import org.battleplugins.arena.event.BattleArenaShutdownEvent;
import org.battleplugins.arena.messages.MessageLoader;
import org.battleplugins.arena.module.ArenaModuleContainer;
import org.battleplugins.arena.module.ArenaModuleLoader;
import org.battleplugins.arena.module.ModuleLoadException;
import org.battleplugins.arena.team.ArenaTeams;
import org.battleplugins.arena.util.CommandInjector;
import org.battleplugins.arena.util.LoggerHolder;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The main class for BattleArena.
 */
public class BattleArena extends JavaPlugin implements Listener, LoggerHolder {
    private static BattleArena instance;

    final Map<String, Class<? extends Arena>> arenaTypes = new HashMap<>();
    final Map<String, Arena> arenas = new HashMap<>();

    private final Map<Arena, List<LiveCompetitionMap<?>>> arenaMaps = new HashMap<>();
    private final Map<String, ArenaLoader> arenaLoaders = new HashMap<>();

    private final CompetitionManager competitionManager = new CompetitionManager(this);
    private final EventScheduler eventScheduler = new EventScheduler();

    private BattleArenaConfig config;
    private ArenaModuleLoader moduleLoader;
    private ArenaTeams teams;

    private Path arenasPath;

    private boolean debugMode;

    @Override
    public void onLoad() {
        instance = this;

        Path dataFolder = this.getDataFolder().toPath();
        this.arenasPath = dataFolder.resolve("arenas");
        Path modulesPath = dataFolder.resolve("modules");

        this.moduleLoader = new ArenaModuleLoader(this, this.getClassLoader(), modulesPath);
        try {
            this.moduleLoader.loadModules();
        } catch (IOException e) {
            this.error("An error occurred loading modules!", e);
        }

        new BattleArenaPreInitializeEvent(this).callEvent();
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        this.enable();
    }

    private void enable() {
        // Copy our default configs
        this.saveDefaultConfig();

        File configFile = new File(this.getDataFolder(), "config.yml");
        Configuration config = YamlConfiguration.loadConfiguration(configFile);
        try {
            this.config = ArenaConfigParser.newInstance(configFile.toPath(), BattleArenaConfig.class, config);
        } catch (ParseException e) {
            ParseException.handle(e);

            this.error("Failed to load BattleArena configuration! Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.debugMode = this.config.isDebugMode();

        if (Files.notExists(this.arenasPath)) {
            this.saveResource("arenas/arena.yml", false);
            this.saveResource("arenas/battlegrounds.yml", false);
            this.saveResource("arenas/colosseum.yml", false);
            this.saveResource("arenas/deathmatch.yml", false);
            this.saveResource("arenas/ffa.yml", false);
            this.saveResource("arenas/skirmish.yml", false);
        }

        Path dataFolder = this.getDataFolder().toPath();
        if (Files.notExists(dataFolder.resolve("messages.yml"))) {
            this.saveResource("messages.yml", false);
        }

        if (Files.notExists(dataFolder.resolve("teams.yml"))) {
            this.saveResource("teams.yml", false);
        }

        // Load teams
        File teamsFile = new File(this.getDataFolder(), "teams.yml");
        Configuration teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
        try {
            this.teams = ArenaConfigParser.newInstance(teamsFile.toPath(), ArenaTeams.class, teamsConfig, this);
        } catch (ParseException e) {
            ParseException.handle(e);

            this.error("Failed to load teams configuration! Disabling plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Clear any remaining dynamic maps
        this.clearDynamicMaps();

        // Enable modules
        this.moduleLoader.enableModules();

        // Load messages
        MessageLoader.load(dataFolder.resolve("messages.yml"));

        // Register default arenas
        this.registerArena("Arena", Arena.class);

        // Create arena loaders
        try (Stream<Path> arenaPaths = Files.walk(this.arenasPath)) {
            arenaPaths.forEach(arenaPath -> {
                try {
                    if (Files.isDirectory(arenaPath)) {
                        return;
                    }

                    Configuration configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(arenaPath));
                    String name = configuration.getString("name");
                    if (name == null) {
                        this.info("Arena {} does not have a name!", arenaPath.getFileName());
                        return;
                    }

                    String mode = configuration.getString("mode", name);
                    List<String> aliases = configuration.getStringList("aliases");
                    ArenaLoader arenaLoader = new ArenaLoader(this, mode, configuration, arenaPath);
                    this.arenaLoaders.put(name, arenaLoader);

                    // Because Bukkit locks its command map upon startup, we need to
                    // add our plugin commands here, but populating the executor
                    // can happen at any time. This also means that Arenas can specify
                    // their own executors if they so please.
                    CommandInjector.inject(name, name.toLowerCase(Locale.ROOT), aliases.toArray(String[]::new));
                } catch (IOException e) {
                    throw new RuntimeException("Error reading arena config", e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Error walking arenas path!", e);
        }

        // Register base command
        PluginCommand command = this.getCommand("battlearena");
        if (command == null) {
            throw new IllegalArgumentException("Failed to register command 'battlearena'. Was it not registered?");
        }

        command.setExecutor(new BACommandExecutor("battlearena"));
    }

    @Override
    public void onDisable() {
        new BattleArenaShutdownEvent(this).callEvent();

        this.disable();
    }

    private void disable() {
        // Close all active competitions
        this.competitionManager.completeAllActiveCompetitions();

        // Stop all scheduled events
        this.eventScheduler.stopAllEvents();

        // Clear dynamic maps
        this.clearDynamicMaps();

        this.arenaTypes.clear();
        for (Arena arena : this.arenas.values()) {
            arena.getEventManager().unregisterAll();
        }

        this.arenas.clear();
        this.arenaMaps.clear();
        this.arenaLoaders.clear();

        this.config = null;
        this.teams = null;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        // There is logic called later, however by this point all plugins
        // using the BattleArena API should have been loaded. As modules will
        // listen for this event to register their behavior, we need to ensure
        // they are fully initialized so any references to said modules in
        // arena config files will be valid.
        new BattleArenaPostInitializeEvent(this).callEvent();

        this.postInitialize();
    }

    private void postInitialize() {
        // Load all arenas
        this.loadArenas();

        // Load the arena maps
        this.loadArenaMaps();

        // Initialize matches
        for (Map.Entry<Arena, List<LiveCompetitionMap<?>>> entry : this.arenaMaps.entrySet()) {
            if (entry.getKey().getType() != CompetitionType.MATCH) {
                continue;
            }

            for (LiveCompetitionMap<?> map : entry.getValue()) {
                if (map.getType() == MapType.STATIC) {
                    Competition<?> competition = map.createCompetition(entry.getKey());
                    this.addCompetition(entry.getKey(), competition);
                }
            }
        }

        // Initialize events
        for (Map.Entry<String, List<EventOptions>> entry : this.config.getEvents().entrySet()) {
            Arena arena = this.arenas.get(entry.getKey());
            if (arena == null) {
                this.warn("Event {} does not have a valid arena!", entry.getKey());
                continue;
            }

            for (EventOptions options : entry.getValue()) {
                if (options.getType() != EventType.SCHEDULED) {
                    continue; // We are not interested in starting manual events
                }

                this.eventScheduler.scheduleEvent(arena, options);
                this.info("Scheduled event for arena {} in {}m.", arena.getName(), options.getInterval());
            }
        }
    }

    public void reload() {
        new BattleArenaReloadEvent(this).callEvent();

        this.disable();
        this.enable();
        this.postInitialize();

        new BattleArenaReloadedEvent(this).callEvent();
    }

    public boolean isInArena(Player player) {
        return ArenaPlayer.getArenaPlayer(player) != null;
    }

    public Optional<Arena> arena(String name) {
        return Optional.ofNullable(this.arenas.get(name));
    }

    @Nullable
    public Arena getArena(String name) {
        return this.arenas.get(name);
    }

    public <T extends Arena> void registerArena(String name, Class<T> arena) {
        this.registerArena(name, arena, () -> {
            try {
                return arena.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("Failed to instantiate arena " + arena.getName(), e);
            }
        });
    }

    public <T extends Arena> void registerArena(String name, Class<T> arenaClass, Supplier<T> arenaFactory) {
        ArenaConfigParser.registerFactory(arenaClass, arenaFactory);

        this.arenaTypes.put(name, arenaClass);
    }

    public List<LiveCompetitionMap<?>> getMaps(Arena arena) {
        List<LiveCompetitionMap<?>> maps = this.arenaMaps.get(arena);
        if (maps == null) {
            return List.of();
        }

        return List.copyOf(maps);
    }

    public Optional<LiveCompetitionMap<?>> map(Arena arena, String name) {
        return Optional.ofNullable(this.getMap(arena, name));
    }

    @Nullable
    public LiveCompetitionMap<?> getMap(Arena arena, String name) {
        List<LiveCompetitionMap<?>> maps = this.arenaMaps.get(arena);
        if (maps == null) {
            return null;
        }

        return maps.stream()
                .filter(map -> map.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void addArenaMap(Arena arena, LiveCompetitionMap<?> map) {
        this.arenaMaps.computeIfAbsent(arena, k -> new ArrayList<>()).add(map);
    }

    public void removeArenaMap(Arena arena, LiveCompetitionMap<?> map) {
        this.arenaMaps.computeIfAbsent(arena, k -> new ArrayList<>()).remove(map);

        // If the map is removed, also remove the competition if applicable
        for (Competition<?> competition : this.competitionManager.getCompetitions(arena)) {
            if (competition.getMap() == map) {
                this.competitionManager.removeCompetition(arena, competition);
            }
        }

        // Now remove the map from the file system
        Path mapPath = arena.getMapsPath().resolve(map.getName().toLowerCase(Locale.ROOT) + ".yml");
        try {
            Files.deleteIfExists(mapPath);
        } catch (IOException e) {
            this.error("Failed to delete map file for map {} in arena {}!", map.getName(), arena.getName(), e);
        }
    }

    public List<Competition<?>> getCompetitions(Arena arena) {
        return this.competitionManager.getCompetitions(arena);
    }

    public List<Competition<?>> getCompetitions(Arena arena, String name) {
        return this.competitionManager.getCompetitions(arena, name);
    }

    public CompletableFuture<CompetitionResult> getOrCreateCompetition(Arena arena, Player player, PlayerRole role, @Nullable String name) {
        return this.competitionManager.getOrCreateCompetition(arena, player, role, name);
    }

    public CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role) {
        return this.competitionManager.findJoinableCompetition(competitions, player, role);
    }

    public void addCompetition(Arena arena, Competition<?> competition) {
        this.competitionManager.addCompetition(arena, competition);
    }

    public void removeCompetition(Arena arena, Competition<?> competition) {
        this.competitionManager.removeCompetition(arena, competition);
    }

    public Path getMapsPath() {
        return this.getDataFolder().toPath().resolve("maps");
    }

    private void loadArenas() {
        // Register our arenas once ALL the plugins have loaded. This ensures that
        // all custom plugins adding their own arena types have been loaded.
        for (ArenaLoader value : this.arenaLoaders.values()) {
            try {
                value.load();
            } catch (Exception e) {
                this.error("An error occurred when loading arena {}: {}", value.arenaPath().getFileName(), e.getMessage(), e);
            }
        }
    }

    private void loadArenaMaps() {
        // All the arenas have been loaded, now we can load the maps
        Path mapsPath = this.getMapsPath();
        if (Files.notExists(mapsPath)) {
            // No maps to load
            return;
        }

        // Check to see if there are any maps to load
        for (Map.Entry<String, Arena> entry : this.arenas.entrySet()) {
            String arenaName = entry.getKey();
            Arena arena = entry.getValue();

            Path arenaMapPath = mapsPath.resolve(arenaName.toLowerCase(Locale.ROOT));
            if (Files.notExists(arenaMapPath)) {
                continue;
            }

            try (Stream<Path> mapPaths = Files.walk(arenaMapPath)) {
                mapPaths.forEach(mapPath -> {
                    if (Files.isDirectory(mapPath)) {
                        return;
                    }

                    try {
                        Configuration configuration = YamlConfiguration.loadConfiguration(Files.newBufferedReader(mapPath));
                        LiveCompetitionMap<?> map = ArenaConfigParser.newInstance(mapPath, arena.getCompetitionMapType(), configuration, this);
                        if (map.getBounds() == null && map.getType() == MapType.DYNAMIC) {
                            // Cannot create dynamic map without bounds
                            this.warn("Map {} for arena {} is dynamic but does not have bounds!", map.getName(), arena.getName());
                            return;
                        }

                        this.addArenaMap(arena, map);
                        this.info("Loaded map {} for arena {}.", map.getName(), arena.getName());
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading competition config", e);
                    } catch (ParseException e) {
                        ParseException.handle(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error loading maps for arena " + arenaName, e);
            }
        }
    }

    public EventScheduler getEventScheduler() {
        return this.eventScheduler;
    }

    public BattleArenaConfig getMainConfig() {
        return this.config;
    }

    public ArenaTeams getTeams() {
        return this.teams;
    }

    public <T> Optional<ArenaModuleContainer<T>> module(String id) {
        return Optional.ofNullable(this.getModule(id));
    }

    @Nullable
    public <T> ArenaModuleContainer<T> getModule(String id) {
        return this.moduleLoader.getModule(id);
    }

    public List<ArenaModuleContainer<?>> getModules() {
        return this.moduleLoader.getModules();
    }

    public Set<ModuleLoadException> getFailedModules() {
        return this.moduleLoader.getFailedModules();
    }

    public void registerExecutor(String name, BaseCommandExecutor executor, String... aliases) {
        PluginCommand command = CommandInjector.inject(name, name.toLowerCase(Locale.ROOT), aliases);
        command.setExecutor(executor);
    }

    public Path getBackupPath(String type) {
        return this.getDataFolder().toPath().resolve("backups").resolve(type);
    }

    private void clearDynamicMaps() {
        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.isDirectory() && file.getName().startsWith("ba-dynamic")) {
                try {
                    try (Stream<Path> pathsToDelete = Files.walk(file.toPath())) {
                        for (Path path : pathsToDelete.sorted(Comparator.reverseOrder()).toList()) {
                            Files.deleteIfExists(path);
                        }
                    }
                } catch (IOException e) {
                    this.error("Failed to delete dynamic map {}", file.getName(), e);
                }
            }
        }
    }

    @Override
    public boolean isDebugMode() {
        return this.debugMode;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public @NotNull Logger getSLF4JLogger() {
        return super.getSLF4JLogger();
    }

    public static BattleArena getInstance() {
        return instance;
    }
}
