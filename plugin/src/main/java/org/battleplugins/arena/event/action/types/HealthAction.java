package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class HealthAction extends EventAction {
    private static final String HEALTH_KEY = "health";
    private static final String HUNGER_KEY = "hunger";

    public HealthAction(Map<String, String> params) {
        super(params, HEALTH_KEY, HUNGER_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        double health = Double.parseDouble(this.get(HEALTH_KEY));
        int hunger = Integer.parseInt(this.get(HUNGER_KEY));

        arenaPlayer.getPlayer().setHealth(health);
        arenaPlayer.getPlayer().setFoodLevel(hunger);
    }
}
