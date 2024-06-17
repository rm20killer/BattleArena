package org.battleplugins.arena.competition.match;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;

public interface Match extends Competition<Match> {

    @Override
    default CompetitionType<Match> getType() {
        return CompetitionType.MATCH;
    }
}
