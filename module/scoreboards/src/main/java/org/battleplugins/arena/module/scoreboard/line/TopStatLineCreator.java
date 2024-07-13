package org.battleplugins.arena.module.scoreboard.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.util.Version;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class TopStatLineCreator implements ScoreboardLineCreator {

    @ArenaOption(name = "max-entries", description = "The maximum number of entries to display on the scoreboard.", required = true)
    private int maxEntries;

    @ArenaOption(name = "stat", description = "The stat to display on the scoreboard.", required = true)
    private String stat;

    @ArenaOption(name = "stat-color", description = "The color of the stat.")
    private Color color;

    @ArenaOption(name = "ascending", description = "Whether to display the stat in ascending order.")
    private boolean ascending;

    @ArenaOption(name = "show-team-color", description = "Whether to show the team color of the player.")
    private boolean showTeamColor = true;

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
        List<ArenaPlayer> players = player.getCompetition().getPlayers()
                .stream()
                .sorted((player1, player2) -> {
                    int value1 = statOrDefault(player1, (ArenaStat<Number>) stat).intValue();
                    int value2 = statOrDefault(player2, (ArenaStat<Number>) stat).intValue();
                    return this.ascending ? Integer.compare(value1, value2) : Integer.compare(value2, value1);
                })
                .limit(this.maxEntries)
                .toList();

        for (ArenaPlayer arenaPlayer : players) {
            Component component = Component.text(arenaPlayer.getPlayer().getName());
            if (this.showTeamColor && arenaPlayer.getTeam() != null) {
                TextColor color = arenaPlayer.getTeam().getTextColor();
                if (Version.getServerVersion().isLessThan("1.20.4")) {
                    color = NamedTextColor.nearestTo(color);
                }
                component = component.color(color);
            } else {
                component = component.color(NamedTextColor.WHITE);
            }

            TextColor statColor = this.color == null ? NamedTextColor.WHITE : TextColor.color(this.color.getRGB());
            lines.add(Component.text("(" + statOrDefault(arenaPlayer, (ArenaStat<Number>) stat) + ") ", statColor).append(component));
        }

        return lines;
    }

    private static Number statOrDefault(ArenaPlayer player, ArenaStat<Number> stat) {
        return player.stat(stat).orElse(stat.getDefaultValue());
    }
}
