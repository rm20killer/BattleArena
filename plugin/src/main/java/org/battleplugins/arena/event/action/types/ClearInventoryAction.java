package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;

import java.util.Map;

public class ClearInventoryAction extends EventAction {

    public ClearInventoryAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
        arenaPlayer.getPlayer().getInventory().clear();
    }
}
