package org.battleplugins.arena.competition.team;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.options.Teams;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TeamManager {
    private final LiveCompetition<?> competition;

    private final Map<ArenaTeam, Set<ArenaPlayer>> teams = new HashMap<>();
    private final Map<ArenaTeam, TeamStatHolder> stats = new HashMap<>();

    public TeamManager(LiveCompetition<?> competition) {
        this.competition = competition;
    }

    public void joinTeam(ArenaPlayer player, ArenaTeam team) {
        this.teams.computeIfAbsent(team, e -> new HashSet<>()).add(player);
        player.setTeam(team);
    }

    public void leaveTeam(ArenaPlayer player) {
        this.leaveTeam(player, player.getTeam());
    }

    public void leaveTeam(ArenaPlayer player, ArenaTeam team) {
        Set<ArenaPlayer> players = this.teams.get(team);
        if (players == null) {
            return;
        }

        players.remove(player);
        player.setTeam(null);
    }

    public int getNumberOfPlayersOnTeam(ArenaTeam team) {
        Set<ArenaPlayer> players = this.teams.get(team);
        return players == null ? 0 : players.size();
    }

    public Set<ArenaPlayer> getPlayersOnTeam(ArenaTeam team) {
        return this.teams.getOrDefault(team, Set.of());
    }

    @Nullable
    public ArenaTeam findSuitableTeam() {
        Teams teams = this.competition.getArena().getTeams();

        // Get all the available teams in the game
        List<ArenaTeam> availableTeams = new ArrayList<>(teams.getAvailableTeams());

        // Sort based on the amount of players on the team. We want the player
        // to join the team with the least amount of players.
        availableTeams.sort((o1, o2) -> {
            int playersOnTeam1 = this.getNumberOfPlayersOnTeam(o1);
            int playersOnTeam2 = this.getNumberOfPlayersOnTeam(o2);

            return Integer.compare(playersOnTeam1, playersOnTeam2);
        });

        // Find the first team that the player can join
        for (ArenaTeam team : availableTeams) {
            if (this.canJoinTeam(team)) {
                return team;
            }
        }

        // Cannot find a team - return null
        return null;
    }

    public Set<ArenaTeam> getTeams() {
        return Set.copyOf(this.teams.keySet());
    }

    public StatHolder getStats(ArenaTeam team) {
        return this.stats.computeIfAbsent(team, e -> new TeamStatHolder(this, team));
    }

    private boolean canJoinTeam(ArenaTeam team) {
        Teams teams = this.competition.getArena().getTeams();
        int playersOnTeam = this.getNumberOfPlayersOnTeam(team);
        return playersOnTeam + 1 <= teams.getTeamSize().getMax();
    }

    public LiveCompetition<?> getCompetition() {
        return this.competition;
    }
}
