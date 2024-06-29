package org.battleplugins.arena.event;

import org.apache.commons.lang3.tuple.Pair;
import org.battleplugins.arena.Arena;
import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.BattleArena;
import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionLike;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.event.action.types.DelayAction;
import org.battleplugins.arena.event.arena.ArenaLoseEvent;
import org.battleplugins.arena.event.arena.ArenaVictoryEvent;
import org.battleplugins.arena.event.player.ArenaPlayerEvent;
import org.battleplugins.arena.util.PolymorphicHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class ArenaEventManager {
    private static final Map<Class<? extends Event>, Function<Event, Player>> PLAYER_EVENT_RESOLVERS = new PolymorphicHashMap<>() {
        {
            this.put(PlayerEvent.class, event -> ((PlayerEvent) event).getPlayer());
            this.put(EntityEvent.class, event -> {
                EntityEvent entityEvent = (EntityEvent) event;
                if (entityEvent.getEntity() instanceof Player player) {
                    return player;
                }

                if (entityEvent instanceof EntityDamageByEntityEvent damageEvent) {
                    if (damageEvent.getDamager() instanceof Player player) {
                        return player;
                    }
                }

                return null;
            });
            this.put(BlockBreakEvent.class, event -> ((BlockBreakEvent) event).getPlayer());
            this.put(BlockPlaceEvent.class, event -> ((BlockPlaceEvent) event).getPlayer());
            this.put(BlockCanBuildEvent.class, event -> ((BlockCanBuildEvent) event).getPlayer());
        }
    };

    private final Map<Class<? extends Event>, Function<Event, LiveCompetition<?>>> arenaEventResolvers = new PolymorphicHashMap<>();
    private final Map<Class<? extends ArenaEvent>, Function<ArenaEvent, Set<ArenaPlayer>>> capturedPlayerResolvers = new HashMap<>() {
        {
            this.put(ArenaVictoryEvent.class, event -> ((ArenaVictoryEvent) event).getVictors());
            this.put(ArenaLoseEvent.class, event -> ((ArenaLoseEvent) event).getLosers());
        }
    };

    private final Arena arena;

    public ArenaEventManager(Arena arena) {
        this.arena = arena;
    }

    public void registerArenaResolver(Class<? extends Event> eventClass, Function<Event, LiveCompetition<?>> resolver) {
        this.arenaEventResolvers.put(eventClass, resolver);
    }

    public Arena getArena() {
        return this.arena;
    }

    public <T extends Event & ArenaEvent> T callEvent(T event) {
        Bukkit.getPluginManager().callEvent(event);
        if (event.getEventTrigger() != null) {
            ArenaEventType<?> eventType = ArenaEventType.get(event.getEventTrigger().value());
            if (eventType == null) {
                this.arena.getPlugin().warn("Could not find event type for " + event.getEventTrigger().value());
                return event;
            }

            Competition<?> competition = event.getCompetition();
            Collection<ArenaPlayer> players;
            if (event instanceof ArenaPlayerEvent arenaPlayerEvent) {
                players = List.of(arenaPlayerEvent.getArenaPlayer());
            } else if (this.capturedPlayerResolvers.get(event.getClass()) != null) {
                players = this.capturedPlayerResolvers.get(event.getClass()).apply(event);
            } else if (event.getCompetition() instanceof LiveCompetition<?> liveCompetition) {
                players = liveCompetition.getPlayers();
            } else {
                players = List.of();
            }

            List<EventAction> actions = new ArrayList<>();
            List<EventAction> arenaActions = this.arena.getEventActions().get(eventType);
            if (arenaActions != null) {
                actions.addAll(arenaActions);
            }

            if (competition instanceof LiveCompetition<?> liveCompetition) {
                List<EventAction> competitionActions = liveCompetition.getPhaseManager().getCurrentPhase().getEventActions().get(eventType);
                if (competitionActions != null) {
                    actions.addAll(competitionActions);
                }
            }

            if (actions.isEmpty()) {
                return event;
            }

            this.pollActions(competition, actions.iterator(), players);
        }

        return event;
    }

    private void pollActions(Competition<?> competition, Iterator<EventAction> iterator, Collection<ArenaPlayer> players) {
        while (iterator.hasNext()) {
            EventAction action = iterator.next();
            if (action instanceof DelayAction delayAction) {
                Bukkit.getScheduler().runTaskLater(BattleArena.getInstance(), () -> this.pollActions(competition, iterator, players), delayAction.getTicks());
                return;
            }

            try {
                action.preProcess(this.arena, competition);
            } catch (Throwable e) {
                this.arena.getPlugin().warn("An error occurred pre-processing event action {}", action, e);
                return;
            }

            for (ArenaPlayer player : new HashSet<>(players)) {
                try {
                    action.call(player);
                } catch (Throwable e) {
                    this.arena.getPlugin().warn("An error occurred calling event action {}", action, e);
                    return;
                }
            }

            try {
                action.postProcess(this.arena, competition);
            } catch (Throwable e) {
                this.arena.getPlugin().warn("An error occurred post-processing event action {}", action, e);
                return;
            }
        }
    }

    public void registerEvents(ArenaListener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            method.setAccessible(true);

            // Check if the method is an event handler
            if (!method.isAnnotationPresent(ArenaEventHandler.class)) {
                continue;
            }

            ArenaEventHandler eventHandler = method.getAnnotation(ArenaEventHandler.class);
            if (method.getParameterCount() == 0) {
                continue;
            }

            // The first argument should be an ArenaEvent or a Player event
            Class<?> eventClass = method.getParameterTypes()[0];
            if (!ArenaEvent.class.isAssignableFrom(eventClass) && PLAYER_EVENT_RESOLVERS.get(eventClass) == null && this.arenaEventResolvers.get(eventClass) == null) {
                continue;
            }

            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) eventClass, listener, eventHandler.priority(), (eventListener, event) -> {
                Pair<Arena, Competition<?>> pair = this.extractContext(event);
                if (pair == null) {
                    return;
                }

                Arena arena = pair.getKey();
                Competition<?> competition = pair.getValue();

                // Only call the event if the arena matches
                if (!arena.equals(this.arena)) {
                    return;
                }

                // We are listening in a Competition, so only call the event if the Competition matches
                if (listener instanceof CompetitionLike<?> like && !like.getCompetition().equals(competition)) {
                    return;
                }

                // Ensure args are the same
                if (!eventClass.isAssignableFrom(event.getClass())) {
                    return;
                }

                if (method.getParameterCount() == 1) {
                    try {
                        method.invoke(eventListener, event);
                    } catch (Exception e) {
                        throw new EventException(e, "Error executing ArenaEvent: " + eventClass);
                    }
                } else if (method.getParameterCount() == 2) {
                    // BattleArena offers a bit of flexibility with the second parameter.
                    // Events that are just normal ArenaEvents can have a second parameter be
                    // a Competition, whereas a ArenaPlayerEvent can have a second parameter be
                    // an ArenaPlayer or a Competition.
                    Class<?> parameterType = method.getParameterTypes()[1];
                    if (Competition.class.isAssignableFrom(parameterType)) {
                        try {
                            method.invoke(eventListener, event, competition);
                        } catch (Exception e) {
                            throw new EventException(e, "Error executing ArenaEvent: " + eventClass);
                        }
                    } else if (ArenaPlayer.class.isAssignableFrom(parameterType)) {
                        if (ArenaPlayerEvent.class.isAssignableFrom(eventClass)) {
                            try {
                                method.invoke(eventListener, event, ((ArenaPlayerEvent) event).getArenaPlayer());
                            } catch (Exception e) {
                                throw new EventException(e, "Error executing ArenaPlayerEvent: " + eventClass);
                            }
                        }

                        Function<Event, Player> eventPlayerFunction = PLAYER_EVENT_RESOLVERS.get(eventClass);
                        if (eventPlayerFunction == null) {
                            this.arena.getPlugin().warn("Could not find event player function for event {}", eventClass);
                            return;
                        }

                        Player player = eventPlayerFunction.apply(event);
                        if (player == null) {
                            this.arena.getPlugin().warn("Could not find player for event {}", eventClass);
                            return;
                        }

                        ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
                        if (arenaPlayer == null) {
                            this.arena.getPlugin().warn("Could not find ArenaPlayer for event {}", eventClass);
                            return;
                        }

                        try {
                            method.invoke(eventListener, event, arenaPlayer);
                        } catch (Exception e) {
                            throw new EventException(e, "Error executing ArenaPlayerEvent: " + eventClass);
                        }
                    }
                }
            }, BattleArena.getInstance(), eventHandler.ignoreCancelled());
        }
    }

    public void unregisterEvents(ArenaListener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Nullable
    private Pair<Arena, Competition<?>> extractContext(Event event) {
        if (event instanceof ArenaEvent arenaEvent) {
            return Pair.of(arenaEvent.getArena(), arenaEvent.getCompetition());
        }

        Function<Event, Player> eventPlayerFunction = PLAYER_EVENT_RESOLVERS.get(event.getClass());
        if (eventPlayerFunction != null) {
            Player player = eventPlayerFunction.apply(event);
            if (player == null) {
                return null;
            }

            ArenaPlayer arenaPlayer = ArenaPlayer.getArenaPlayer(player);
            if (arenaPlayer == null) {
                return null;
            }

            return Pair.of(arenaPlayer.getArena(), arenaPlayer.getCompetition());
        }

        Function<Event, LiveCompetition<?>> eventArenaFunction = this.arenaEventResolvers.get(event.getClass());
        if (eventArenaFunction != null) {
            LiveCompetition<?> competition = eventArenaFunction.apply(event);
            if (competition == null) {
                return null;
            }

            return Pair.of(competition.getArena(), competition);
        }

        return null;
    }
}
