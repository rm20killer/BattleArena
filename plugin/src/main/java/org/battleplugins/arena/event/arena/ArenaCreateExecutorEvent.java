package org.battleplugins.arena.event.arena;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.command.ArenaCommand;
import org.battleplugins.arena.command.ArenaCommandExecutor;
import org.battleplugins.arena.command.BaseCommandExecutor;
import org.battleplugins.arena.command.SubCommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

/**
 * Called when a {@link ArenaCommandExecutor} is constructed.
 */
public class ArenaCreateExecutorEvent extends Event {
    private final static HandlerList HANDLERS = new HandlerList();

    private final Arena arena;
    private final ArenaCommandExecutor executor;

    public ArenaCreateExecutorEvent(Arena arena, ArenaCommandExecutor executor) {
        this.arena = arena;
        this.executor = executor;
    }

    /**
     * Gets the {@link Arena} this event is occurring in.
     *
     * @return the arena this event is occurring in
     */
    public Arena getArena() {
        return this.arena;
    }

    /**
     * Gets the {@link ArenaCommandExecutor} that is being created.
     *
     * @return the arena command executor that is being created
     */
    public ArenaCommandExecutor getExecutor() {
        return this.executor;
    }

    /**
     * Registers a context to the {@link ArenaCommandExecutor}.
     *
     * @param instance the instance to register
     */
    public void registerSubExecutor(Object instance) {
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ArenaCommand.class)) {
                BaseCommandExecutor.CommandWrapper wrapper = new BaseCommandExecutor.CommandWrapper(instance, method, this.executor.getUsage(method));
                this.executor.injectWrapper(wrapper);
            }
        }

        if (instance instanceof SubCommandExecutor executor) {
            this.executor.injectExecutor(executor);
        }
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
