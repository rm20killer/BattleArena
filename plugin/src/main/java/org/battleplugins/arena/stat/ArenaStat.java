package org.battleplugins.arena.stat;

/**
 * Represents a stat that can be tracked.
 *
 * @param <T> the type of the stat
 */
public interface ArenaStat<T> {

    /**
     * Gets the key of the stat.
     *
     * @return the name of the stat
     */
    String getKey();

    /**
     * Gets the name of the stat.
     *
     * @return the name of the stat
     */
    String getName();

    /**
     * Gets the default value of the stat.
     *
     * @return the default value of the stat
     */
    T getDefaultValue();

    /**
     * Gets the type of the stat.
     *
     * @return the type of the stat
     */
    Class<T> getType();
}
