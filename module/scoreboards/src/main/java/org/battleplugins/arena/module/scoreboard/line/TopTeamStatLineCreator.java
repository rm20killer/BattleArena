package org.battleplugins.arena.module.scoreboard.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.team.TeamManager;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.stat.StatHolder;
import org.battleplugins.arena.team.ArenaTeam;
import org.battleplugins.arena.util.Version;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TopTeamStatLineCreator implements ScoreboardLineCreator {

    @ArenaOption(name = "max-entries", description = "The maximum number of entries to display on the scoreboard.", required = true)
    private int maxEntries;

    @ArenaOption(name = "stat", description = "The stat to display on the scoreboard.", required = true)
    private String stat;

    @ArenaOption(name = "stat-color", description = "The color of the stat.")
    private Color color;

    @ArenaOption(name = "ascending", description = "Whether to display the stat in ascending order.")
    private boolean ascending;

    @SuppressWarnings("unchecked")
    @Override
    public List<Component> createLines(ArenaPlayer player) {
        ArenaStat<?> stat = ArenaStats.get(this.stat);
        if (stat == null) {
            return List.of();
        }

        if (!(stat.getDefaultValue() instanceof Number)) {
            BattleArena.getInstance().warn("Stat {} is not a number. Unsure how to sort players in top stat line type.", stat.getName());
            return List.of();
        }

        List<Component> lines = new ArrayList<>(this.maxEntries);
        TeamManager teamManager = player.getCompetition().getTeamManager();
        List<ArenaTeam> teams = teamManager.getTeams()
                .stream()
                .sorted((team1, team2) -> {
                    int value1 = statOrDefault(teamManager, team1, (ArenaStat<Number>) stat).intValue();
                    int value2 = statOrDefault(teamManager, team2, (ArenaStat<Number>) stat).intValue();
                    return this.ascending ? Integer.compare(value1, value2) : Integer.compare(value2, value1);
                })
                .toList();

        for (ArenaTeam team : teams) {
            if (teamManager.getPlayersOnTeam(team).isEmpty()) {
                continue;
            }

            Component component;
            TextColor color = team.getTextColor();
            if (Version.getServerVersion().isLessThan("1.20.4")) {
                component = Component.text(team.getName(), color);
            } else {
                component = team.getFormattedName();
            }

            TextColor statColor = this.color == null ? NamedTextColor.WHITE : TextColor.color(this.color.getRGB());
            lines.add(Component.text("(" + statOrDefault(teamManager, team, (ArenaStat<Number>) stat) + ") ", statColor).append(component));
        }

        return lines;
    }

    private static Number statOrDefault(TeamManager manager, ArenaTeam team, ArenaStat<Number> stat) {
        StatHolder stats = manager.getStats(team);
        if (stats.stat(stat).isEmpty()) {
            Set<ArenaPlayer> players = manager.getPlayersOnTeam(team);
            int score = 0;
            for (ArenaPlayer teamPlayer : players) {
                score += teamPlayer.stat(stat).orElse(stat.getDefaultValue()).intValue();
            }

            return score;
        } else {
            return stats.stat(stat).orElse(stat.getDefaultValue());
        }
    }
}
