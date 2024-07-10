package org.battleplugins.arena;

import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapFactory;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.victory.VictoryConditionType;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.config.Scoped;
import org.battleplugins.arena.config.context.EventContextProvider;
import org.battleplugins.arena.config.context.OptionContextProvider;
import org.battleplugins.arena.config.context.PhaseContextProvider;
import org.battleplugins.arena.config.context.VictoryConditionContextProvider;
import org.battleplugins.arena.event.ArenaEventManager;
import org.battleplugins.arena.event.ArenaEventType;
import org.battleplugins.arena.event.ArenaListener;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.options.ArenaOptionType;
import org.battleplugins.arena.options.Lives;
import org.battleplugins.arena.options.Teams;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a gamemode within BattleArena.
 * <p>
 * An Arena is not an active representation of a game. Instead, it is
 * the place in which game logic is configured and where events are
 * processed.
 * <p>
 * The {@link Competition} is the location in which all the lifecycle and
 * active game actions will occur (i.e. game timer, score, etc.)
 */
@DocumentationSource("https://docs.battleplugins.org/books/user-guide/chapter/configuration")
public class Arena implements ArenaLike, ArenaListener, Resolvable {

    @Scoped
    private BattleArena plugin;

    @ArenaOption(name = "name", description = "The name of the game.", required = true)
    private String name;

    @ArenaOption(name = "aliases", description = "Aliases for command arguments.")
    private List<String> aliases;

    @ArenaOption(name = "type", description = "The competition type", required = true)
    private CompetitionType type;

    @ArenaOption(name = "team-options", description = "The options for teams.", required = true)
    private Teams teams;

    @ArenaOption(name = "lives", description = "Lives settings.")
    private Lives lives;

    @ArenaOption(name = "initial-phase", description = "The initial phase of the game.", required = true)
    private CompetitionPhaseType<?, ?> initialPhase;

    @ArenaOption(
            name = "phases",
            description = "The game phase configurations.",
            required = true,
            contextProvider = PhaseContextProvider.class
    )
    private Map<CompetitionPhaseType<?, ?>, CompetitionPhaseType.Provider<?, ?>> phases;

    @ArenaOption(
            name = "events",
            description = "The events actions to be performed during the game.",
            required = true,
            contextProvider = EventContextProvider.class
    )
    private Map<ArenaEventType<?>, List<EventAction>> eventActions;

    @ArenaOption(
            name = "options",
            description = "The options for the game.",
            contextProvider = OptionContextProvider.class
    )
    private Map<ArenaOptionType<?>, org.battleplugins.arena.options.ArenaOption> options;

    @ArenaOption(
            name = "victory-conditions",
            description = "The conditions to determine when the game ends",
            contextProvider = VictoryConditionContextProvider.class
    )
    private Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> victoryConditions;

    @ArenaOption(name = "modules", description = "The modules to enable for this arena.")
    private List<String> modules;

    private final ArenaEventManager eventManager;

    public Arena() {
        this.eventManager = new ArenaEventManager(this);
    }

    // API methods

    /**
     * Creates a new command executor for this arena.
     *
     * @return a new command executor for this arena
     */
    public ArenaCommandExecutor createCommandExecutor() {
        return new ArenaCommandExecutor(this);
    }

    /**
     * Returns the {@link MapFactory} for this arena.
     * <p>
     * This factory controls the creation of maps for this
     * arena, and can be overridden to provide custom map
     * creation logic.
     *
     * @return the map factory for this arena
     */
    public MapFactory getMapFactory() {
        return LiveCompetitionMap.getFactory();
    }

    /**
     * Returns whether the given module is enabled for this arena.
     *
     * @param module the module to check
     * @return whether the given module is enabled for this arena
     */
    public boolean isModuleEnabled(String module) {
        return this.modules != null && this.modules.contains(module);
    }

    // Internal methods (cannot be overridden by extending plugins)

