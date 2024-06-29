package org.battleplugins.arena.competition.event;

import net.kyori.adventure.text.Component;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;
import org.battleplugins.arena.competition.map.MapType;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class EventScheduler {
    private final Map<Arena, ScheduledEvent> scheduledEvents = new HashMap<>();
    private final Map<Arena, Competition<?>> activeEvents = new HashMap<>();

    public void scheduleEvent(Arena arena, EventOptions options) {
        ScheduledEvent scheduledEvent = this.scheduledEvents.get(arena);
        if (scheduledEvent != null) {
            scheduledEvent.task().cancel();

            arena.getPlugin().info("An event is already scheduled in arena {}, cancelling the previous event.", arena.getName());
        }

        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskLater(arena.getPlugin(), () -> {
            this.startEvent(arena, options);
        }, options.getInterval().toMillis() / 50);

        this.scheduledEvents.put(arena, new ScheduledEvent(options, bukkitTask));
    }

    public void startEvent(Arena arena, EventOptions options) {
        if (this.activeEvents.containsKey(arena)) {
            arena.getPlugin().warn("An event is already running in arena {}, failed to start!", arena.getName());
            return;
        }

        // Create the competition
        List<LiveCompetitionMap<?>> maps = arena.getPlugin().getMaps(arena);
        if (maps.isEmpty()) {
            arena.getPlugin().warn("No maps found for arena {}, failed to start event!", arena.getName());
            return;
        }

        // Get a random map
        LiveCompetitionMap<?> map = maps.get(ThreadLocalRandom.current().nextInt(maps.size()));
        Competition<?> competition = map.getType() == MapType.DYNAMIC ? map.createDynamicCompetition(arena) : map.createCompetition(arena);

        // Create the competition
        arena.getPlugin().addCompetition(arena, competition);

        this.activeEvents.put(arena, competition);

        // Broadcast that the event has started
        if (options.getMessage() != null && !Component.empty().equals(options.getMessage())) {
            Bukkit.broadcast(options.getMessage());
        }
    }

    public void stopEvent(Arena arena) {
        ScheduledEvent scheduledEvent = this.scheduledEvents.get(arena);
        if (scheduledEvent != null) {
            scheduledEvent.task().cancel();
            this.scheduledEvents.remove(arena);
        }

        Competition<?> competition = this.activeEvents.remove(arena);
        if (competition == null) {
            return;
        }

        arena.getPlugin().removeCompetition(arena, competition);
    }

    public void eventEnded(Arena arena, Competition<?> competition) {
        Competition<?> activeCompetition = this.activeEvents.get(arena);
        if (activeCompetition == null || !activeCompetition.equals(competition)) {
            arena.getPlugin().warn("Event in arena {} has ended, but the competition is not the active competition. Not rescheduling at interval.", arena.getName());
            return;
        }

        this.activeEvents.remove(arena);

        ScheduledEvent scheduledEvent = this.scheduledEvents.get(arena);
        if (scheduledEvent == null) {
            arena.getPlugin().info("No scheduled event found for arena {}. Not rescheduling at interval.", arena.getName());
            return;
        }

        this.scheduledEvents.remove(arena);
        if (scheduledEvent.options().getType() == EventType.MANUAL) {
            arena.getPlugin().info("Event in arena {} has ended. Not rescheduling as event type is manual.", arena.getName());
            return;
        }

        this.scheduleEvent(arena, scheduledEvent.options());
        arena.getPlugin().info("Event in arena {} has ended. Rescheduling event at interval.", arena.getName());
    }

    public void stopAllEvents() {
        for (ScheduledEvent task : this.scheduledEvents.values()) {
            task.task().cancel();
        }

        this.scheduledEvents.clear();
        this.activeEvents.clear();
    }

    public Set<Arena> getScheduledEvents() {
        return Set.copyOf(this.scheduledEvents.keySet());
    }

    record ScheduledEvent(EventOptions options, BukkitTask task) {
    }
}
