package org.battleplugins.arena.competition.map;

/**
 * Represents the type of map.
 */
public enum MapType {
    /**
     * A map that is always loaded at a defined location.
     */
    STATIC,
    /**
     * A map loaded dynamically. There is no guarantee that
     * the map will be loaded at the same location, or the
     * same world.
     */
    DYNAMIC,
    // /**
    //  * A map that exists on a remote server.
    //  */
    // REMOTE
}
