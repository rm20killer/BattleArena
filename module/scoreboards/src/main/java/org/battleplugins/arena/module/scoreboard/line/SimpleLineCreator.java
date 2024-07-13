package org.battleplugins.arena.module.scoreboard.line;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ArenaOption;

import java.util.List;

public class SimpleLineCreator implements ScoreboardLineCreator {

    @ArenaOption(name = "lines", description = "The lines to display on the scoreboard.", required = true)
    private List<Component> lines;

    @Override
    public List<Component> createLines(ArenaPlayer player) {
        return this.lines.stream()
                .map(player.resolve()::resolveToComponent)
                .toList();
    }
}
