package org.battleplugins.arena.competition.match;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;

/**
 * Represents a match which is live on this server.
 */
public class LiveMatch extends LiveCompetition<Match> implements Match {

    public LiveMatch(Arena arena, LiveCompetitionMap map) {
        super(arena, map);
    }
}
