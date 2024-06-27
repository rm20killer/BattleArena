package org.battleplugins.arena.competition;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaLike;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.phase.CompetitionPhase;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.phase.LiveCompetitionPhase;
import org.battleplugins.arena.competition.phase.PhaseManager;
import org.battleplugins.arena.competition.team.TeamManager;
import org.battleplugins.arena.competition.victory.VictoryManager;
import org.battleplugins.arena.event.player.ArenaJoinEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.event.player.ArenaPreJoinEvent;
import org.battleplugins.arena.event.player.ArenaSpectateEvent;
import org.battleplugins.arena.options.ArenaOptionType;
import org.battleplugins.arena.options.TeamSelection;
import org.battleplugins.arena.options.Teams;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.team.ArenaTeams;
import org.battleplugins.arena.util.IntRange;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link Competition} that is occurring on the same server
 * that this plugin is running on.
 */
public abstract class LiveCompetition<T extends Competition<T>> implements ArenaLike, Competition<T> {
    private final Arena arena;
    private final LiveCompetitionMap<T> map;

    private final Map<Player, ArenaPlayer> players = new HashMap<>();
    private final Map<PlayerRole, Set<ArenaPlayer>> playersByRole = new HashMap<>();

    private final PhaseManager<T> phaseManager;
    private final TeamManager teamManager;
    private final VictoryManager<T> victoryManager;

    public LiveCompetition(Arena arena, LiveCompetitionMap<T> map) {
        this.arena = arena;
        this.map = map;

        this.phaseManager = new PhaseManager<>(arena, (T) this);
        this.teamManager = new TeamManager(this);
        this.victoryManager = new VictoryManager<>(arena, (T) this);

        arena.getEventManager().registerEvents(new CompetitionListener<>(this));
        arena.getEventManager().registerEvents(new OptionsListener<>(this));
        arena.getEventManager().registerEvents(new StatListener<>(this));

        // Set the initial phase
        CompetitionPhaseType<?, ?> initialPhase = arena.getInitialPhase();
        this.phaseManager.setPhase(initialPhase);
    }

    private ArenaPlayer createPlayer(Player player) {
        return new ArenaPlayer(player, this.arena, this);
    }

    @Override
    public CompletableFuture<JoinResult> canJoin(Player player, PlayerRole role) {
        CompetitionPhase<T> currentPhase = this.phaseManager.getCurrentPhase();

        // Check if the player can join the competition in its current state
        if (role == PlayerRole.PLAYING) {
            if (!currentPhase.canJoin()){
                return CompletableFuture.completedFuture(JoinResult.NOT_JOINABLE);
            }

            // See if the player will fit within the player limits
            Teams teams = this.arena.getTeams();
            List<ArenaTeam> availableTeams = teams.getAvailableTeams();
            IntRange teamSize = teams.getTeamSize();

            for (ArenaTeam availableTeam : availableTeams) {
                int playersOnTeam = this.teamManager.getNumberOfPlayersOnTeam(availableTeam);
                if (playersOnTeam >= teamSize.getMax()) {
                    if (teams.isNonTeamGame()) {
                        // If the team selection is none and the default team is available, then
                        // we can just put the player on the default team. But first, we need to
                        // check to see if the default team is full
                        int teamSizeMax = teamSize.getMax();
                        int teamAmount = teams.getTeamAmount().getMax();
                        if (teamAmount == Integer.MAX_VALUE || teamSizeMax == Integer.MAX_VALUE || playersOnTeam < teamAmount * teamSizeMax) {
                            // Not full - allow them through
                            return CompletableFuture.completedFuture(JoinResult.SUCCESS);
                        } else {
                            // No available teams - return false
                            return CompletableFuture.completedFuture(JoinResult.ARENA_FULL);
                        }
                    }
                }
            }
        }

        // Check if the player can spectate the competition in its current phase
        if (role == PlayerRole.SPECTATING && !currentPhase.canSpectate()) {
            return CompletableFuture.completedFuture(JoinResult.NOT_SPECTATABLE);
        }

        // Call the ArenaPreJoinEvent
        ArenaPreJoinEvent event = this.arena.getEventManager().callEvent(new ArenaPreJoinEvent(this.arena, this, role, JoinResult.SUCCESS, player));
        return CompletableFuture.completedFuture(event.getResult());
    }

