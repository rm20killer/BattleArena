package org.battleplugins.arena.module.scoreboard;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.module.scoreboard.config.ScoreboardLineCreatorContextProvider;
import org.battleplugins.arena.module.scoreboard.line.ScoreboardLineCreator;

import java.time.Duration;
import java.util.List;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/scoreboards")
public class ScoreboardTemplate {

    @ArenaOption(name = "title", description = "The title of the scoreboard.", required = true)
    private Component title;

    @ArenaOption(name = "refresh-time", description = "The refresh time of the scoreboard.", required = true)
    private Duration refreshTime;

    @ArenaOption(
            name = "lines",
            description = "The lines to display on the scoreboard.",
            contextProvider = ScoreboardLineCreatorContextProvider.class,
            required = true
    )
    private List<ScoreboardLineCreator> lines;

    public Component getTitle() {
        return this.title;
    }

    public Duration getRefreshTime() {
        return this.refreshTime;
    }

    public List<ScoreboardLineCreator> getLines() {
        return List.copyOf(this.lines);
    }
}
