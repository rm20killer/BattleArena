package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;

import java.util.Map;

public class DelayAction extends EventAction {
    private static final String TICKS_KEY = "ticks";

    public DelayAction(Map<String, String> params) {
        super(params, TICKS_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer) {
        // No-op; this is simply used to mark when a delay
        // should occur between other actions
    }

    public int getTicks() {
        return Integer.parseInt(this.get(TICKS_KEY));
    }
}
