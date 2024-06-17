package org.battleplugins.arena.event;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * An annotation to mark methods as being {@link Arena} event handler methods.
 * <p>
 * An {@link ArenaEventHandler} deviates from a normal {@link EventHandler} in
 * that it only listens for events that occur within an Arena. This is useful
 * for cases where you want to listen for events that occur within an Arena,
 * rather than globally.
 * <p>
 * In order to use an {@link ArenaEventHandler}, you must first register the
 * event handler with the {@link Arena} using {@link Arena#getEventManager()}.
 * <p>
 * Classes that contain {@link ArenaEventHandler} methods must also implement
 * the {@link ArenaListener} interface, rather than Bukkit's {@link Listener}.
 * This class also supports usage of the normal {@link EventHandler} annotation,
 * <p>
 * It is important to note that the {@link ArenaEventHandler} annotation
 * <b>does not</b> support every single Bukkit event. All {@link PlayerEvent},
 * along with a few other common Bukkit events that contain a {@link Player},
 * are supported.
 * <p>
 * There are two options if you want to listen for an event that is not
 * supported by the {@link ArenaEventHandler} annotation:
 * <ol>
 *     <li>If you have created the event, implement the {@link ArenaEvent}
 *     interface. This will allow you to listen for the event in an Arena.</li>
 *     <li>In your Arena class, run {@link ArenaEventManager#registerArenaResolver(Class, Function)},
 *     and provide a resolver for the event. This will allow you to listen for
 *     the event in an Arena. Keep in mind that you must implement the logic
 *     for extracting a {@link LiveCompetition} yourself.</li>
 * </ol>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ArenaEventHandler {

    /**
     * Define the priority of the event.
     * <p>
     * First priority to the last priority executed:
     * <ol>
     * <li>LOWEST
     * <li>LOW
     * <li>NORMAL
     * <li>HIGH
     * <li>HIGHEST
     * <li>MONITOR
     * </ol>
     *
     * @return the priority
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Define if the handler ignores a cancelled event.
     * <p>
     * If ignoreCancelled is true and the event is cancelled, the method is
     * not called. Otherwise, the method is always called.
     *
     * @return whether cancelled events should be ignored
     */
    boolean ignoreCancelled() default false;
}
