package org.battleplugins.arena.module.tournaments;

import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;

import java.time.Duration;
import java.util.List;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/tournaments")
public class TournamentConfig {

    @ArenaOption(name = "broadcast-tournament", description = "Whether to broadcast when tournaments start to all players on the server.", required = true)
    private boolean broadcastTournament;

    @ArenaOption(name = "advance-time", description = "How long until each round starts in the tournament after the previous round has ended.", required = true)
    private Duration advanceTime;

    @ArenaOption(name = "commands-on-win", description = "The commands to run when a player wins the tournament.")
    private List<String> commandsOnWin;

    public boolean isBroadcastTournament() {
        return this.broadcastTournament;
    }

    public Duration getAdvanceTime() {
        return this.advanceTime;
    }

    public List<String> getCommandsOnWin() {
        return this.commandsOnWin == null ? List.of() : List.copyOf(this.commandsOnWin);
    }
}
