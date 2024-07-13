package org.battleplugins.arena.module.scoreboard.line;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.options.Lives;
import org.battleplugins.arena.stat.ArenaStats;
import org.battleplugins.arena.util.Version;

import java.util.ArrayList;
import java.util.List;

public class PlayerListLineCreator implements ScoreboardLineCreator {

    @ArenaOption(name = "max-entries", description = "The maximum number of entries to display on the scoreboard.", required = true)
    private int maxEntries;

    @ArenaOption(name = "show-team-color", description = "Whether to show the team color of the player.")
    private boolean showTeamColor = true;

    @ArenaOption(name = "require-alive", description = "Whether to only show alive players.")
    private boolean requireAlive;

    @Override
    public List<Component> createLines(ArenaPlayer player) {
        List<Component> lines = new ArrayList<>(this.maxEntries);
        List<ArenaPlayer> players = player.getCompetition().getPlayers()
                .stream()
                .limit(this.maxEntries)
                .toList();

        if (this.requireAlive) {
            players = players.stream().filter(entry -> {
                Lives lives = entry.getArena().getLives();
                if (lives == null || !lives.isEnabled()) {
                    return entry.stat(ArenaStats.DEATHS).orElse(0) == 0;
                }

                return entry.stat(ArenaStats.LIVES).orElse(0) > 0;
            }).toList();
        }

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

            lines.add(Component.text("- ", NamedTextColor.GRAY).append(component));
        }

        if (this.showTeamColor) {
            // Sort based on team
            lines.sort((line1, line2) -> {
                TextColor color1 = line1.color();
                TextColor color2 = line2.color();
                if (color1 == null || color2 == null) {
                    return 0;
                }

                return color1.compareTo(color2);
            });
        }

        return lines;
    }
}
