package org.battleplugins.arena.competition.event;

/**
 * Represents the type of event.
 */
public enum EventType {
    /**
     * An event that is scheduled to occur at a certain time.
     * <p>
     * Scheduled events will consecutively run one after the other,
     * so once an event is done, the next event will start after the
     * scheduled interval.
     */
    SCHEDULED,
    /**
     * An event that is manually started by an administrator or the
     * server.
     */
    MANUAL
}
