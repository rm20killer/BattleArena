package org.battleplugins.arena.event;

import org.bukkit.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an event trigger for an {@link ArenaEvent}.
 * <p>
 * Event triggers are implemented over an {@link ArenaEvent}
 * and allow for actions to be called through config files
 * using the {@link #value()} of the event trigger.
 * <p>
 * Event triggers are processed when you call the event this
 * is annotated over through the {@link ArenaEventManager#callEvent(Event)}
 * method. Keep in mind that the event must also have a relevant
 * {@link ArenaEventType} created, which can be done using the
 * {@link ArenaEventType#create(String, Class)} method.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTrigger {

    /**
     * Gets the value of the event trigger.
     *
     * @return the value of the event trigger
     */
    String value();
}
