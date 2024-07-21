package org.battleplugins.arena.module.scoreboard.line;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.ArenaPlayer;

import java.util.List;
import java.util.Map;

public interface ScoreboardLineCreator {
    Map<String, Class<? extends ScoreboardLineCreator>> LINE_CREATORS = Map.of(
            "simple", SimpleLineCreator.class,
            "player-list", PlayerListLineCreator.class,
            "top-stat", TopStatLineCreator.class,
            "top-team-stat", TopTeamStatLineCreator.class
    );

    List<Component> createLines(ArenaPlayer player);
}
