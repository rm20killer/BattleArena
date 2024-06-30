package org.battleplugins.arena.competition.map;

/**
 * A map for a competition.
 */
public interface CompetitionMap {

    /**
     * Gets the name of the map.
     *
     * @return the name of the map
     */
    String getName();

    /**
     * Gets the type of map.
     *
     * @return the type of map
     */
    MapType getType();
}
