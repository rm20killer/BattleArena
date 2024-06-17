package org.battleplugins.arena;

import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.victory.VictoryConditionType;
import org.battleplugins.arena.config.ArenaOption;
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
public class Arena implements ArenaLike, ArenaListener {

    @Scoped
    private BattleArena plugin;

    @ArenaOption(name = "name", description = "The name of the game.", required = true)
    private String name;

    @ArenaOption(name = "type", description = "The competition type", required = true)
    private CompetitionType<?> type;

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
        this.eventManager.registerEvents(this);
    }

    // API methods

    public ArenaCommandExecutor createCommandExecutor() {
        return new ArenaCommandExecutor(this);
    }

    public Class<? extends LiveCompetitionMap> getCompetitionMapType() {
        return LiveCompetitionMap.class;
    }

    public boolean isModuleEnabled(String module) {
        return this.modules != null && this.modules.contains(module);
    }

    // Internal methods (cannot be overridden by extending plugins)

    @SuppressWarnings({"rawtypes", "unchecked"})
    public final <C extends Competition<C>> CompetitionPhase<C> createPhase(CompetitionPhaseType<?, ?> type, C competition) {
        CompetitionPhaseType.Provider<?, ?> provider = this.phases.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for phase type " + type.getPhaseType());
        }

        return ((CompetitionPhaseType.Provider) provider).create(competition);
    }

    public final Set<CompetitionPhaseType<?, ?>> getPhases() {
        return this.phases.keySet();
    }

    public final Path getMapsPath() {
        return this.plugin.getMapsPath().resolve(this.name.toLowerCase(Locale.ROOT));
    }

    public final BattleArena getPlugin() {
        return this.plugin;
    }

    public final String getName() {
        return this.name;
    }

    public final CompetitionType<?> getType() {
        return this.type;
    }

    public final Teams getTeams() {
        return this.teams;
    }

    @Nullable
    public final Lives getLives() {
        return this.lives;
    }

    public final Optional<Lives> lives() {
        return Optional.ofNullable(this.lives);
    }

    public final boolean isLivesEnabled() {
        return this.lives != null && this.lives.isEnabled();
    }

    public final CompetitionPhaseType<?, ?> getInitialPhase() {
        return this.initialPhase;
    }

    public final Map<ArenaEventType<?>, List<EventAction>> getEventActions() {
        return this.eventActions;
    }

    public final Map<VictoryConditionType<?, ?>, VictoryConditionType.Provider<?, ?>> getVictoryConditions() {
        return this.victoryConditions;
    }

    public final ArenaEventManager getEventManager() {
        return this.eventManager;
    }

    @Nullable
    public final <E extends org.battleplugins.arena.options.ArenaOption> E getOption(ArenaOptionType<E> type) {
        if (this.options == null) {
            return null;
        }

        return (E) this.options.get(type);
    }

    public <E extends org.battleplugins.arena.options.ArenaOption> Optional<E> option(ArenaOptionType<E> type) {
        return Optional.ofNullable(this.getOption(type));
    }

    @Override
    public final Arena getArena() {
        return this;
    }
}
