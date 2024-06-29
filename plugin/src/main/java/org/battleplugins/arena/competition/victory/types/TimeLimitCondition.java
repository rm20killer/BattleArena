package org.battleplugins.arena.competition.victory.types;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.VictoryCondition;
import org.battleplugins.arena.config.ArenaOption;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Set;

public class TimeLimitCondition<T extends LiveCompetition<T>> extends VictoryCondition<T> {

    @ArenaOption(name = "time-limit", description = "How long this condition will run for.", required = true)
    private Duration timeLimit;

    private BukkitTask task;

    @Override
    public void onStart() {
        if (this.task != null) {
            throw new IllegalStateException("Attempted to start a time limit condition that is already running!");
        }

        this.task = this.competition.getArena().getPlugin().getServer().getScheduler().runTaskLater(this.competition.getArena().getPlugin(), () -> {
            this.advanceToNextPhase(Set.of());
        }, this.timeLimit.toMillis() / 50L);
    }

    @Override
    public void onEnd() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
