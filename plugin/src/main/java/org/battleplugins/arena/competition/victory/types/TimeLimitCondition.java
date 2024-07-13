package org.battleplugins.arena.competition.victory.types;

import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.victory.VictoryCondition;
import org.battleplugins.arena.config.ArenaOption;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.battleplugins.arena.util.Util;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.Set;

public class TimeLimitCondition<T extends LiveCompetition<T>> extends VictoryCondition<T> {

    @ArenaOption(name = "time-limit", description = "How long this condition will run for.", required = true)
    private Duration timeLimit;

    private long startTime = -1;
    private BukkitTask task;

    @Override
    public void onStart() {
        if (this.task != null) {
            throw new IllegalStateException("Attempted to start a time limit condition that is already running!");
        }

        this.startTime = System.currentTimeMillis();
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

        this.startTime = -1;
    }

    public Duration getTimeLimit() {
        return this.timeLimit;
    }

    public Duration getTimeRemaining() {
        if (this.startTime == -1) {
            return Duration.ZERO;
        }

        return this.timeLimit.minus(Duration.ofMillis(System.currentTimeMillis() - this.startTime));
    }

    @Override
    public Resolver resolve() {
        return super.resolve().toBuilder()
                .define(ResolverKeys.TIME_REMAINING, ResolverProvider.simple(this.getTimeRemaining(), Util::toTimeString))
                .define(ResolverKeys.TIME_REMAINING_SHORT, ResolverProvider.simple(this.getTimeRemaining(), Util::toTimeStringShort))
                .build();
    }
}
