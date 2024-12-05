package org.battleplugins.arena.competition;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapType;
import org.battleplugins.arena.competition.phase.CompetitionPhaseType;
import org.battleplugins.arena.competition.phase.phases.VictoryPhase;
import org.battleplugins.arena.event.arena.ArenaCreateCompetitionEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CompetitionManager {
    private final Map<Arena, List<Competition<?>>> competitions = new HashMap<>();

    private final BattleArena plugin;

    public CompetitionManager(BattleArena plugin) {
        this.plugin = plugin;
    }

    public List<Competition<?>> getCompetitions(Arena arena) {
        List<Competition<?>> competitions = this.competitions.get(arena);
        return competitions == null ? List.of() : List.copyOf(competitions);
    }

    public List<Competition<?>> getCompetitions(Arena arena, String name) {
        List<Competition<?>> competitions = this.getCompetitions(arena);
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

            List<LiveCompetitionMap> maps = this.plugin.getMaps(arena);
            if (maps == null) {
                // No maps, return
                return invalidResult;
            }

            // Ensure we have WorldEdit installed
            if (this.plugin.getServer().getPluginManager().getPlugin("WorldEdit") == null) {
                this.plugin.error("WorldEdit is required to create dynamic competitions! Not proceeding with creating a new dynamic competition.");
                return invalidResult;
            }

            // Check if we have exceeded the maximum number of dynamic maps
            List<Competition<?>> allCompetitions = this.getCompetitions(arena);
            long dynamicMaps = allCompetitions.stream()
                    .map(Competition::getMap)
                    .filter(map -> map.getType() == MapType.DYNAMIC)
                    .count();

            if (dynamicMaps >= this.plugin.getMainConfig().getMaxDynamicMaps() && this.plugin.getMainConfig().getMaxDynamicMaps() != -1) {
                this.plugin.warn("Exceeded maximum number of dynamic maps for arena {}! Not proceeding with creating a new dynamic competition.", arena.getName());
                return invalidResult;
            }

            // Create a new competition if possible

            if (name == null) {
                // Shuffle results if map name is not requested
                maps = new ArrayList<>(maps);
                Collections.shuffle(maps);
            }

            for (LiveCompetitionMap map : maps) {
                if (map.getType() != MapType.DYNAMIC) {
                    continue;
                }

                if ((name == null || map.getName().equals(name))) {
                    Competition<?> competition = map.createDynamicCompetition(arena);
                    if (competition == null) {
                        this.plugin.warn("Failed to create dynamic competition for map {} in arena {}!", map.getName(), arena.getName());
                        continue;
                    }

                    this.addCompetition(arena, competition);
                    return new CompetitionResult(competition, JoinResult.SUCCESS);
                }
            }

            // No open competitions found or unable to create a new one
            return invalidResult;
        }, Bukkit.getScheduler().getMainThreadExecutor(this.plugin));
    }

    public CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role) {
        return this.findJoinableCompetition(competitions, player, role, null);
    }

    private CompletableFuture<CompetitionResult> findJoinableCompetition(List<Competition<?>> competitions, Player player, PlayerRole role, @Nullable JoinResult lastResult) {
        if (competitions.isEmpty()) {
            return CompletableFuture.completedFuture(new CompetitionResult(null, lastResult == null ? JoinResult.NOT_JOINABLE : lastResult));
        }

        if (this.plugin.getMainConfig().isRandomizedArenaJoin()) {
            competitions = new ArrayList<>(competitions);
            Collections.shuffle(competitions);
        }

        // Select the competition with the most number of players
        Competition<?> competition = competitions.stream()
                .max(Comparator.comparingInt(Competition::getAlivePlayerCount))
                .orElse(null);

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

    public void addCompetition(Arena arena, Competition<?> competition) {
        this.competitions.computeIfAbsent(arena, k -> new ArrayList<>()).add(competition);
        this.plugin.getServer().getPluginManager().callEvent(new ArenaCreateCompetitionEvent(arena, competition));
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

                // End the victory phase
                liveCompetition.getPhaseManager().end(true);
            } else {
                // No victory phase - just forcefully kick every player
                for (ArenaPlayer player : Set.copyOf(liveCompetition.getPlayers())) {
                    liveCompetition.leave(player, ArenaLeaveEvent.Cause.SHUTDOWN);
                }
            }

            // Remove spectators
            for (ArenaPlayer player : Set.copyOf(liveCompetition.getSpectators())) {
                liveCompetition.leave(player, ArenaLeaveEvent.Cause.SHUTDOWN);
            }

            liveCompetition.destroy();
        }

        competitions.remove(competition);
        if (competition.getMap().getType() == MapType.DYNAMIC && competition.getMap() instanceof LiveCompetitionMap map) {
            this.clearDynamicMap(map);
        }
    }

    public void completeAllActiveCompetitions() {
        for (Map.Entry<Arena, List<Competition<?>>> entry : Map.copyOf(this.competitions).entrySet()) {
            for (Competition<?> competition : List.copyOf(entry.getValue())) {
                this.removeCompetition(entry.getKey(), competition);
            }
        }
    }

    private void clearDynamicMap(LiveCompetitionMap map) {
        if (map.getType() != MapType.DYNAMIC) {
            return;
        }

        Bukkit.unloadWorld(map.getWorld(), false);
        if (!map.getWorld().getWorldFolder().exists()) {
            return;
        }

        try {
            try (Stream<Path> pathsToDelete = Files.walk(map.getWorld().getWorldFolder().toPath())) {
                for (Path path : pathsToDelete.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        } catch (IOException e) {
            this.plugin.error("Failed to delete dynamic map {}", map.getName(), e);
        }
    }
}