    @Override
    public void join(Player player, PlayerRole type) {
        this.join(player, type, null);
    }

    public void join(Player player, PlayerRole type, @Nullable ArenaTeam team) {
        if (this.arena.getPlugin().isInArena(player)) {
            throw new IllegalStateException("Player is already in an arena!");
        }

        ArenaPlayer arenaPlayer = this.createPlayer(player);
        arenaPlayer.setRole(type);

        this.join(arenaPlayer, null);
    }

    private void join(ArenaPlayer player, @Nullable ArenaTeam team) {
        this.players.put(player.getPlayer(), player);
        this.playersByRole.computeIfAbsent(player.getRole(), e -> new HashSet<>()).add(player);

        if (team == null) {
            if (player.getRole() == PlayerRole.PLAYING) {
                this.findAndJoinTeamIfApplicable(player);
            }
        } else {
            this.teamManager.joinTeam(player, team);
        }

        if (player.getRole() == PlayerRole.PLAYING) {
            ArenaJoinEvent event = new ArenaJoinEvent(player);
            this.arena.getEventManager().callEvent(event);
        } else {
            ArenaSpectateEvent event = new ArenaSpectateEvent(player);
            this.arena.getEventManager().callEvent(event);
        }
    }

    @Override
    public void leave(Player player, ArenaLeaveEvent.Cause cause) {
        ArenaPlayer arenaPlayer = this.players.get(player);
        if (arenaPlayer == null) {
            return;
        }

        this.leave(arenaPlayer, cause);
    }

    public void leave(ArenaPlayer player, ArenaLeaveEvent.Cause cause) {
        this.players.remove(player.getPlayer());
        this.playersByRole.get(player.getRole()).remove(player);

        this.teamManager.leaveTeam(player);

        ArenaLeaveEvent event = new ArenaLeaveEvent(player, cause);
        this.arena.getEventManager().callEvent(event);

        player.remove();
    }

    public void findAndJoinTeamIfApplicable(ArenaPlayer player) {
        Teams teams = this.arena.getTeams();

        // If the team selection is none, then we can just put
        // the player on the default team
        if (teams.isNonTeamGame()) {
            this.teamManager.joinTeam(player, ArenaTeams.DEFAULT);
        }

        if (teams.getTeamSelection() == TeamSelection.RANDOM) {
            this.teamManager.joinTeam(player, this.teamManager.findSuitableTeam());
        }
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public LiveCompetitionMap<T> getMap() {
        return this.map;
    }

    @Override
    public CompetitionPhaseType<T, ?> getPhase() {
        return this.phaseManager.getCurrentPhase().getType();
    }

    public Set<ArenaPlayer> getPlayers() {
        return Collections.unmodifiableSet(this.playersByRole.getOrDefault(PlayerRole.PLAYING, Set.of()));
    }

    public Set<ArenaPlayer> getSpectators() {
        return Collections.unmodifiableSet(this.playersByRole.getOrDefault(PlayerRole.SPECTATING, Set.of()));
    }

    public PhaseManager<T> getPhaseManager() {
        return this.phaseManager;
    }

    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    public VictoryManager<T> getVictoryManager() {
        return this.victoryManager;
    }

    public <E extends org.battleplugins.arena.options.ArenaOption> Optional<E> option(ArenaOptionType<E> type) {
        return Optional.ofNullable(this.getOption(type));
    }

    @Nullable
    public <E extends org.battleplugins.arena.options.ArenaOption> E getOption(ArenaOptionType<E> type) {
        if (this.getPhaseManager().getCurrentPhase() instanceof LiveCompetitionPhase<?> livePhase) {
            return livePhase.getOption(type);
        }

        return this.arena.getOption(type);
    }
}
