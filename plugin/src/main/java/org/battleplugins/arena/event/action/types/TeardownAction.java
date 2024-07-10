package org.battleplugins.arena.event.action.types;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;

public class TeardownAction extends EventAction {

    public TeardownAction(Map<String, String> params, String... requiredKeys) {
        super(params, requiredKeys);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
    }

    @Override
    public void postProcess(Arena arena, Competition<?> competition, Resolvable resolvable) {
        arena.getPlugin().removeCompetition(arena, competition);
    }
}
