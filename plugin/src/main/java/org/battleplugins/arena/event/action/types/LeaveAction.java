package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.player.ArenaLeaveEvent;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class LeaveAction extends EventAction {

    public LeaveAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        arenaPlayer.getCompetition().leave(arenaPlayer, ArenaLeaveEvent.Cause.GAME);
    }
}
