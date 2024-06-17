package org.battleplugins.arena.competition.match;

import org.battleplugins.arena.Arena;
import org.battleplugins.arena.competition.LiveCompetition;
import org.battleplugins.arena.competition.map.LiveCompetitionMap;

public class LiveMatch extends LiveCompetition<Match> implements Match {

    public LiveMatch(Arena arena, LiveCompetitionMap<Match> map) {
        super(arena, map);
    }
}
