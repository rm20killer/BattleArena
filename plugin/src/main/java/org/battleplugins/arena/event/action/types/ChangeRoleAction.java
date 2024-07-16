package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.PlayerRole;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.player.ArenaJoinEvent;
import org.battleplugins.arena.event.player.ArenaSpectateEvent;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Locale;
import java.util.Map;

public class ChangeRoleAction extends EventAction {
    private static final String ROLE_KEY = "role";

    public ChangeRoleAction(Map<String, String> params) {
        super(params, ROLE_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        PlayerRole role = PlayerRole.valueOf(this.get(ROLE_KEY).toUpperCase(Locale.ROOT));
        boolean changedRole = arenaPlayer.getRole() != role;
        arenaPlayer.getCompetition().changeRole(arenaPlayer, role);

        if (changedRole && role == PlayerRole.SPECTATING) {
            arenaPlayer.getCompetition().getTeamManager().leaveTeam(arenaPlayer);
            arenaPlayer.getArena().getEventManager().callEvent(new ArenaSpectateEvent(arenaPlayer));
        } else if (changedRole && role == PlayerRole.PLAYING) {
            arenaPlayer.getCompetition().findAndJoinTeamIfApplicable(arenaPlayer);
            arenaPlayer.getArena().getEventManager().callEvent(new ArenaJoinEvent(arenaPlayer));
        }
    }
}