    /**
     * Creates a new phase for the given competition.
     *
     * @param type the phase type
     * @param competition the competition
     * @return the new phase
     * @param <C> the type of competition
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public final <C extends Competition<C>> CompetitionPhase<C> createPhase(CompetitionPhaseType<?, ?> type, C competition) {
        CompetitionPhaseType.Provider<?, ?> provider = this.phases.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for phase type " + type.getPhaseType());
        }

        return ((CompetitionPhaseType.Provider) provider).create(competition);
    }

    /**
     * Gets the {@link CompetitionPhaseType phases} for this arena.
     *
     * @return the phases for this arena
     */
    public final Set<CompetitionPhaseType<?, ?>> getPhases() {
        return Set.copyOf(this.phases.keySet());
    }

    /**
     * Gets the path to the map for this arena.
     *
     * @return the path to the map for this arena
     */
    public final Path getMapPath() {
        return this.plugin.getMapsPath().resolve(this.name.toLowerCase(Locale.ROOT));
    }

    /**
     * Gets the plugin associated with this arena.
     *
     * @return the plugin associated with this arena
     */
    public final BattleArena getPlugin() {
        return this.plugin;
    }

    /**
     * Gets the name of this arena.
     *
     * @return the name of this arena
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the command aliases for this arena.
     *
     * @return the aliases for this arena
     */
    public List<String> getAliases() {
        return this.aliases == null ? List.of() : List.copyOf(this.aliases);
    }

    /**
     * Gets the {@link CompetitionType} for this arena.
     *
     * @return the competition type for this arena
     */
    public final CompetitionType getType() {
        return this.type;
    }

    /**
     * Gets the {@link Teams} for this arena.
     *
     * @return the teams for this arena
     */
    public final Teams getTeams() {
        return this.teams;
    }

    /**
     * Gets the {@link Lives} for this arena.
     *
     * @return the lives for this arena
     */
    public final Optional<Lives> lives() {
        return Optional.ofNullable(this.lives);
    }

    /**
     * Gets the {@link Lives} for this arena.
     *
     * @return the lives for this arena, or null if lives are not enabled
     */
    @Nullable
    public final Lives getLives() {
        return this.lives;
    }

    /**
     * Returns whether lives are enabled for this arena.
     *
     * @return whether lives are enabled for this arena
     */
    public final boolean isLivesEnabled() {
        return this.lives != null && this.lives.isEnabled();
    }

    /**
     * Gets the initial {@link CompetitionPhaseType phase} for this arena.
     *
     * @return the initial phase for this arena
     */
    public final CompetitionPhaseType<?, ?> getInitialPhase() {
        return this.initialPhase;
    }

    /**
     * Gets the {@link EventAction actions} for this arena.
     *
     * @return the event actions for this arena
     */
    public final Map<ArenaEventType<?>, List<EventAction>> getEventActions() {
        return this.eventActions;
    }

    /**
     * Gets the {@link VictoryConditionType victory conditions} for this arena.
     *
     * @return the victory conditions for this arena
     */
    public final Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> getVictoryConditions() {
        return this.victoryConditions;
    }

    /**
     * Gets the {@link ArenaEventManager} for this arena.
     *
     * @return the event manager for this arena
     */
    public final ArenaEventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * Gets the {@link org.battleplugins.arena.options.ArenaOption} of the specified type.
     *
     * @param type the type of option
     * @param <E> the type of option
     * @return the option of the specified type
     */
    public <E extends org.battleplugins.arena.options.ArenaOption> Optional<E> option(ArenaOptionType<E> type) {
        return Optional.ofNullable(this.getOption(type));
    }

    /**
     * Gets the {@link org.battleplugins.arena.options.ArenaOption} of the specified type.
     *
     * @param type the type of option
     * @param <E> the type of option
     * @return the option of the specified type, or null if the option does not exist
     */
    @Nullable
    public final <E extends org.battleplugins.arena.options.ArenaOption> E getOption(ArenaOptionType<E> type) {
        if (this.options == null) {
            return null;
        }

        return (E) this.options.get(type);
    }

    @Override
    public final Arena getArena() {
        return this;
    }

    @Override
    public Resolver resolve() {
        return Resolver.builder()
                .define(ResolverKeys.ARENA, ResolverProvider.simple(this, Arena::getName))
                .build();
    }
}
