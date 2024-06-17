package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;

import java.util.Map;

public class LeaveAction extends EventAction {

    public LeaveAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
        arenaPlayer.getCompetition().leave(arenaPlayer);
    }
}
