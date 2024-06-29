package org.battleplugins.arena;

import org.battleplugins.arena.command.BACommandExecutor;
import org.battleplugins.arena.command.BaseCommandExecutor;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionResult;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.JoinResult;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.competition.event.EventOptions;
import org.battleplugins.arena.competition.event.EventScheduler;
import org.battleplugins.arena.competition.event.EventType;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapType;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.phase.phases.VictoryPhase;
import org.battleplugins.arena.config.ArenaConfigParser;
import org.battleplugins.arena.config.ParseException;
import org.battleplugins.arena.event.BattleArenaPostInitializeEvent;
import org.battleplugins.arena.event.BattleArenaPreInitializeEvent;
import org.battleplugins.arena.event.BattleArenaShutdownEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.messages.MessageLoader;
import org.battleplugins.arena.module.ArenaModuleContainer;
import org.battleplugins.arena.module.ArenaModuleLoader;
import org.battleplugins.arena.module.ModuleLoadException;
import org.battleplugins.arena.team.ArenaTeams;
import org.battleplugins.arena.util.CommandInjector;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
public class BattleArena extends JavaPlugin implements Listener {
    private static BattleArena instance;

    final Map<String, Class<? extends Arena>> arenaTypes = new HashMap<>();
    final Map<String, Arena> arenas = new HashMap<>();

    private final Map<Arena, List<LiveCompetitionMap<?>>> arenaMaps = new HashMap<>();
    private final Map<Arena, List<Competition<?>>> competitions = new HashMap<>();

    private final Map<String, ArenaLoader> arenaLoaders = new HashMap<>();

    private EventScheduler eventScheduler;

    private BattleArenaConfig config;
    private ArenaModuleLoader moduleLoader;
    private ArenaTeams teams;

    private Path arenasPath;
    private Path modulesPath;

    private boolean debugMode;

