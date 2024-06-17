package org.battleplugins.arena.competition.event;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;

public interface Event extends Competition<Event> {

    @Override
    default CompetitionType<Event> getType() {
        return CompetitionType.EVENT;
    }
}
