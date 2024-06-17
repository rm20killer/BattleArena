package org.battleplugins.arena.competition.victory.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.VictoryCondition;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.player.ArenaDeathEvent;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.team.ArenaTeam;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamsAliveCondition<T extends LiveCompetition<T>> extends VictoryCondition<T> {

    @ArenaOption(name = "amount", description = "The amount of teams that must be alive for this condition to be met.", required = true)
    private int amount;

    private boolean active;

    @ArenaEventHandler
    public void onDeath(ArenaDeathEvent event) {
        this.checkTeamsAlive();
    }

    @ArenaEventHandler
    public void onLeave(ArenaLeaveEvent event) {
        this.checkTeamsAlive();
    }

    @Override
    public void onStart() {
        this.active = true;
    }

    @Override
    public void onEnd() {
        this.active = false;
    }

    @Override
    public Set<ArenaPlayer> identifyPotentialVictors() {
        AliveTeamsResult aliveTeams = this.getAliveTeams();
        if (aliveTeams.aliveTeams() <= this.amount) {
            return aliveTeams.players();
        }

        return Set.of();
    }

    private void checkTeamsAlive() {
        if (!this.active) {
            return;
        }

        Set<ArenaPlayer> victors = this.identifyPotentialVictors();
        if (!victors.isEmpty()) {
            this.advanceToNextPhase(victors);
        }

        // If the game has no players, just end the game
        if (this.competition.getPlayers().isEmpty()) {
            this.advanceToNextPhase(Set.of());
        }
    }

    public AliveTeamsResult getAliveTeams() {
        // If this is a non-team game, then we can just check the
        // deaths on the number of players instead
        Arena arena = this.competition.getArena();
        boolean livesEnabled = arena.getLives() != null && arena.getLives().isEnabled();
        if (arena.getTeams().isNonTeamGame()) {
            Set<ArenaPlayer> alivePlayers = new HashSet<>();
            int aliveTeams = 0;
            for (ArenaPlayer player : this.competition.getPlayers()) {
                int deaths = player.stat(ArenaStats.DEATHS).orElse(0);
                if ((livesEnabled && deaths >= arena.getLives().getLives()) || (!livesEnabled && deaths > 0)) {
                    continue;
                }

                alivePlayers.add(player);
                aliveTeams++;
            }

            return new AliveTeamsResult(alivePlayers, aliveTeams);
        }

        Set<ArenaTeam> aliveTeams = new HashSet<>();
        for (ArenaPlayer player : this.competition.getPlayers()) {
            int deaths = player.stat(ArenaStats.DEATHS).orElse(0);
            if ((livesEnabled && deaths >= arena.getLives().getLives()) || (!livesEnabled && deaths > 0)) {
                continue;
            }

            aliveTeams.add(player.getTeam());
        }

        return new AliveTeamsResult(
                aliveTeams.stream()
                        .flatMap(team -> this.competition.getTeamManager()
                                .getPlayersOnTeam(team)
                                .stream()
                        )
                        .collect(Collectors.toSet()),
                aliveTeams.size()
        );
    }

    public record AliveTeamsResult(Set<ArenaPlayer> players, int aliveTeams) {
    }
}
