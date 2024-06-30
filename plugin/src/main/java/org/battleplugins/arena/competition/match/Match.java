package org.battleplugins.arena.competition.match;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;
import org.battleplugins.arena.competition.event.EventScheduler;

/**
 * Represents a match competition.
 * <p>
 * A match is a mode that is started when a certain condition is met (i.e. number of players),
 * or is always active. These games can be joined at any time, as long as there are available
 * maps.
 * <p>
 * The bulk of match management logic is handled by the plugin and manual intervention in terms
 * of starting the match is not required.
 */
public interface Match extends Competition<Match> {

    @Override
    default CompetitionType<Match> getType() {
        return CompetitionType.MATCH;
    }
}
