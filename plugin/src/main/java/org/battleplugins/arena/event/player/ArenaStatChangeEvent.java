package org.battleplugins.arena.event.player;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.ArenaEvent;
import org.battleplugins.arena.event.EventTrigger;
import org.battleplugins.arena.resolver.Resolver;
import org.battleplugins.arena.resolver.ResolverKeys;
import org.battleplugins.arena.resolver.ResolverProvider;
import org.battleplugins.arena.stat.ArenaStat;
import org.battleplugins.arena.stat.StatHolder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a stat changes.
 */
@EventTrigger("on-stat-change")
public class ArenaStatChangeEvent<T> extends Event implements ArenaEvent {
    private final static HandlerList HANDLERS = new HandlerList();

    private final LiveCompetition<?> competition;
    private final StatHolder statHolder;
    private final ArenaStat<T> stat;
    private final T oldValue;
    private final T newValue;

    public ArenaStatChangeEvent(LiveCompetition<?> competition, StatHolder statHolder, ArenaStat<T> stat, T oldValue, T newValue) {
        this.competition = competition;
        this.statHolder = statHolder;
        this.stat = stat;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Returns the {@link StatHolder} that holds the stat.
     *
     * @return the stat holder
     */
    public StatHolder getStatHolder() {
        return this.statHolder;
    }

    /**
     * Returns the {@link ArenaStat} that was changed.
     *
     * @return the stat that was changed
     */
    public ArenaStat<T> getStat() {
        return this.stat;
    }

    /**
     * Returns the old value of the stat.
     *
     * @return the old value of the stat
     */
    @Nullable
    public T getOldValue() {
        return this.oldValue;
    }

    /**
     * Returns the new value of the stat.
     *
     * @return the new value of the stat
     */
    public T getNewValue() {
        return this.newValue;
    }

    @Override
    public Arena getArena() {
        return this.competition.getArena();
    }

    @Override
    public Competition<?> getCompetition() {
        return this.competition;
    }

    @Override
    public Resolver resolve() {
        return ArenaEvent.super.resolve().toBuilder()
                .define(ResolverKeys.STAT, ResolverProvider.simple(this.stat, ArenaStat::getName))
                .define(ResolverKeys.STAT_HOLDER, ResolverProvider.simple(this.statHolder, StatHolder::describe))
                .define(ResolverKeys.OLD_STAT_VALUE, ResolverProvider.simple(this.oldValue, Object::toString))
                .define(ResolverKeys.NEW_STAT_VALUE, ResolverProvider.simple(this.newValue, Object::toString))
                .build();
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
