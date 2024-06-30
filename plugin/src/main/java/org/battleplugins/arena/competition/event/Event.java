package org.battleplugins.arena.competition.event;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;

/**
 * Represents an event competition.
 * <p>
 * An event is a mode that is started based on a certain interval,
 * or when triggered by a server action. These games cannot be joined
 * normally unless the event is active.
 * <p>
 * The bulk of event management logic is handled in the {@link EventScheduler}.
 */
public interface Event extends Competition<Event> {

    @Override
    default CompetitionType<Event> getType() {
        return CompetitionType.EVENT;
    }
}
