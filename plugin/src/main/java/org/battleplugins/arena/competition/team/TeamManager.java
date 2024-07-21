package org.battleplugins.arena.competition.team;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.options.Spawns;
import org.battleplugins.arena.competition.map.options.TeamSpawns;
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

/**
 * Represents the team manager, which manages all the teams in the competition.
 */
public class TeamManager {
    private final LiveCompetition<?> competition;

    private final Map<ArenaTeam, Set<ArenaPlayer>> teams = new HashMap<>();
    private final Map<ArenaTeam, TeamStatHolder> stats = new HashMap<>();

    public TeamManager(LiveCompetition<?> competition) {
        this.competition = competition;

        if (competition.getArena().getTeams().isNonTeamGame() || (competition.getMap().getSpawns() == null || competition.getMap().getSpawns().getTeamSpawns() == null)) {
            for (ArenaTeam availableTeam : competition.getArena().getTeams().getAvailableTeams()) {
                this.teams.put(availableTeam, new HashSet<>());
            }

            return;
        }

        for (Map.Entry<String, TeamSpawns> entry : competition.getMap().getSpawns().getTeamSpawns().entrySet()) {
            String teamName = entry.getKey();
            ArenaTeam team = BattleArena.getInstance().getTeams().getTeam(teamName);
            if (team == null) {
                BattleArena.getInstance().warn("Could not find team with name {} when loading {} for {}!", teamName, competition.getMap().getName(), competition.getArena().getName());
                continue;
            }

            this.teams.put(team, new HashSet<>());
        }
    }

    /**
     * Joins the given {@link ArenaPlayer} to the specified {@link ArenaTeam}.
     *
     * @param player the player to join
     */
    public void joinTeam(ArenaPlayer player, ArenaTeam team) {
        if (player.getTeam() != null) {
            this.teams.get(player.getTeam()).remove(player);
        }

        this.teams.get(team).add(player);
        player.setTeam(team);
    }

    /**
     * Removes the given {@link ArenaPlayer} from their current team.
     *
     * @param player the player to remove
     */
    public void leaveTeam(ArenaPlayer player) {
        this.leaveTeam(player, player.getTeam());
    }

    /**
     * Removes the given {@link ArenaPlayer} from the specified {@link ArenaTeam}.
     *
     * @param player the player to remove
     * @param team the team to remove the player from
     */
    public void leaveTeam(ArenaPlayer player, ArenaTeam team) {
        Set<ArenaPlayer> players = this.teams.get(team);
        if (players == null) {
            return;
        }

        players.remove(player);
        player.setTeam(null);
    }

    /**
     * Returns the number of players on the given {@link ArenaTeam}.
     *
     * @param team the team to get the number of players from
     * @return the number of players on the team
     */
    public int getNumberOfPlayersOnTeam(ArenaTeam team) {
        Set<ArenaPlayer> players = this.teams.get(team);
        return players == null ? 0 : players.size();
    }

    /**
     * Returns all the players on the given {@link ArenaTeam}.
     *
     * @param team the team to get the players from
     * @return all the players on the team
     */
    public Set<ArenaPlayer> getPlayersOnTeam(ArenaTeam team) {
        return this.teams.getOrDefault(team, Set.of());
    }

    /**
     * Finds a suitable team for the player to join.
     *
     * @return a suitable team for the player to join, or
     *         null if no suitable team is found
     */
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

    /**
     * Returns all the {@link ArenaTeam teams} in the competition.
     *
     * @return all the teams in the competition
     */
    public Set<ArenaTeam> getTeams() {
        return Set.copyOf(this.teams.keySet());
    }

    /**
     * Returns the {@link StatHolder} for the given {@link ArenaTeam}.
     *
     * @param team the team to get the stats for
     * @return the stats for the team
     */
    public StatHolder getStats(ArenaTeam team) {
        return this.stats.computeIfAbsent(team, e -> new TeamStatHolder(this, team));
    }

    /**
     * Returns whether the player can join the given {@link ArenaTeam}.
     *
     * @param team the team to check if the player can join
     * @return whether the player can join the team
     */
    public boolean canJoinTeam(ArenaTeam team) {
        int playersOnTeam = this.getNumberOfPlayersOnTeam(team);
        return playersOnTeam < this.getMaximumTeamSize(team);
    }

    /**
     * Returns the maximum team size for the given {@link ArenaTeam}.
     *
     * @param team the team to get the maximum team size for
     * @return the maximum team size for the team
     */
    public int getMaximumTeamSize(ArenaTeam team) {
        Teams teams = this.competition.getArena().getTeams();
        int teamSizeMax = teams.getTeamSize().getMax();

        // If spawn points are not shared, that means we only have a limited
        // amount of spawn points for each team. We need to check if the team
        // is full based on the amount of spawn points available.
        Spawns spawns = this.competition.getMap().getSpawns();
        if (!teams.isSharedSpawnPoints() && spawns != null) {
            teamSizeMax = Math.min(teamSizeMax, spawns.getSpawnPointCount(team.getName()));
        }

        // If we have zero spawns in any situation, this team cannot hold any players
        if (spawns == null || spawns.getSpawnPointCount(team.getName()) == 0) {
            teamSizeMax = 0;
        }

        return teamSizeMax;
    }

    /**
     * Gets the {@link LiveCompetition} this team manager is managing.
     *
     * @return the competition this team manager is managing
     */
    public LiveCompetition<?> getCompetition() {
        return this.competition;
    }
}
