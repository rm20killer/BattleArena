package org.battleplugins.arena.module.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.module.scoreboard.line.ScoreboardLineCreator;
import org.battleplugins.arena.util.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardHandler {
    private static final ChatColor[] CHAT_COLORS = ChatColor.values();

    private final Scoreboards scoreboards;
    private final ArenaPlayer player;
    private final ScoreboardTemplate template;

    private Scoreboard previousScoreboard;
    private BukkitTask updateTask;

    private List<Component> lastLines = new ArrayList<>();

    public ScoreboardHandler(Scoreboards scoreboards, ArenaPlayer player, ScoreboardTemplate template) {
        this.scoreboards = scoreboards;
        this.player = player;
        this.template = template;
    }

    private List<Component> constructLines() {
        List<Component> lines = new ArrayList<>();
        for (ScoreboardLineCreator creator : this.template.getLines()) {
            lines.addAll(creator.createLines(this.player));
        }

        return lines;
    }

    public Scoreboard createScoreboard() {
        Scoreboard scoreboard = this.player.getPlayer().getScoreboard();
        if (this.scoreboards.getConfig().shouldReplaceScoreboard()) {
            this.previousScoreboard = this.player.getPlayer().getScoreboard();

            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.player.getPlayer().setScoreboard(scoreboard);
        }

        Component title = this.player.resolve().resolveToComponent(this.template.getTitle());
        Objective objective = scoreboard.registerNewObjective("ba_sidebar", Criteria.DUMMY, title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Version serverVersion = Version.getServerVersion();
        // API introduced in 1.20.4
        if (serverVersion.isCompatible("1.20.4")) {
            objective.numberFormat(io.papermc.paper.scoreboard.numbers.NumberFormat.blank());
        }

        List<Component> lines = this.constructLines();
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            String text = LegacyComponentSerializer.legacySection().serialize(line);

            Score score = objective.getScore(entryPrefix(i) + text);
            score.setScore(lines.size() - i);
            // API introduced in 1.20.4
            if (serverVersion.isCompatible("1.20.4")) {
                score.customName(line);
            }
        }

        this.lastLines = lines;
        this.updateTask = Bukkit.getScheduler().runTaskTimer(BattleArena.getInstance(), this::updateScoreboard, 0, this.template.getRefreshTime().toMillis() / 50);
        return scoreboard;
    }

    public void updateScoreboard() {
        Scoreboard scoreboard = this.player.getPlayer().getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            return;
        }

        Component title = this.player.resolve().resolveToComponent(this.template.getTitle());
        objective.displayName(title);

        List<Component> lines = this.constructLines();

        // Line size has not changed - we can run a far more optimized update cycle
        if (this.lastLines.size() == lines.size()) {
            for (int i = 0; i < lines.size(); i++) {
                Component line = lines.get(i);
                Component lastLine = this.lastLines.get(i);
                if (line.equals(lastLine)) {
                    continue;
                }

                String lastText = LegacyComponentSerializer.legacySection().serialize(lastLine);
                scoreboard.resetScores(entryPrefix(i) + lastText);

                String text = LegacyComponentSerializer.legacySection().serialize(line);
                Score score = objective.getScore(entryPrefix(i) + text);
                score.setScore(lines.size() - i);
                // API introduced in 1.20.4
                if (Version.getServerVersion().isCompatible("1.20.4")) {
                    score.customName(line);
                }
            }

            this.lastLines = lines;
            return;
        }

        // Slightly more complicated logic if the line size has changed
        // We need to clear the scoreboard and re-add all the lines
        for (int i = 0; i < this.lastLines.size(); i++) {
            Component line = this.lastLines.get(i);
            String text = LegacyComponentSerializer.legacySection().serialize(line);
            scoreboard.resetScores(entryPrefix(i) + text);
        }

        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            String text = LegacyComponentSerializer.legacySection().serialize(line);

            Score score = objective.getScore(entryPrefix(i) + text);
            score.setScore(lines.size() - i);
            // API introduced in 1.20.4
            if (Version.getServerVersion().isCompatible("1.20.4")) {
                score.customName(line);
            }
        }

        this.lastLines = lines;
    }

    public void removeScoreboard() {
        if (this.updateTask != null) {
            this.updateTask.cancel();
            this.updateTask = null;
        }

        if (this.scoreboards.getConfig().shouldReplaceScoreboard() && this.previousScoreboard != null) {
            this.player.getPlayer().setScoreboard(this.previousScoreboard);
            this.previousScoreboard = null;
        }

        this.player.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    private static String entryPrefix(int index) {
        return CHAT_COLORS[(int) Math.floor(index / 16D)].toString() + CHAT_COLORS[index % 16].toString();
    }
}
