package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.team.TeamManager;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class JoinRandomTeamAction extends EventAction {
    public JoinRandomTeamAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (arenaPlayer.getTeam() == null) {
            TeamManager teamManager = arenaPlayer.getCompetition().getTeamManager();
            teamManager.joinTeam(arenaPlayer, teamManager.findSuitableTeam());
        }
    }
}
