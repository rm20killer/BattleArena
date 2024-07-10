package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class FlightAction extends EventAction {
    private static final String FLIGHT_KEY = "enabled";

    public FlightAction(Map<String, String> params) {
        super(params, FLIGHT_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        boolean enabled = Boolean.parseBoolean(this.get(FLIGHT_KEY));

        arenaPlayer.getPlayer().setAllowFlight(enabled);
    }
}