    @Override
    public void onLoad() {
        instance = this;

        Path dataFolder = this.getDataFolder().toPath();
        this.arenasPath = dataFolder.resolve("arenas");
        this.modulesPath = dataFolder.resolve("modules");

        this.moduleLoader = new ArenaModuleLoader(this, this.getClassLoader(), this.modulesPath);
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

        // Copy our default configs
        this.saveDefaultConfig();

        Configuration config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        this.config = ArenaConfigParser.newInstance(BattleArenaConfig.class, config);
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
        Configuration teamsConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "teams.yml"));
        this.teams = ArenaConfigParser.newInstance(ArenaTeams.class, teamsConfig, this);

        // Clear any remaining dynamic maps
        this.clearDynamicMaps();

        // Enable modules
        this.moduleLoader.enableModules();

        // Load messages
        MessageLoader.load(dataFolder.resolve("messages.yml"));

        // Register default arenas
        this.registerArena("Arena", Arena.class);

        this.eventScheduler = new EventScheduler();

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

        // Close all active competitions
        this.completeAllActiveCompetitions();

        // Stop all scheduled events
        this.eventScheduler.stopAllScheduledEvents();

        // Clear dynamic maps
        this.clearDynamicMaps();
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        // There is logic called later, however by this point all plugins
        // using the BattleArena API should have been loaded. As modules will
        // listen for this event to register their behavior, we need to ensure
        // they are fully initialized so any references to said modules in
        // arena config files will be valid.
        new BattleArenaPostInitializeEvent(this).callEvent();

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

                    // TODO: Call event in Arena.java
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

    public void addArenaMap(Arena arena, LiveCompetitionMap<?> map) {
        this.arenaMaps.computeIfAbsent(arena, k -> new ArrayList<>()).add(map);
    }

    public void removeArenaMap(Arena arena, LiveCompetitionMap<?> map) {
        this.arenaMaps.computeIfAbsent(arena, k -> new ArrayList<>()).remove(map);

        // If the map is removed, also remove the competition if applicable
        this.competitions.computeIfAbsent(arena, k -> new ArrayList<>()).removeIf(competition -> competition.getMap() == map);

        // Now remove the map from the file system
        Path mapPath = arena.getMapsPath().resolve(map.getName().toLowerCase(Locale.ROOT) + ".yml");
        try {
            Files.deleteIfExists(mapPath);
        } catch (IOException e) {
            this.error("Failed to delete map file for map {} in arena {}!", map.getName(), arena.getName(), e);
        }
    }

    public void addCompetition(Arena arena, Competition<?> competition) {
        this.competitions.computeIfAbsent(arena, k -> new ArrayList<>()).add(competition);
    }

    @SuppressWarnings("unchecked")
    public void removeCompetition(Arena arena, Competition<?> competition) {
        List<Competition<?>> competitions = this.competitions.get(arena);
        if (competitions == null) {
            return;
        }

        Set<CompetitionPhaseType<?, ?>> phases = arena.getPhases();

        // Check if we have a victory phase
        CompetitionPhaseType<?, VictoryPhase<?>> victoryPhase = null;
        for (CompetitionPhaseType<?, ?> phase : phases) {
            if (VictoryPhase.class.isAssignableFrom(phase.getPhaseType())) {
                victoryPhase = (CompetitionPhaseType<?, VictoryPhase<?>>) phase;
                break;
            }
        }

        boolean removed = competitions.remove(competition);
        if (removed && competition instanceof LiveCompetition<?> liveCompetition) {
            // De-reference any remaining resources
            liveCompetition.getVictoryManager().end(true);

            if (victoryPhase != null && !(VictoryPhase.class.isAssignableFrom(liveCompetition.getPhase().getPhaseType()))) {
                liveCompetition.getPhaseManager().setPhase(victoryPhase);

                VictoryPhase<?> phase = (VictoryPhase<?>) liveCompetition.getPhaseManager().getCurrentPhase();
                phase.onDraw(); // Mark as a draw
            } else {
                // No victory phase - just forcefully kick every player
                for (ArenaPlayer player : liveCompetition.getPlayers()) {
                    liveCompetition.leave(player, ArenaLeaveEvent.Cause.SHUTDOWN);
                }
            }
        }

        competitions.remove(competition);
        if (competition.getMap().getType() == MapType.DYNAMIC && competition.getMap() instanceof LiveCompetitionMap<?> map) {
            this.clearDynamicMap(map);
        }
    }

    public Path getMapsPath() {
        return this.getDataFolder().toPath().resolve("maps");
    }

    private void completeAllActiveCompetitions() {
        for (Map.Entry<Arena, List<Competition<?>>> entry : Map.copyOf(this.competitions).entrySet()) {
            for (Competition<?> competition : List.copyOf(entry.getValue())) {
                this.removeCompetition(entry.getKey(), competition);
            }
        }
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
                        LiveCompetitionMap<?> map = ArenaConfigParser.newInstance(arena.getCompetitionMapType(), configuration, this);
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
                        this.error("An error occurred when loading competition for arena {}: {}", arenaName, e.getMessage(), e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Error loading maps for arena " + arenaName, e);
            }
        }
    }

    public boolean isInArena(Player player) {
        return ArenaPlayer.getArenaPlayer(player) != null;
    }

    @Nullable
    public Arena getArena(String name) {
        return this.arenas.get(name);
    }

    public List<LiveCompetitionMap<?>> getMaps(Arena arena) {
        List<LiveCompetitionMap<?>> maps = this.arenaMaps.get(arena);
        if (maps == null) {
            return List.of();
        }

        return List.copyOf(maps);
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

    public List<Competition<?>> getCompetitions(Arena arena) {
        return List.copyOf(this.competitions.getOrDefault(arena, List.of()));
    }

    public List<Competition<?>> getCompetitions(Arena arena, String name) {
        List<Competition<?>> competitions = BattleArena.getInstance().getCompetitions(arena);
        return competitions.stream()
                .filter(competition -> competition.getMap().getName().equals(name))
                .toList();
    }

    public CompletableFuture<CompetitionResult> getOrCreateCompetition(Arena arena, Player player, PlayerRole role, @Nullable String name) {
        // See if we can join any already open competitions
        List<Competition<?>> openCompetitions = this.getCompetitions(arena, name);
        CompletableFuture<CompetitionResult> joinableCompetition = this.findJoinableCompetition(openCompetitions, player, role);
        return joinableCompetition.thenApplyAsync(result -> {
            if (result.competition() != null) {
                return result;
            }

            CompetitionResult invalidResult = new CompetitionResult(null, !result.result().canJoin() ? result.result() : JoinResult.NOT_JOINABLE);
            if (arena.getType() == CompetitionType.EVENT) {
                // Cannot create non-requested dynamic competitions for events
                return invalidResult;
            }

            List<LiveCompetitionMap<?>> maps = this.arenaMaps.get(arena);
            if (maps == null) {
                // No maps, return
                return invalidResult;
            }

            // Ensure we have WorldEdit installed
            if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
                this.error("WorldEdit is required to create dynamic competitions! Not proceeding with creating a new dynamic competition.");
                return invalidResult;
            }

            // Check if we have exceeded the maximum number of dynamic maps
            List<Competition<?>> allCompetitions = this.getCompetitions(arena);
            long dynamicMaps = allCompetitions.stream()
                    .map(Competition::getMap)
                    .filter(map -> map.getType() == MapType.DYNAMIC)
                    .count();

            if (dynamicMaps >= this.config.getMaxDynamicMaps() && this.config.getMaxDynamicMaps() != -1) {
                this.warn("Exceeded maximum number of dynamic maps for arena {}! Not proceeding with creating a new dynamic competition.", arena.getName());
                return invalidResult;
            }

            // Create a new competition if possible

            if (name == null) {
                // Shuffle results if map name is not requested
                maps = new ArrayList<>(maps);
                Collections.shuffle(maps);
            }

            for (LiveCompetitionMap<?> map : maps) {
                if (map.getType() != MapType.DYNAMIC) {
                    continue;
                }

                if ((name == null || map.getName().equals(name))) {
                    Competition<?> competition = map.createDynamicCompetition(arena);
                    if (competition == null) {
                        this.warn("Failed to create dynamic competition for map {} in arena {}!", map.getName(), arena.getName());
                        continue;
                    }

                    this.addCompetition(arena, competition);

                    // TODO: Call event in Arena.java
                    return new CompetitionResult(competition, JoinResult.SUCCESS);
                }
            }

            // No open competitions found or unable to create a new one
            return invalidResult;
        }, Bukkit.getScheduler().getMainThreadExecutor(this));
    }

    public CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role) {
        return this.findJoinableCompetition(competitions, player, role, null);
    }

    private CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role, @Nullable JoinResult lastResult) {
        if (competitions.isEmpty()) {
            return CompletableFuture.completedFuture(new CompetitionResult(null, lastResult == null ? JoinResult.NOT_JOINABLE : lastResult));
        }

        Competition<?> competition = competitions.get(0);
        CompletableFuture<JoinResult> result = competition.canJoin(player, role);
        JoinResult joinResult = result.join();
        if (joinResult == JoinResult.SUCCESS) {
            return CompletableFuture.completedFuture(new CompetitionResult(competition, JoinResult.SUCCESS));
        } else {
            List<Competition<?>> remainingCompetitions = new ArrayList<>(competitions);
            remainingCompetitions.remove(competition);

            return this.findJoinableCompetition(remainingCompetitions, player, role, joinResult);
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

    @Nullable
    public <T> ArenaModuleContainer<T> getModule(String id) {
        return this.moduleLoader.getModule(id);
    }

    public <T> Optional<ArenaModuleContainer<T>> module(String id) {
        return Optional.ofNullable(this.getModule(id));
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

    private void clearDynamicMap(LiveCompetitionMap<?> map) {
        if (map.getType() != MapType.DYNAMIC) {
            return;
        }

        Bukkit.unloadWorld(map.getWorld(), false);

        try {
            try (Stream<Path> pathsToDelete = Files.walk(map.getWorld().getWorldFolder().toPath())) {
                for (Path path : pathsToDelete.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        } catch (IOException e) {
            this.error("Failed to delete dynamic map {}", map.getName(), e);
        }
    }

    public boolean isDebugMode() {
        return this.debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public void info(String message, Object... args) {
        this.getSLF4JLogger().info(message, args);
    }

    public void error(String message, Object... args) {
        this.getSLF4JLogger().error(message, args);
    }

    public void warn(String message, Object... args) {
        this.getSLF4JLogger().warn(message, args);
    }

    public void debug(String message, Object... args) {
        if (this.debugMode) {
            this.getSLF4JLogger().info("[DEBUG] " + message, args);
        }
    }

    public static BattleArena getInstance() {
        return instance;
    }
}
