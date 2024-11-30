package org.battleplugins.arena.competition.phase.phases;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.phase.LiveCompetitionPhase;
import org.battleplugins.arena.event.ArenaEventHandler;
import org.battleplugins.arena.event.player.ArenaJoinEvent;
import org.battleplugins.arena.options.Teams;
import org.battleplugins.arena.util.IntRange;


public class WaitingForPlayersPhase<T extends LiveCompetition<T>> extends LiveCompetitionPhase<T> {
    @Override
    public void onStart() {
    }

    @Override
    public void onComplete() {
    }

    @ArenaEventHandler
    public void onJoin(ArenaJoinEvent event) {
        if (this.hasEnoughPlayersToStart()) {
            this.advanceToNextPhase();
        }
    }

    public boolean hasEnoughPlayersToStart() {
        Teams teams = this.competition.getArena().getTeams();
        IntRange teamAmount = teams.getTeamAmount();

        int maxPlayer = teamAmount.getMax();
        return this.competition.getPlayers().size() >= maxPlayer;
    }
}
