package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.team.TeamManager;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.battleplugins.arena.team.ArenaTeam;

import java.util.Map;

public class JoinRandomTeamAction extends EventAction {
    public JoinRandomTeamAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (arenaPlayer.getTeam() == null) {
            TeamManager teamManager = arenaPlayer.getCompetition().getTeamManager();

            ArenaTeam suitableTeam = teamManager.findSuitableTeam();
            if (suitableTeam == null) {
                arenaPlayer.getArena().getPlugin().warn("A suitable team could not be found for player {}!", arenaPlayer.getPlayer().getName());
                return;
            }

            teamManager.joinTeam(arenaPlayer, suitableTeam);
        }
    }
}
