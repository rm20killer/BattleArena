package org.battleplugins.arena.competition.event;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;

public class LiveEvent extends LiveCompetition<Event> implements Event {

    public LiveEvent(Arena arena, LiveCompetitionMap<Event> map) {
        super(arena, map);
    }
}
