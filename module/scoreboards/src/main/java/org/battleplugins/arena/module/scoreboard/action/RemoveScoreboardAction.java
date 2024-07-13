package org.battleplugins.arena.module.scoreboard.action;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.module.scoreboard.ScoreboardHandler;
import org.battleplugins.arena.module.scoreboard.Scoreboards;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class RemoveScoreboardAction extends EventAction {

    public RemoveScoreboardAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        if (!arenaPlayer.getArena().isModuleEnabled(Scoreboards.ID)) {
            return;
        }

        Optional<Scoreboards> moduleOpt = arenaPlayer.getArena()
                .getPlugin()
                .<Scoreboards>module(Scoreboards.ID)
                .map(module -> module.initializer(Scoreboards.class));

        // No scoreboard module (should never happen)
        if (moduleOpt.isEmpty()) {
            return;
        }

        ScoreboardHandler previous = arenaPlayer.getMetadata(ScoreboardHandler.class);
        if (previous != null) {
            previous.removeScoreboard();
        }

        arenaPlayer.removeMetadata(ScoreboardHandler.class);
    }
}
