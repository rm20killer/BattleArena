package org.battleplugins.arena.options;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.config.DocumentationSource;
import org.battleplugins.arena.config.PostProcessable;
import org.battleplugins.arena.config.Scoped;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.team.ArenaTeams;
import org.battleplugins.arena.util.IntRange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@DocumentationSource("https://docs.battleplugins.org/books/user-guide/page/teams")
public class Teams implements PostProcessable {

    @Scoped
    private Arena arena;

    @ArenaOption(name = "named-teams", description = "Whether named teams are enabled or not.")
    private boolean namedTeams = true;

    @ArenaOption(name = "team-size", description = "The size of each team.", required = true)
    private IntRange teamSize;

    @ArenaOption(name = "team-amount", description = "The amount of teams.", required = true)
    private IntRange teamAmount;

    @ArenaOption(name = "team-selection", description = "The team selection type.")
    private TeamSelection teamSelection = TeamSelection.RANDOM;

    @ArenaOption(name = "shared-spawn-points", description = "Whether spawn points are shared between team members.")
    private boolean sharedSpawnPoints = false;

    private final List<ArenaTeam> availableTeams = new ArrayList<>();

    @Override
    public void postProcess() {
        if (!this.namedTeams && this.teamSize.getMax() == 1) {
            this.teamSelection = TeamSelection.NONE;
            this.availableTeams.add(ArenaTeams.DEFAULT);
            return;
        } else if (!this.namedTeams && this.teamSize.getMax() != 1) {
            this.arena.getPlugin().warn("Ignoring named-teams: false option as team-size is greater than 1!");
        }

        // Check if we have a max team size. If the value is
        // Integer.MAX_VALUE, then we have no max team size, and
        // we can just add all the teams to the available teams list.
        int maxTeams = this.teamAmount.getMax();
        ArenaTeams teams = this.arena.getPlugin().getTeams();
        if (maxTeams == Integer.MAX_VALUE) {
            for (ArenaTeam team : teams) {
                this.availableTeams.add(team);
            }

            return;
        }

        // Otherwise, just add up until the maximum
        Iterator<ArenaTeam> iterator = teams.iterator();
        for (int i = 0; i < maxTeams; i++) {
            if (!iterator.hasNext()) {
                break;
            }

            this.availableTeams.add(iterator.next());
        }
    }

    public boolean isNonTeamGame() {
        return this.teamSelection == TeamSelection.NONE
                && this.availableTeams.size() == 1
                && this.availableTeams.contains(ArenaTeams.DEFAULT);
    }

    public boolean isNamedTeams() {
        return this.namedTeams;
    }

    public IntRange getTeamSize() {
        return this.teamSize;
    }

    public IntRange getTeamAmount() {
        return this.teamAmount;
    }

    public boolean hasMaxPlayers() {
        return this.teamAmount.getMax() != Integer.MAX_VALUE;
    }

    public TeamSelection getTeamSelection() {
        return this.teamSelection;
    }

    public boolean isSharedSpawnPoints() {
        return this.sharedSpawnPoints;
    }

    public List<ArenaTeam> getAvailableTeams() {
        return this.availableTeams;
    }
}
