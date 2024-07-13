package org.battleplugins.arena.module.scoreboard.action;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.module.scoreboard.ScoreboardHandler;
import org.battleplugins.arena.module.scoreboard.ScoreboardTemplate;
import org.battleplugins.arena.module.scoreboard.Scoreboards;
import org.battleplugins.arena.resolver.Resolvable;

import java.util.Map;
import java.util.Optional;

public class ApplyScoreboardAction extends EventAction {
    private static final String SCOREBOARD_KEY = "scoreboard";

    public ApplyScoreboardAction(Map<String, String> params) {
        super(params, SCOREBOARD_KEY);
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

        String scoreboardTemplate = this.get(SCOREBOARD_KEY);
        ScoreboardTemplate template = moduleOpt.get().getConfig().getTemplates().get(scoreboardTemplate);
        if (template == null) {
            BattleArena.getInstance().warn("Invalid scoreboard template {} for arena {}. Not applying scoreboard to player.", scoreboardTemplate, arenaPlayer.getArena().getName());
            return;
        }

        ScoreboardHandler previous = arenaPlayer.getMetadata(ScoreboardHandler.class);
        if (previous != null) {
            previous.removeScoreboard();
        }

        ScoreboardHandler handler = new ScoreboardHandler(moduleOpt.get(), arenaPlayer, template);
        handler.createScoreboard();

        arenaPlayer.setMetadata(ScoreboardHandler.class, handler);
    }
}
