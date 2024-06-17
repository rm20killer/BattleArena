package org.battleplugins.arena.competition.map;

import org.battleplugins.arena.competition.Competition;
import org.battleplugins.arena.competition.CompetitionType;

/**
 * A map for a competition.
 */
public interface CompetitionMap<T extends Competition<T>> {

    /**
     * Gets the name of the map.
     *
     * @return the name of the map
     */
    String getName();

    /**
     * Gets the competition this map is for.
     *
     * @return the competition this map is for
     */
    CompetitionType<T> getCompetitionType();

    /**
     * Gets the type of map.
     *
     * @return the type of map
     */
    MapType getType();
}
