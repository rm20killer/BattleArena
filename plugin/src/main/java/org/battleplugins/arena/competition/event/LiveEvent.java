package org.battleplugins.arena.competition.event;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;

/**
 * Represents an event which is live on this server.
 */
public class LiveEvent extends LiveCompetition<Event> implements Event {

    public LiveEvent(Arena arena, LiveCompetitionMap map) {
        super(arena, map);
    }
}
