package org.battleplugins.arena.competition.map;

import org.battleplugins.arena.util.Describable;

/**
 * A map for a competition.
 */
public interface CompetitionMap extends Describable {

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

    @Override
    default String describe() {
        return this.getName();
    }
}
